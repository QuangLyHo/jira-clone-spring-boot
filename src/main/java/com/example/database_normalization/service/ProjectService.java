package com.example.database_normalization.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.database_normalization.dto.ProjectRequest;
import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.entity.Project;
import com.example.database_normalization.entity.Team;
import com.example.database_normalization.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;

    public ProjectService(ProjectRepository projectRepository, TeamService teamService) {
        this.projectRepository = projectRepository;
        this.teamService = teamService;
    }

    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(ProjectResponse::from);
    }

    public Page<ProjectResponse> getProjectsByTeam(Long teamId, Pageable pageable) {
        Team team = teamService.getTeamOrThrow(teamId);
        teamService.checkMembership(team);

        return projectRepository.findByTeamId(teamId, pageable)
                .map(ProjectResponse::from);
    }

    public Optional<ProjectResponse> getProjectById(Long id) {
        return projectRepository.findById(id).map(ProjectResponse::from);
    }

    public ProjectResponse createProject(ProjectRequest request) {
        Team team = teamService.getTeamOrThrow(request.teamId());
        teamService.checkMembership(team);

        Project project = new Project();

        project.setName(request.name());
        project.setBudget(request.budget());
        project.setTeam(team);

        return ProjectResponse.from(projectRepository.save(project));
    }

    public Optional<ProjectResponse> updateProject(Long id, ProjectRequest request) {
        return projectRepository.findById(id).map(existingProject -> {
            teamService.checkMembership(existingProject.getTeam());

            Team team = teamService.getTeamOrThrow(request.teamId());
            teamService.checkMembership(team);

            existingProject.setName(request.name());
            existingProject.setBudget(request.budget());
            existingProject.setTeam(team);

            return ProjectResponse.from(projectRepository.save(existingProject));
        });
    }

    public boolean deleteProject(Long id) {
        Optional<Project> existingProject = projectRepository.findById(id);

        if (existingProject.isEmpty()) {
            return false;
        }

        teamService.checkMembership(existingProject.get().getTeam());

        projectRepository.deleteById(id);
        return true;
    }
}
