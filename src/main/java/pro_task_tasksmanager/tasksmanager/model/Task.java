package pro_task_tasksmanager.tasksmanager.model;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "task_id")
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
       
    //Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    
    //Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
