package net.thumbtack.school.elections.server.dao;

import net.thumbtack.school.elections.server.service.ServerException;
//REVU: я думаю, что здесь дженерики не нужны
public interface CandidateDao<T> {

    //REVU: я думаю, что здесь дженерики не нужны
    T get(String s) throws ServerException;

    //REVU: обычно при save\add возвращают сущность
    void save(T t);

    boolean contains(String s);

    void delete(T t);
}
