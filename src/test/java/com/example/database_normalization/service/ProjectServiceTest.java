package com.example.database_normalization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.database_normalization.dto.ProjectRequest;
import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.entity.Project;
import com.example.database_normalization.entity.Team;
import com.example.database_normalization.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamService teamService;

    @InjectMocks
    public ProjectService projectService;

    @Test
    void getAllProjects_delegatesToRepositoryAndReturnsResult() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Mobile App v1");
        project.setBudget(new BigDecimal("500000.00"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(project), pageable, 1);

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);

        Page<ProjectResponse> result = projectService.getAllProjects(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Mobile App v1");
        verify(projectRepository).findAll(pageable);
    }

    @Test
    void getProjectById_whenProjectExists_returnsProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("new project");
        project.setBudget(new BigDecimal("50000.00"));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<ProjectResponse> result = projectService.getProjectById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("new project");
        verify(projectRepository).findById(1L);
    }

    @Test
    void getProjectById_whenProjectDoesNotExist_returnsEmptyOptional() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = projectService.getProjectById(99L);

        assertThat(result).isEmpty();
        verify(projectRepository).findById(99L);
    }

    @Test
    void createProject_savesAndReturnsProject() {
        ProjectRequest request = new ProjectRequest("New project", new BigDecimal("5000.00"), 1L);
        Team team = new Team("Engineering");
        team.setId(1L);

        when(teamService.getTeamOrThrow(1L)).thenReturn(team);
        doNothing().when(teamService).checkMembership(team);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0);
            savedProject.setId(1L);
            return savedProject;
        });

        ProjectResponse result = projectService.createProject(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("New project");
        assertThat(result.budget()).isEqualTo("5000.00");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void updateProject_whenProjectExists_updatesAndReturnsProject() {
        Team team = new Team("Engineering");
        team.setId(1L);

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setName("Old Name");
        existingProject.setBudget(new BigDecimal("1000.00"));
        existingProject.setTeam(team);

        ProjectRequest request = new ProjectRequest("New Name", new BigDecimal("200.00"), 1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamService.getTeamOrThrow(1L)).thenReturn(team);
        doNothing().when(teamService).checkMembership(team);
        when(projectRepository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = projectService.updateProject(1L, request);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("New Name");
        assertThat(result.get().budget()).isEqualByComparingTo("200.00");
        verify(projectRepository).save(existingProject);
    }

    @Test
    void updatesProject_whenProjectDoesNotExist_returnsEmptyOptional() {
        ProjectRequest request = new ProjectRequest("New Name", new BigDecimal("2000.00"), 1L);

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = projectService.updateProject(99L, request);

        assertThat(result).isEmpty();
        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_whenProjectExists_deletesAndReturnsTrue() {
        Team team = new Team("Engineering");
        team.setId(1L);

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setTeam(team);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        doNothing().when(teamService).checkMembership(team);

        boolean result = projectService.deleteProject(1L);

        assertThat(result).isTrue();
        verify(projectRepository).deleteById(1L);
    }

    @Test
    void deleteProject_whenProjectDoesNotExist_returnsFalseWihtoutDeleting() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        boolean result = projectService.deleteProject(99L);

        assertThat(result).isFalse();
        verify(projectRepository, never()).deleteById(any());
    }

}
