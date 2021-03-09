
package net.thumbtack.school.elections.server.dto.response;

public class AddIdeaDtoResponse {
    private final String key;

    public AddIdeaDtoResponse(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}