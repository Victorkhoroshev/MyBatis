package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Commissioner;

public class LoginCommissionerDtoRequest {
    private Commissioner commissioner;

    public LoginCommissionerDtoRequest(Commissioner commissioner) {
        this.commissioner = commissioner;
    }

    public Commissioner getCommissioner() {
        return commissioner;
    }
}