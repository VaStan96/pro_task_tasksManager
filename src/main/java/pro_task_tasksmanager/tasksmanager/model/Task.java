package pro_task_tasksmanager.tasksmanager.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {
    
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    @Id
    @Column(name = "task_id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_name", nullable = false)
    private String name;

    @Column(name = "task_description")
    private String description;

    @Column(name = "task_created_at", nullable = false)
    private Date createdAt;

    @Column(name = "task_deadline")
    private Date deadline;

    @Column(name = "task_status", nullable = false)
    private String status;

    
}
