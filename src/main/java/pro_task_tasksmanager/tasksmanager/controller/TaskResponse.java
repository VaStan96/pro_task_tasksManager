package pro_task_tasksmanager.tasksmanager.controller;

import java.sql.Date;

import pro_task_tasksmanager.tasksmanager.model.Task;

public class TaskResponse {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date deadline;
    private String status;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.createdAt = task.getCreatedAt();
        this.deadline = task.getDeadline();
        this.status = task.getStatus();
    }

    public TaskResponse(Long id, String name, String description, Date createdAt, Date deadline, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getDeadline() {
        return deadline;
    }
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }    
}
