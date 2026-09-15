package co.edu.unicauca.bancopreguntas.domain.user;

import java.util.Objects;

/**
 * Capa de dominio (HU01 - Gestion de usuarios del sistema).
 *
 * Entidad usuario del sistema. Encapsula sus invariantes basicos: login,
 * nombre, rol, estado y contrasena (ya hasheada) obligatorios.
 */
public class User {

    private Long id;
    private String login;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String hashedPassword;

    public User(String login, String fullName, Role role, UserStatus status, String hashedPassword) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login cannot be null or empty");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("FullName cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("HashedPassword cannot be null or empty");
        }

        this.login = login;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.hashedPassword = hashedPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacio");
        }
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        this.status = status;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVO;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacia");
        }
        this.hashedPassword = hashedPassword;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return this.login.equals(other.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
