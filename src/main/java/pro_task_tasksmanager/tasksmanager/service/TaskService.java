package pro_task_tasksmanager.tasksmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pro_task_tasksmanager.tasksmanager.model.Task;
import pro_task_tasksmanager.tasksmanager.repository.TaskRepository;
import pro_task_tasksmanager.tasksmanager.repository.UserRepository;

@Service
public class TaskService{

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Optional<List<Task>> getAllTasks(){
        Long userId = getUserId();
        return taskRepository.findByUserId(userId);
    }

    public Optional<Task> getIdTask(Long id) {
        return taskRepository.findById(id);
    }

    public Boolean addTask(Task task) {
        taskRepository.save(task);
        if (taskRepository.existsById(task.getId()))
            return true;
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

    private Long getUserId(){
        Long userId = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                    .get().getId();
        return userId;
    }
}
