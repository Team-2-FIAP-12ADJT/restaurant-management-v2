package com.fiap.restaurant_management_v2.domain;

import com.fiap.restaurant_management_v2.domain.exception.InvalidUserTypeException;

import java.util.Objects;
import java.util.UUID;


public final class UserType {

    private final UUID id;
    private final String userType;


    private UserType(UUID id, String userType) {
        this.id = id;
        this.userType = userType;
    }



    public static UserType create(String userType){

        UserType userTypeCreate = new UserType(UUID.randomUUID(), userType);
        userTypeCreate.validate();
        return userTypeCreate;

    }

    public static UserType restore(UUID id , String userType){
        return new UserType(
                Objects.requireNonNull(id),
                userType
        ) ;
    }

    private void validate() {
        if (isBlank(userType)) {
            throw new InvalidUserTypeException("Nome inválido");
        }

    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public UUID getId() {
        return id;
    }
    public String getUserType() {
        return userType;
    }

    public UserType changeType(String newUserType) {
        if (newUserType == null || newUserType.isBlank()) {
            throw new InvalidUserTypeException("O tipo de usuário não pode ser vazio.");
        }
        var update =  new UserType(this.id, newUserType);
        update.validate();
        return  update ;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserType other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
