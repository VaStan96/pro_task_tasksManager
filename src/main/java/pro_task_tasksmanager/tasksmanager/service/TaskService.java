package pro_task_tasksmanager.tasksmanager.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pro_task_tasksmanager.tasksmanager.Kafka.TaskProducer;
import pro_task_tasksmanager.tasksmanager.controller.NotificationMessage;
import pro_task_tasksmanager.tasksmanager.controller.TaskRequest;
import pro_task_tasksmanager.tasksmanager.controller.TaskResponse;
import pro_task_tasksmanager.tasksmanager.model.Task;
import pro_task_tasksmanager.tasksmanager.repository.CacheRepository;
import pro_task_tasksmanager.tasksmanager.repository.TaskRepository;

@Service
public class TaskService{

    private final CacheRepository cacheRepository;
    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;

    public TaskService(CacheRepository cacheRepository, TaskRepository taskRepository, TaskProducer taskProducer){
        this.cacheRepository = cacheRepository;
        this.taskRepository = taskRepository;
        this.taskProducer = taskProducer;
    }

    
    public Optional<List<TaskResponse>> getAllTasks(){
        // get UserID from Token
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return Optional.empty();
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Check CACHE or DB ung get Tasks
        Optional<List<Task>> tasksOptional;

        List<Task> cachedTasks = cacheRepository.get_tasks(userId);
        if (cachedTasks != null) {
            tasksOptional = Optional.of(cachedTasks);
        }
        else{
            tasksOptional = taskRepository.findByUserIdOrderByIdAsc(userId);
            if (tasksOptional.isPresent()){
                cacheRepository.set_tasks(userId, tasksOptional.get());
            }
        }
        
        // Check deleted Tasks
        if (tasksOptional.isPresent()){
            List<Task> tasks = tasksOptional.get();
            List<TaskResponse> tasksResponse = new ArrayList<>();
            for (Task task : tasks){
                if (task.getIsDeleted()==false)
                    tasksResponse.add(new TaskResponse(task));
            }
            return Optional.of(tasksResponse);
        }
        else
            return Optional.empty();
    }



    public Optional<TaskResponse> getIdTask(Long id) {
        
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return Optional.empty();
        }

        Optional<Task> taskOptional;
        Task cacheTask = cacheRepository.get_task(id);
        if (cacheTask != null){
            taskOptional = Optional.of(cacheTask);
        }
        else{
            taskOptional = taskRepository.findById(id);
            if (taskOptional.isPresent()){
                cacheRepository.set_task(id, taskOptional.get());
            }
        }
        
        if (taskOptional.isPresent()){
            Task task = taskOptional.get();
            if (task.getIsDeleted() == false){
                TaskResponse taskResponse = new TaskResponse(task);
                return Optional.of(taskResponse);
            }
            else
                return Optional.empty();
        }
        else
            return Optional.empty();
    }



    public Boolean addTask(TaskRequest taskRequest) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return false;
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setDeadline(taskRequest.getDeadline());
        task.setCreatedAt(Date.valueOf(LocalDate.now()));
        task.setStatus("Pending");
        task.setUserId((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        task.setIsDeleted(false);

        taskRepository.save(task);

        if (taskRepository.existsById(task.getId())){

            cacheRepository.set_task(task.getId(), task);
            cacheRepository.del_tasks(userId);

            NotificationMessage notification = new NotificationMessage();
            notification.setTaskId(task.getId());
            notification.setTaskName(task.getName());  
            notification.setMessage("User %d created a new Task #%d \"%s\"".formatted(task.getUserId(), task.getId(), task.getName()));
            notification.setUserId(task.getUserId());  
            notification.setCreatedAt(task.getCreatedAt());  
            
            taskProducer.sendTaskCreateNotification(notification);
            return true;
        }
        else
            return false;
    }



    public Boolean delTask(Long id) {
        if (taskRepository.existsById(id)){

            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Task task = taskRepository.findById(id).get();

            NotificationMessage notification = new NotificationMessage();
            notification.setTaskId(task.getId());
            notification.setTaskName(task.getName());  
            notification.setMessage("User %d deleted a Task #%d \"%s\"".formatted(task.getUserId(), task.getId(), task.getName()));  
            notification.setUserId(task.getUserId());  
            notification.setCreatedAt(task.getCreatedAt());  

            taskRepository.changeIsDeletedById(id);

            cacheRepository.del_task(id);
            cacheRepository.del_tasks(userId);
            
            taskProducer.sendTaskDeleteNotification(notification);

            return true;
        }
        return false;
    }



    public Boolean changeStatus(Long id) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (taskRepository.existsById(id)){
            String status = taskRepository.findById(id).get().getStatus();
            if ("Pending".equals(status)){
                status = "Completed";
            }
            else{
                status = "Pending";
            }
            taskRepository.changeTaskStatusById(id, status);

            cacheRepository.del_task(id);
            cacheRepository.del_tasks(userId);

            return true;
        }
        return false;
    }
}
