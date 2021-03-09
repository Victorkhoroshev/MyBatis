
package net.thumbtack.school.elections.server.dto.request;

public class GetCommissionerDtoRequest {
    private String token;

    public GetCommissionerDtoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}