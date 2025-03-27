package tdarwin.sakila.service;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import tdarwin.sakila.model.Film;
import tdarwin.sakila.repository.FilmRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmService {

    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("film-service");

    @Autowired
    private FilmRepository filmRepository;

    public List<Film> searchFilmsByTitle(String title) {
        Span span = tracer.spanBuilder("searchFilmsByTitle").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("title.query", title);
            
            List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title);

            if (films.isEmpty()) {
                logger.warn("No films found for title: " + title);
                span.setAttribute("films.found", false);
            } else {
                span.setAttribute("films.found", true);
                span.setAttribute("films.count", films.size());
                
                // Add film titles to the span for better visibility
                if (films.size() <= 10) {  // Limit the number of titles to avoid huge spans
                    StringBuilder filmTitles = new StringBuilder();
                    for (Film film : films) {
                        if (filmTitles.length() > 0) {
                            filmTitles.append(", ");
                        }
                        filmTitles.append(film.getTitle());
                    }
                    span.setAttribute("films.titles", filmTitles.toString());
                }
            }

            return films;
        } finally {
            span.end();
        }
    }
}