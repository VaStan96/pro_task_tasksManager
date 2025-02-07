package pro_task_tasksmanager.tasksmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pro_task_tasksmanager.tasksmanager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    Optional<List<Task>> findByUserIdOrderByIdAsc(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.status = :taskStatus WHERE t.id = :taskId")
    void changeTaskStatusById(@Param("taskId") Long taskId, @Param("taskStatus") String taskStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.isDeleted = True WHERE t.id = :taskId")
    void changeIsDeletedById(@Param("taskId") Long taskId);
}
