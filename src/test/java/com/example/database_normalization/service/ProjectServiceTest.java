package com.example.database_normalization.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.entity.Project;
import com.example.database_normalization.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    public ProjectService projectService;

    @Test
    void getAllProjects_delegatesToRepositoryAndReturnsResult() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Mobile App v1");
        project.setBudget(new BigDecimal("500000.00"));

        when(projectRepository.findAll()).thenReturn(List.of(project));
        List<ProjectResponse> result = projectService.getAllProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Mobile App v1");
        verify(projectRepository).findAll();
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
}
