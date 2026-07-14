package ru.skypro.homework.service;

import ru.skypro.homework.dto.auth.ResponseAnswerRegisterDto;
import ru.skypro.homework.dto.auth.Register;

public interface AuthService {
    boolean login(String userName, String password);

    ResponseAnswerRegisterDto register(Register register);
}
