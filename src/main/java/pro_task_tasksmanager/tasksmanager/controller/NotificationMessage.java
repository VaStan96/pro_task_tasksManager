package pro_task_tasksmanager.tasksmanager.controller;

import java.io.Serializable;
import java.sql.Date;

public class NotificationMessage implements Serializable {
    private Long taskId;
    private String taskName;
    private String message;
    private Long userId;
    private Date createdAt;

    public NotificationMessage(Long taskId, String taskName, String message, Long userId, Date createdAt){
        this.taskId = taskId;
        this.taskName = taskName;
        this.message = message;
        this.userId = userId;   
        this.createdAt = createdAt;
    }

    public NotificationMessage() {
        this.taskId = null;
        this.taskName = null;
        this.message = null;
        this.userId = null;
        this.createdAt = null;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
