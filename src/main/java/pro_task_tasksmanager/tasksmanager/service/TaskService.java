package pro_task_tasksmanager.tasksmanager.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

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
        logger.info("Trying to get user ID from token");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            logger.warn("Failed to get user ID");
            return Optional.empty();
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(" User ID-{} from token successfully retrieved", userId);
        
        // Check CACHE or DB ung get Tasks
        Optional<List<Task>> tasksOptional;

        List<Task> cachedTasks = cacheRepository.get_tasks(userId);
        if (cachedTasks != null) {
            tasksOptional = Optional.of(cachedTasks);
            logger.info("Data retrieved from cache");
        }
        else{
            tasksOptional = taskRepository.findByUserIdOrderByIdAsc(userId);
            logger.info("Data retrieved from DB");
            if (tasksOptional.isPresent()){
                logger.info("Data added in cache");
                cacheRepository.set_tasks(userId, tasksOptional.get());
            }
        }
        
        // Check deleted Tasks
        if (tasksOptional.isPresent()){
            logger.info("Check deleted tasks");
            List<Task> tasks = tasksOptional.get();
            List<TaskResponse> tasksResponse = new ArrayList<>();
            for (Task task : tasks){
                if (task.getIsDeleted()==false)
                    tasksResponse.add(new TaskResponse(task));
            }
            logger.info("Return tasks");
            return Optional.of(tasksResponse);
        }
        else{
            logger.warn("Taskslist in DB is empty");
            return Optional.empty();
        }
    }



    public Optional<TaskResponse> getIdTask(Long id) {

        logger.info("Trying to get user ID from token");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            logger.warn("Failed to get user ID");
            return Optional.empty();
        }
        logger.info("User ID from token successfully retrieved");

        Optional<Task> taskOptional;
        Task cacheTask = cacheRepository.get_task(id);
        if (cacheTask != null){
            taskOptional = Optional.of(cacheTask);
            logger.info("Data retrieved from cache");
        }
        else{
            taskOptional = taskRepository.findById(id);
            logger.info("Data retrieved from DB");
            if (taskOptional.isPresent()){
                cacheRepository.set_task(id, taskOptional.get());
                logger.info("Data added in cache");
            }
        }
        
        if (taskOptional.isPresent()){
            logger.info("Check deleted task");
            Task task = taskOptional.get();
            if (task.getIsDeleted() == false){
                TaskResponse taskResponse = new TaskResponse(task);
                logger.info("Return task");
                return Optional.of(taskResponse);
            }
            else{
                logger.warn("Task is deleted");
                return Optional.empty();
            }
        }
        else{
            logger.warn("Task with ID-{} in DB is not exist", id);
            return Optional.empty();
        }
    }



    public Boolean addTask(TaskRequest taskRequest) {
        logger.info("Trying to get user ID from token");
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null){
            logger.warn("Failed to get user ID");
            return false;
        }
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(" User ID from token successfully retrieved");

        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setDeadline(taskRequest.getDeadline());
        task.setCreatedAt(Date.valueOf(LocalDate.now()));
        task.setStatus("Pending");
        task.setUserId((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        task.setIsDeleted(false);

        taskRepository.save(task);
        logger.info("New task sucessfully saved");

        if (taskRepository.existsById(task.getId())){

            cacheRepository.set_task(task.getId(), task);
            logger.info("Data added in cache");
            cacheRepository.del_tasks(userId);
            logger.info("Clean cache with alt tasks");

            NotificationMessage notification = new NotificationMessage();
            notification.setTaskId(task.getId());
            notification.setTaskName(task.getName());  
            notification.setMessage("User %d created a new Task #%d \"%s\"".formatted(task.getUserId(), task.getId(), task.getName()));
            notification.setUserId(task.getUserId());  
            notification.setCreatedAt(task.getCreatedAt());  
            
            taskProducer.sendTaskCreateNotification(notification);
            logger.info("Notification sent to Kafka");
            return true;
        }
        else{
            logger.warn("The task with the ID of the new task does not exist in the database");
            return false;
        }
    }



    public Boolean delTask(Long id) {
        if (taskRepository.existsById(id)){
            logger.info("Task with ID-{} exist in DB", id);

            Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.info("User ID from token successfully retrieved");

            Task task = taskRepository.findById(id).get();
            logger.info("Task with ID- {} aus DB successfully retrieved", id);

            NotificationMessage notification = new NotificationMessage();
            notification.setTaskId(task.getId());
            notification.setTaskName(task.getName());  
            notification.setMessage("User %d deleted a Task #%d \"%s\"".formatted(task.getUserId(), task.getId(), task.getName()));  
            notification.setUserId(task.getUserId());  
            notification.setCreatedAt(task.getCreatedAt());  

            taskRepository.changeIsDeletedById(id);
            logger.info("The \"Deleted by\" field has been changed");

            cacheRepository.del_task(id);
            cacheRepository.del_tasks(userId);
            logger.info("Clean cache");

            
            taskProducer.sendTaskDeleteNotification(notification);
            logger.info("Notification sent to Kafka");

            return true;
        }
        logger.warn("Task with ID-{} NOT exist in DB", id);
        return false;
    }



    public Boolean changeStatus(Long id) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("User ID from token successfully retrieved");

        if (taskRepository.existsById(id)){
            logger.info("Task with ID-{} exist in DB", id);

            String status = taskRepository.findById(id).get().getStatus();
            logger.info("Task's status from DB successfully retrieved");

            if ("Pending".equals(status)){
                status = "Completed";
            }
            else{
                status = "Pending";
            }
            taskRepository.changeTaskStatusById(id, status);
            logger.info("Task's status successfully changed");

            cacheRepository.del_task(id);
            cacheRepository.del_tasks(userId);
            logger.info("Clean cache");

            return true;
        }
        logger.warn("Task with ID-{} NOT exist in DB", id);
        return false;
    }
}
