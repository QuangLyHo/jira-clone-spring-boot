package com.example.database_normalization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.database_normalization.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
