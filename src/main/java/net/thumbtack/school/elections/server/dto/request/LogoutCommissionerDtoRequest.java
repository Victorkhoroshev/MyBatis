package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Commissioner;

public class LogoutCommissionerDtoRequest {
    private final Commissioner commissioner;

    public LogoutCommissionerDtoRequest(Commissioner commissioner) {
        this.commissioner = commissioner;
    }

    public Commissioner getCommissioner() {
        return commissioner;
    }
}
