package pro_task_tasksmanager.tasksmanager.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pro_task_tasksmanager.tasksmanager.service.TaskService;


@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);


    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<?> getAllTasks() {
        logger.info("Request to get all tasks.");
        Optional<List<TaskResponse>> tasks = taskService.getAllTasks();
        
        if (tasks.isPresent()){
            logger.info("Tasks returned successfully");
            return ResponseEntity.ok(tasks);
        }
        else{
            logger.warn("Tasks not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tasks not found.");
        }
    }   

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getIdTask(@PathVariable Long id) {
        logger.info("Request to get task with ID-{}.", id);
        Optional<TaskResponse> taskResponse = taskService.getIdTask(id);

        if (taskResponse.isPresent()){
            logger.info("Task with ID-{} returned successfully", id);
            return ResponseEntity.ok(taskResponse);
        }
        else{
            logger.warn("Task with ID-{} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }   
    }

    @PostMapping()
    public ResponseEntity<?> addTask(@RequestBody TaskRequest taskRequest) {
        logger.info("Request to add task");
        Boolean load = taskService.addTask(taskRequest);
        
        if (load){
            logger.info("Task load successfully");
            return ResponseEntity.ok().body("Task load successfully.");
        }
        else{
            logger.warn("Task not added");
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Task not loaded.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask (@PathVariable Long id) {
        logger.info("Request to delete task with ID-{}.", id);
        Boolean del = taskService.delTask(id);

        if (del){
            logger.info("Task with ID-{} delete successfully", id);
            return ResponseEntity.ok().body("Task delete successfully.");
        }
        else{
            logger.warn("Task with ID-{} not deleted", id);
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Task not deleted.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        logger.info("Request to change status by task with ID-{}.", id);
        Boolean change = taskService.changeStatus(id);
        
        if (change){
            logger.info("Task with ID-{} change successfully", id);
            return ResponseEntity.ok().body("Status change successfully.");
        }
        else{
            logger.warn("Task with ID-{} not changed", id);
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Status not changed.");
        }
    }  
}
