package co.edu.unicauca.bancopreguntas.access.user;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.bancopreguntas.domain.user.User;

public interface IUserRepository {

    User save(User user);

    Optional<User> findByLogin(String login);

    List<User> findAll();

    boolean existByLogin(String login);

    void update(User user);
}
