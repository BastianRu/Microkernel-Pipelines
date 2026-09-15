package co.edu.unicauca.bancopreguntas.service.user;

import java.util.List;

import co.edu.unicauca.bancopreguntas.domain.user.Role;
import co.edu.unicauca.bancopreguntas.domain.user.User;

/**
 * Abstraccion del servicio de usuarios (HU01). La capa de presentacion
 * depende de esta interfaz, no de la implementacion concreta (DIP).
 */
public interface IUserService {

    User register(String login, String fullName, Role role, String rawPassword);

    User login(String login, String rawPassword);

    List<User> listUsers();
}
