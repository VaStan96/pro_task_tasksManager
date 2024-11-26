package pro_task_tasksmanager.tasksmanager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro_task_tasksmanager.tasksmanager.model.Task;
import pro_task_tasksmanager.tasksmanager.service.TaskService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<?> getAllTasks() {
        Optional<List<Task>> tasks = taskService.getAllTasks();
        
        if (tasks.isPresent()){
            return ResponseEntity.ok(tasks);
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tasks not found.");
    }   

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getIdTask(@PathVariable Long id) {
        Optional<Task> task = taskService.getIdTask(id);

        if (task.isPresent()){
            return ResponseEntity.ok(task);
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
    }

    @PostMapping()
    public ResponseEntity<?> addTask(@RequestBody Task task) {
        Boolean load = taskService.addTask(task);
        
        if (load){
            return ResponseEntity.ok().body("Task load successfully.");
        }
        else
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Task not loaded.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask (@PathVariable Long id) {
        Boolean del = taskService.delTask(id);

        if (del){
            return ResponseEntity.ok().body("Task delete successfully.");
        }
        else
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body("Task not deleted.");
    }


    

    
}
