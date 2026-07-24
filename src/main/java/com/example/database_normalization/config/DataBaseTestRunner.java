package com.example.database_normalization.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.database_normalization.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Component
public class DataBaseTestRunner implements CommandLineRunner {
    
    // private final UserRepository userRepository;

    // public DataBaseTestRunner(UserRepository userRepository) {
    //     this.userRepository = userRepository;
    // }

    // @Override
    // public void run(String... args) throws Exception {
    //     System.out.println(">>> FETCHING DATA FROM LOCAL MYSQL DATABASE! <<<");

    //     userRepository.findAll().forEach(user -> {
    //         System.out.println("User name: " + user.getFirstName() + " " + user.getLastName());
    //     });
    // }

    private final TaskRepository taskRepository;

    public DataBaseTestRunner(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println(">>> FETCHING TASKS AND MANY-TO-MANY ASSIGNEES <<<");

        taskRepository.findAllWithAssignees().forEach(task -> {
            System.out.println();
            System.out.println("Task: " + task.getTitle());
            System.out.println("Assigned to: ");
            task.getAssignees().forEach(user -> System.out.println(user.getFirstName() + " "));
            System.out.println("\n-------------------------");
        });
    }
}
