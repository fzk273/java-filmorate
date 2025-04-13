package ru.yandex.practicum.filmorate;

import java.util.HashMap;

public class Utils {
    public static long nextId(HashMap<Long, ?> map) {
        long currentUserId = map.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentUserId;
    }
}
