package net.thumbtack.school.elections.server.dto.response;

import net.thumbtack.school.elections.server.dto.request.Session;

public class GetVoterSessionDtoResponse {
    private final Session session;

    public GetVoterSessionDtoResponse(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}