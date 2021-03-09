package net.thumbtack.school.elections.server.model;

import java.io.Serializable;

public class Commissioner extends User implements Serializable {
    private final boolean isChairman;

    public Commissioner(String login, String password, boolean isChairman) {
        super(login, password);
        this.isChairman = isChairman;
    }

    public boolean isChairman() {
        return isChairman;
    }
}