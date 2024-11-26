package pro_task_tasksmanager.tasksmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pro_task_tasksmanager.tasksmanager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    Optional<List<Task>> findByUserId(Long userId);
}
