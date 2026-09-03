package com.example.database_normalization.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.database_normalization.dto.AddTeamMemberRequest;
import com.example.database_normalization.dto.ProjectResponse;
import com.example.database_normalization.dto.TeamRequest;
import com.example.database_normalization.dto.TeamResponse;
import com.example.database_normalization.dto.UserResponse;
import com.example.database_normalization.service.ProjectService;
import com.example.database_normalization.service.TeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final ProjectService projectService;

    public TeamController(TeamService teamService, ProjectService projectService) {
        this.teamService = teamService;
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getMyTeams() {
        return ResponseEntity.ok(teamService.getMyTeams());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserResponse>> getMembersForTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamMembers(id));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse createdTeam = teamService.createTeam(request);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long id, @Valid @RequestBody AddTeamMemberRequest request) {
        teamService.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<Page<ProjectResponse>> getProjectsForTeam(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(projectService.getProjectsByTeam(id, pageable));
    }
}
