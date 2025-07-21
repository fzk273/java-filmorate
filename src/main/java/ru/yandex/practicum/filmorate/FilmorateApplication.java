package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		//TODO не очень понимаю, почему у меня не работает аннотация Qualifier. пришлось сделать классы с базой Primary и вырубить все тесты. Нужна помощь.
		SpringApplication.run(FilmorateApplication.class, args);
	}
}
