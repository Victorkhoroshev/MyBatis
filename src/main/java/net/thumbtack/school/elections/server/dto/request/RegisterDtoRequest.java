package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Voter;
import org.jetbrains.annotations.Nullable;

public class RegisterDtoRequest {
    private final String firstName;
    private final String lastName;
    @Nullable
    private final String patronymic;
    private final String street;
    private final Integer house;
    @Nullable
    private final Integer apartment;
    private final String login;
    private final String password;

    public RegisterDtoRequest(String firstName, String lastName, @Nullable String patronymic, String street, Integer house,
                              @Nullable Integer apartment, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.login = login;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    @Nullable
    public String getPatronymic() {
        return patronymic;
    }

    public String getStreet() {
        return street;
    }

    public Integer getHouse() {
        return house;
    }

    @Nullable
    public Integer getApartment() {
        return apartment;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Voter newVoter() {
        return new Voter(firstName, lastName, patronymic, street, house, apartment, login, password);
    }
}