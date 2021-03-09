package net.thumbtack.school.elections.server.dto.request;

public class IsCommissionerDtoRequest {
    private String login;
    private String password;

    public IsCommissionerDtoRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean requiredFieldsIsNotNull(){
        return login != null && password != null;
    }
}