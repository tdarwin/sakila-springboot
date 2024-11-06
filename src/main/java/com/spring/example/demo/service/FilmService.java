package com.spring.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.example.demo.model.Film;
import com.spring.example.demo.repository.FilmRepository;

@Service
public class FilmService {

    Logger logger = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    private FilmRepository filmRepository;

    public List<Film> searchFilmsByTitle(String title) {
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);
        
        if (films.isEmpty()) {
          logger.warn("No films found for title: " + title);
        } else {
          logger.trace(films.toString());
        }

        return films;
    }
}