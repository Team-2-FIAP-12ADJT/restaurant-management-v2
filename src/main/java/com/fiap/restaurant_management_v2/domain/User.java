package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Domain entity for a system user. Pure Java — no framework dependency.
 * Invariants are enforced inside the entity: use {@link #create} for new users
 * and {@link #restore} to rehydrate from a trusted source.
 */
public final class User {

    private static final Pattern EMAIL = Pattern.compile(
        "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
    );

    private static final Pattern TAX_IDENTIFIER_PATTERN = Pattern.compile(
        "^\\d{11}$"
    );

    private final UUID id;
    private final String name;
    private final String email;
    private final String login;
    private final String taxIdentifier;
    private final String password;

    private User(
        UUID id,
        String name,
        String email,
        String login,
        String taxIdentifier,
        String password
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.taxIdentifier = taxIdentifier;
        this.password = password;
    }

    /**
     * Creates a new, validated user with a freshly generated id.
     *
     * @throws InvalidUserException if any invariant is violated
     */
    public static User create(
        String name,
        String email,
        String login,
        String taxIdentifier,
        String password
    ) {
        User user = new User(
            UUID.randomUUID(),
            name,
            email,
            login,
            taxIdentifier,
            password
        );
        user.validate();
        return user;
    }

    /** Rehydrates an existing user from a trusted source (e.g. database mapper) without re-validating. */
    public static User restore(
        UUID id,
        String name,
        String email,
        String login,
        String taxIdentifier,
        String password
    ) {
        return new User(
            Objects.requireNonNull(id, "id"),
            name,
            email,
            login,
            taxIdentifier,
            password
        );
    }

    public static User bind(
        UUID id,
        String name,
        String email,
        String login,
        String taxIdentifier,
        String password
    ) {
        return new User(
            Objects.requireNonNull(id, "id"),
            name,
            email,
            login,
            taxIdentifier,
            password
        );
    }

    /**
     * Validates the mutable detail fields (name, email, login) without requiring a
     * password — used by the update use case, where the password is preserved and
     * never re-supplied. Same invariants as {@link #validate()} minus the password.
     *
     * @throws InvalidUserException if any invariant is violated
     */
    public static void validateDetails(
        String name,
        String email,
        String login,
        String taxIdentifier
    ) {
        if (isBlank(name)) {
            throw new InvalidUserException("Nome inválido");
        }
        if (isBlank(login)) {
            throw new InvalidUserException("Login inválido");
        }
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new InvalidUserException("Email inválido");
        }

        if (
            isBlank(taxIdentifier) ||
            !TAX_IDENTIFIER_PATTERN.matcher(taxIdentifier).matches()
        ) {
            throw new InvalidUserException("CPF inválido");
        }
    }

    private void validate() {
        validateDetails(name, email, login, taxIdentifier);
        if (isBlank(password)) {
            throw new InvalidUserException("Senha inválida");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getTaxIdentifier() {
        return taxIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
