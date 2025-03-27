package tdarwin.sakila.controller;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
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

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("film-controller");

    @Autowired
    private FilmService filmService;

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("title") String title) {
        Span span = tracer.spanBuilder("searchFilms").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Add attributes to the span
            span.setAttribute("title.search", title);
            logger.info("Title searched for: " + title);
            
            List<Film> films = filmService.searchFilmsByTitle(title);
            
            // Add result information to the span
            span.setAttribute("films.count", films.size());
            return films;
        } finally {
            span.end();
        }
    }
}