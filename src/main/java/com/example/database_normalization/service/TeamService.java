package com.example.database_normalization.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.database_normalization.dto.AddTeamMemberRequest;
import com.example.database_normalization.dto.TeamRequest;
import com.example.database_normalization.dto.TeamResponse;
import com.example.database_normalization.dto.UserResponse;
import com.example.database_normalization.entity.Team;
import com.example.database_normalization.entity.User;
import com.example.database_normalization.repository.TeamRepository;
import com.example.database_normalization.repository.UserRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public List<TeamResponse> getMyTeams() {
        User currentUser = getCurrentUser();

        return teamRepository.findByMembersContaining(currentUser).stream()
                .map(TeamResponse::from)
                .toList();
    }

    public TeamResponse createTeam(TeamRequest request) {
        User currentUser = getCurrentUser();

        Team team = new Team(request.name());
        team.getMembers().add(currentUser);

        return TeamResponse.from(teamRepository.save(team));
    }

    public List<UserResponse> getTeamMembers(Long teamId) {
        Team team = getTeamOrThrow(teamId);
        checkMembership(team);

        return team.getMembers().stream()
                .map(UserResponse::from)
                .toList();
    }

    public void addMember(Long teamId, AddTeamMemberRequest request) {
        Team team = getTeamOrThrow(teamId);
        checkMembership(team);

        User userToAdd = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));

        team.getMembers().add(userToAdd);
        teamRepository.save(team);
    }

    public Team getTeamOrThrow(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
    }

    public void checkMembership(Team team) {
        User currentUser = getCurrentUser();

        if (!team.getMembers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this team");
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email);
    }
}
