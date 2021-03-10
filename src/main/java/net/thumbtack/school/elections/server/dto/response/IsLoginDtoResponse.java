package net.thumbtack.school.elections.server.dto.response;

public class IsLoginDtoResponse {
    private final boolean isLogin;

    public IsLoginDtoResponse(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }
}