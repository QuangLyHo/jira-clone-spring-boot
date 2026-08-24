package com.example.database_normalization.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(ProjectResponse::from);
    }

    public Optional<ProjectResponse> getProjectById(Long id) {
        return projectRepository.findById(id).map(ProjectResponse::from);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();

        project.setName(request.name());
        project.setBudget(request.budget());
        
        return ProjectResponse.from(projectRepository.save(project));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<ProjectResponse> updateProject(Long id, ProjectRequest request) {
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setName(request.name());
            existingProject.setBudget(request.budget());

            return ProjectResponse.from(projectRepository.save(existingProject));
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteProject(Long id) {
        if (!projectRepository.existsById(id)) return false;

        projectRepository.deleteById(id);
        return true;
    }
}
