package tdarwin.sakila.controller;

import tdarwin.sakila.model.Film;
import tdarwin.sakila.service.FilmService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/films")
public class FilmController {

  Logger logger = LoggerFactory.getLogger(FilmController.class);

  @Autowired
  private FilmService filmService;

  @GetMapping("/search")
  public List<Film> searchFilms(@RequestParam("title") String title) {
    logger.info("Title searched for: " + title);
    return filmService.searchFilmsByTitle(title);
  }
}