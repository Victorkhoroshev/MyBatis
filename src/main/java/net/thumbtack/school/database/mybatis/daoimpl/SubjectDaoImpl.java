package net.thumbtack.school.database.mybatis.daoimpl;

import net.thumbtack.school.database.model.Subject;
import net.thumbtack.school.database.mybatis.dao.SubjectDao;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class SubjectDaoImpl extends DaoImplBase implements SubjectDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectDaoImpl.class);

    @Override
    public Subject insert(Subject subject) {
        LOGGER.debug("DAO insert Subject {}", subject);
        try (SqlSession sqlSession = getSession()) {
            try {
                getSubjectMapper(sqlSession).insert(subject);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't insert subject {}", subject, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
        return subject;
    }

    @Override
    public Subject getById(int id) {
        LOGGER.debug("DAO get by Id {}", id);
        try (SqlSession sqlSession = getSession()) {
            try {
                return getSubjectMapper(sqlSession).getById(id);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't get by id {}", id, ex);
                throw ex;
            }
        }
    }

    @Override
    public List<Subject> getAll() {
        LOGGER.debug("DAO get all");
        try (SqlSession sqlSession = getSession()) {
            try {
                return getSubjectMapper(sqlSession).getAll();
            } catch (RuntimeException ex) {
                LOGGER.info("Can't get all", ex);
                throw ex;
            }
        }
    }

    @Override
    public Subject update(Subject subject) {
        LOGGER.debug("DAO update Subject {}", subject);
        try (SqlSession sqlSession = getSession()) {
            try {
                getSubjectMapper(sqlSession).update(subject);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't update subject {}", subject, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
        return subject;
    }

    @Override
    public void delete(Subject subject) {
        LOGGER.debug("DAO delete Subject {}", subject);
        try (SqlSession sqlSession = getSession()) {
            try {
                getSubjectMapper(sqlSession).delete(subject);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't delete subject {}", subject, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
    }

    @Override
    public void deleteAll() {
        LOGGER.debug("DAO delete All");
        try (SqlSession sqlSession = getSession()) {
            try {
                getSubjectMapper(sqlSession).deleteAll();
            } catch (RuntimeException ex) {
                LOGGER.info("Can't delete all", ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
    }
}
