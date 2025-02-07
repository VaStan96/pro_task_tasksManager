package pro_task_tasksmanager.tasksmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TasksmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasksmanagerApplication.class, args);
	}

}
