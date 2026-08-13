package com.example.database_normalization.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.database_normalization.dto.ProjectRequest;
import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.entity.Project;
import com.example.database_normalization.repository.ProjectRepository;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public Optional<ProjectResponse> getProjectById(Long id) {
        return projectRepository.findById(id).map(ProjectResponse::from);
    }

    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();

        project.setName(request.name());
        project.setBudget(request.budget());
        
        return ProjectResponse.from(projectRepository.save(project));
    }

    public Optional<ProjectResponse> updateProject(Long id, ProjectRequest request) {
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setName(request.name());
            existingProject.setBudget(request.budget());

            return ProjectResponse.from(projectRepository.save(existingProject));
        });
    }

    public boolean deleteProject(Long id) {
        if (!projectRepository.existsById(id)) return false;

        projectRepository.deleteById(id);
        return true;
    }
}
