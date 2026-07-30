package com.example.database_normalization.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.database_normalization.entity.Project;
import com.example.database_normalization.repository.ProjectRepository;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> updateProject(Long id, Project projectDetails) {
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setName(projectDetails.getName());
            existingProject.setBudget(projectDetails.getBudget());

            return projectRepository.save(existingProject);
        });
    }

    public boolean deleteProject(Long id) {
        if (!projectRepository.existsById(id)) return false;

        projectRepository.deleteById(id);
        return true;
    }
}
