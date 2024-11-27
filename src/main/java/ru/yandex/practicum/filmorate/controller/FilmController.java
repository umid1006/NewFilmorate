package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private
    final List<Film> films = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(User.class); // Добавляем логгер

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
            // 1. Проверка на наличие ID
            if (film.getId() == 0) { // Предполагаем, что 0 - значение по умолчанию для нового фильма
                throw new ValidationException("ID фильма должен быть указан");
            }
            Film existingFilm = films.stream()
                    .filter(f -> f.getId() == film.getId())
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Фильм с ID " + film.getId() + " не найден"));
            validateFilm(film);
            log.info("Обновлен фильм: {}", existingFilm);
            return existingFilm;
        } catch (ValidationException | ResourceNotFoundException e) {
            log.warn("Ошибка при обновлении фильма: {}", e.getMessage());
            throw e; // Перебрасываем исключение дальше для обработки
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
