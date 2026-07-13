package ru.skypro.homework.service.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.ResponseAnswerRegisterDto;
import ru.skypro.homework.ecxeption.UserCreationException;
import ru.skypro.homework.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.ecxeption.UserAlreadyExistsException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

@Service
@Transactional (readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(UserDetailsManager manager,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository) {
        this.manager = manager;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    /**
     * Выполняет аутентификацию пользователя.
     * <p>
     * Проверяет существование пользователя и корректность введённого пароля.
     * Пароль сравнивается с хэшем из БД через {@link PasswordEncoder#matches}.
     * </p>
     *
     * @param userName имя пользователя (логин)
     * @param password введённый пароль
     * @return {@code true}, если пользователь существует и пароль верный; {@code false} в противном случае
     */

    @Override
    @Transactional
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }


    /**
     * Регистрирует нового пользователя в системе.
     * <p>
     * Выполняет следующие шаги:
     * <ol>
     *     <li>Проверяет, что пользователь с таким именем ещё не существует</li>
     *     <li>Создаёт сущность {@link User} с закодированным паролем</li>
     *     <li>Сохраняет пользователя в БД</li>
     *     <li>Возвращает DTO с ID созданного пользователя</li>
     * </ol>
     * </p>
     *
     * @param register DTO с данными для регистрации
     * @return DTO с ID созданного пользователя
     * @throws UserAlreadyExistsException если пользователь с таким именем уже существует
     * @throws UserCreationException      если не удалось получить ID созданного пользователя
     */

    @Override
    @Transactional
    public ResponseAnswerRegisterDto register(Register register) {
        if (manager.userExists(register.username())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }

        User user = User.builder()
                .username(register.username())
                .password(encoder.encode(register.password()))
                .firstName(register.firstName())
                .lastName(register.lastName())
                .phone(register.phone())
                .role(register.role())
                .build();

        userRepository.save(user);

        Long userId = userRepository.findByUsername(register.username())
                .orElseThrow(() -> new UserCreationException(
                        "Не удалось получить ID созданного пользователя"
                ))
                .getId();

        return new ResponseAnswerRegisterDto(userId);

    }
}
