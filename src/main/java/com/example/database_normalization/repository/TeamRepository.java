package com.example.database_normalization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.database_normalization.entity.Team;
import com.example.database_normalization.entity.User;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByMembersContaining(User user);
}
