package tdarwin.sakila.repository;

import tdarwin.sakila.model.Film;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
  List<Film> findByTitleContainingIgnoreCase(String title);
}