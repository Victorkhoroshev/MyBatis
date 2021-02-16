package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.service.ServerException;
import java.util.Set;

public interface VoterDao<T> {
    //REVU: я думаю, что здесь дженерики не нужны
    T get(String s) throws ServerException;

    Set<T> getAll();
    //REVU: обычно при save\add возвращают сущность
    void save(T t) throws ServerException;
}
