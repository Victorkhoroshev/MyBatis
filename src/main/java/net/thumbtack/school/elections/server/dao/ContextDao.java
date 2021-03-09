package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.model.Context;

public interface ContextDao{

    void sync(Context context);
}