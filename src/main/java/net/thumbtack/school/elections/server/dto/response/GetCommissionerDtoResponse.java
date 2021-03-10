package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.model.Commissioner;

public class GetCommissionerDtoResponse {
    private final Commissioner commissioner;

    public GetCommissionerDtoResponse(Commissioner commissioner) {
        this.commissioner = commissioner;
    }

    public Commissioner getCommissioner() {
        return commissioner;
    }
}