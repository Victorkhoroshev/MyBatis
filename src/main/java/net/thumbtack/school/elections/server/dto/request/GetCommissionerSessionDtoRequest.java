package net.thumbtack.school.elections.server.dto.request;

import net.thumbtack.school.elections.server.model.Commissioner;

public class GetCommissionerSessionDtoRequest {
    private Commissioner commissioner;

    public GetCommissionerSessionDtoRequest(Commissioner commissioner) {
        this.commissioner = commissioner;
    }

    public Commissioner getCommissioner() {
        return commissioner;
    }
}