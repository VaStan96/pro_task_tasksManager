package pro_task_tasksmanager.tasksmanager.repository;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import pro_task_tasksmanager.tasksmanager.model.Task;

@Repository
public class CacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_DURATION = Duration.ofHours(1);
    
    public CacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // get
    public List<Task> get_tasks(Long userId){
        return (List<Task>) redisTemplate.opsForValue().get("Tasks:" + userId);
    }

    public Task get_task(Long taskId){
        return (Task) redisTemplate.opsForValue().get("Task:" + taskId);
    }

    // set 
    public void set_tasks(Long userId, List<Task> tasks){
        redisTemplate.opsForValue().set("Tasks:" + userId, tasks, CACHE_DURATION);
    }

    public void set_task(Long taskId, Task task){
        redisTemplate.opsForValue().set("Task:" + taskId, task, CACHE_DURATION);
    }

    // delete
    public void del_tasks(Long userId){
        redisTemplate.delete("Tasks:" + userId);
    }
    
    public void del_task(Long taskId){
        redisTemplate.delete("Task:" + taskId);
    }
}
