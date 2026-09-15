package co.edu.unicauca.bancopreguntas.service.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.edu.unicauca.bancopreguntas.access.user.IUserRepository;
import co.edu.unicauca.bancopreguntas.domain.user.User;

/**
 * Implementacion en memoria de IUserRepository, usada solo en pruebas.
 * Gracias a que UserService depende de la interfaz IUserRepository (DIP),
 * se puede probar toda la logica de negocio sin una base de datos real.
 */
public class FakeUserRepository implements IUserRepository {

    private final Map<String, User> storage = new LinkedHashMap<>();
    private long nextId = 1;

    @Override
    public User save(User user) {
        user.setId(nextId++);
        storage.put(user.getLogin(), user);
        return user;
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(storage.get(login));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public boolean existByLogin(String login) {
        return storage.containsKey(login);
    }

    @Override
    public void update(User user) {
        storage.put(user.getLogin(), user);
    }
}
