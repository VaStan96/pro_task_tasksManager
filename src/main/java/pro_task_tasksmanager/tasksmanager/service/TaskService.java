package pro_task_tasksmanager.tasksmanager.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pro_task_tasksmanager.tasksmanager.Kafka.TaskProducer;
import pro_task_tasksmanager.tasksmanager.controller.NotificationMessage;
import pro_task_tasksmanager.tasksmanager.controller.TaskRequest;
import pro_task_tasksmanager.tasksmanager.controller.TaskResponse;
import pro_task_tasksmanager.tasksmanager.model.Task;
import pro_task_tasksmanager.tasksmanager.repository.TaskRepository;

@Service
public class TaskService{

    private final TaskRepository taskRepository;
    private final TaskProducer taskProducer;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskProducer taskProducer){
        this.taskRepository = taskRepository;
        this.taskProducer = taskProducer;
    }

    public Optional<List<TaskResponse>> getAllTasks(){
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            return Optional.empty();
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Optional<List<Task>> tasksOptional = taskRepository.findByUserIdOrderByIdAsc(userId);
        
        if (tasksOptional.isPresent()){
            List<Task> tasks = tasksOptional.get();
            List<TaskResponse> tasksResponse = new ArrayList<>();
            for (Task task : tasks){
                tasksResponse.add(new TaskResponse(task));
            }
            return Optional.of(tasksResponse);
        }
        else
            return Optional.empty();
    }

    public Optional<TaskResponse> getIdTask(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        
        if (taskOptional.isPresent()){
            Task task = taskOptional.get();
            TaskResponse taskResponse = new TaskResponse(task);
            return Optional.of(taskResponse);
        }
        else
            return Optional.empty();
    }

    public Boolean addTask(TaskRequest taskRequest) {
        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setDeadline(taskRequest.getDeadline());
        task.setCreatedAt(Date.valueOf(LocalDate.now()));
        task.setStatus("Pending");
        task.setUserId((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        taskRepository.save(task);

        if (taskRepository.existsById(task.getId())){
            NotificationMessage notification = new NotificationMessage();
            notification.setTaskId(task.getId());
            notification.setTaskName(task.getName());  
            notification.setMessage("New task created");  
            notification.setUserId(task.getUserId());  
            notification.setCreatedAt(task.getCreatedAt());  
            
            taskProducer.sendTaskNotification(notification);
            return true;
        }
        else
            return false;
    }

    public Boolean delTask(Long id) {
        if (taskRepository.existsById(id)){
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Boolean changeStatus(Long id) {
        if (taskRepository.existsById(id)){
            String status = taskRepository.findById(id).get().getStatus();
            if ("Pending".equals(status)){
                status = "Completed";
            }
            else{
                status = "Pending";
            }
            taskRepository.changeTaskStatusById(id, status);
            return true;
        }
        return false;
    }
}
