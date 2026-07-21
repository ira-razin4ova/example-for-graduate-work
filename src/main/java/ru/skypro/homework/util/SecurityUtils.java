package ru.skypro.homework.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.model.user.User;

/**
 * Утилитный класс для проверок безопасности.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Проверяет право на изменение ресурса.
     * Разрешает если пользователь — собственник ИЛИ имеет роль ADMIN.
     * При нарушении выбрасывает AccessDeniedException.
     *
     * @param resourceOwner владелец ресурса (Entity из БД)
     * @param currentUser   текущий авторизованный пользователь
     */
    public static void checkModifyPermission(User resourceOwner, UserDetails currentUser) {
        boolean isOwner = resourceOwner.getEmail().equals(currentUser.getUsername());

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Нет прав на изменение данного ресурса");
        }
    }
}