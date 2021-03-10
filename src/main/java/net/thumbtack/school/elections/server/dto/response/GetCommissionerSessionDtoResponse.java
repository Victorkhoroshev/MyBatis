package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.dto.request.Session;

public class GetCommissionerSessionDtoResponse {
    private final Session session;

    public GetCommissionerSessionDtoResponse(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}