package com.spring.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.example.demo.model.Film;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    List<Film> findByTitleContainingIgnoreCase(String title);
}