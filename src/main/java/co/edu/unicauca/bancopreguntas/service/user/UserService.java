package co.edu.unicauca.bancopreguntas.service.user;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.bancopreguntas.access.user.IUserRepository;
import co.edu.unicauca.bancopreguntas.domain.user.IPasswordHasher;
import co.edu.unicauca.bancopreguntas.domain.user.PasswordPolicy;
import co.edu.unicauca.bancopreguntas.domain.user.Role;
import co.edu.unicauca.bancopreguntas.domain.user.User;
import co.edu.unicauca.bancopreguntas.domain.user.UserStatus;

/**
 * Implementacion de la logica de negocio de usuarios (RF-01, RF-02, RF-03).
 */
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    public UserService(IUserRepository userRepository, IPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User register(String login, String fullName, Role role, String rawPassword) {
        if (userRepository.existByLogin(login)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el login: " + login);
        }

        List<String> passwordErrors = PasswordPolicy.validate(rawPassword);
        if (!passwordErrors.isEmpty()) {
            throw new InvalidPasswordException(passwordErrors);
        }

        String hashedPassword = passwordHasher.hash(rawPassword);
        User newUser = new User(login, fullName, role, UserStatus.ACTIVO, hashedPassword);
        return userRepository.save(newUser);
    }

    @Override
    public User login(String login, String rawPassword) {
        Optional<User> maybeUser = userRepository.findByLogin(login);

        if (maybeUser.isEmpty()) {
            throw new AuthenticationException("Usuario o contrasena incorrectos");
        }

        User user = maybeUser.get();

        if (!user.isActive()) {
            throw new AuthenticationException("El usuario esta inactivo");
        }

        if (!passwordHasher.matches(rawPassword, user.getHashedPassword())) {
            throw new AuthenticationException("Usuario o contrasena incorrectos");
        }

        return user;
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
