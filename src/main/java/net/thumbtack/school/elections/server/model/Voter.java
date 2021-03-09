package net.thumbtack.school.elections.server.model;

import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class Voter extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String firstName;
    private final String lastName;
    private final @Nullable String patronymic;
    private final String street;
    private final Integer house;
    private final @Nullable Integer apartment;
    private boolean isHasOwnCandidate;

    public Voter (String firstName, String lastName, @Nullable String patronymic, String street, Integer house, @Nullable Integer apartment, String login, String password) {
        super(login, password);
        this.firstName = firstName.toLowerCase(Locale.ROOT);
        this.lastName = lastName.toLowerCase(Locale.ROOT);
        //REVU:вынесите эту логику в сеттер
        if( patronymic != null) {
            this.patronymic = patronymic.toLowerCase(Locale.ROOT);
        } else {
            this.patronymic = null;
        }
        this.street = street.toLowerCase(Locale.ROOT);
        this.house = house;
        this.apartment = apartment;
        //REVU: по умолчанию boolean переменная и так равна false, может не имеет смысла ее выставлять
//        setHasOwnCandidate(false);
    }

    public Voter (String firstName, String lastName, String street, Integer house,
                  Integer apartment, String login, String password) {
        this(firstName, lastName,null, street, house, apartment, login, password);
    }

    public Voter (String firstName, String lastName, String patronymic,
                  String street, Integer house, String login, String password) {
        this(firstName, lastName, patronymic, street, house, null,  login, password);
    }

    public Voter (String firstName, String lastName, String street, Integer house, String login, String password) {
        this(firstName, lastName, null, street, house, null, login, password);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isHasOwnCandidate() {
        return isHasOwnCandidate;
    }

    public @Nullable String getPatronymic() {
        return patronymic;
    }

    public String getStreet() {
        return street;
    }

    public Integer getHouse() {
        return house;
    }

    public @Nullable Integer getApartment() {
        return apartment;
    }

    public void setHasOwnCandidate(boolean hasOwnCandidate) {
        isHasOwnCandidate = hasOwnCandidate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voter voter = (Voter) o;
        return Objects.equals(firstName, voter.firstName) && Objects.equals(lastName, voter.lastName) &&
                Objects.equals(patronymic, voter.patronymic) && Objects.equals(street, voter.street) &&
                Objects.equals(house, voter.house) && Objects.equals(apartment, voter.apartment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, patronymic, street, house, apartment);
    }
}