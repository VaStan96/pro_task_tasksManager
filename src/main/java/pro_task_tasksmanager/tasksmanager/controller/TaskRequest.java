package pro_task_tasksmanager.tasksmanager.controller;

import java.sql.Date;

public class TaskRequest {
    private String name;
    private String description;
    private Date deadline;


    public TaskRequest(String name, String description, Date deadline) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public Date getDeadline() {
        return deadline;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
