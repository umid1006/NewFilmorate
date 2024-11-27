package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private
    final List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> findAll() {
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film);
            films.add(film);
            log.info("Добавлен новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при создании фильма: {}", e.getMessage());
            throw e; // Перебрасываем исключение дальше
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        try {
            validateFilm(film);
            // Здесь должна быть логика для обновления фильма в списке films
            // Например, поиск фильма по id и замена его на новый
            log.info("Обновлен фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.warn("Ошибка валидации при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription()
                != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть " +
                    "раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть " +
                    "положительным числом.");
        }
    }
}
