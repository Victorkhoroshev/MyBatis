package net.thumbtack.school.database.mybatis.daoimpl;

import net.thumbtack.school.database.model.Group;
import net.thumbtack.school.database.model.School;
import net.thumbtack.school.database.model.Subject;
import net.thumbtack.school.database.model.Trainee;
import net.thumbtack.school.database.mybatis.dao.SchoolDao;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class SchoolDaoImpl extends DaoImplBase implements SchoolDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchoolDaoImpl.class);

    @Override
    public School insert(School school) {
        LOGGER.debug("DAO insert School {}", school);
        try (SqlSession sqlSession = getSession()) {
            try {
               getSchoolMapper(sqlSession).insert(school);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't insert school {}", school, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
        return school;
    }

    @Override
    public School getById(int id) {
        LOGGER.debug("DAO get School by Id {}", id);
        try (SqlSession sqlSession = getSession()) {
            try {
                return getSchoolMapper(sqlSession).getById(id);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't get school", ex);
                throw ex;
            }
        }
    }

    @Override
    public List<School> getAllLazy() {
        LOGGER.debug("DAO get all Lazy");
        try (SqlSession sqlSession = getSession()) {
            try {
                return getSchoolMapper(sqlSession).getAllLazy();
            } catch (RuntimeException ex) {
                LOGGER.info("Can't get all lazy", ex);
                throw ex;
            }
        }
    }

    @Override
    public List<School> getAllUsingJoin() {
        LOGGER.debug("DAO get all using join");
        try (SqlSession sqlSession = getSession()) {
            try {
                List<School> list = sqlSession.selectList("net.thumbtack.school.database.mybatis.mappers.SchoolMapper.getAllUsingJoin");
                for (School school: list) {
                    for (Group group : school.getGroups()) {
                        group.setSubjects(getSubjectMapper(sqlSession).getByGroup(group));
                        group.setTrainees(getTraineeMapper(sqlSession).getByGroup(group));
                    }
                }
                return list;
            } catch (RuntimeException ex) {
                LOGGER.info("Can't insert get using join", ex);
                throw ex;
            }
        }
    }

    @Override
    public void update(School school) {
        LOGGER.debug("DAO update School {}", school);
        try (SqlSession sqlSession = getSession()) {
            try {
                getSchoolMapper(sqlSession).update(school);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't update school {}", school, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
    }

    @Override
    public void delete(School school) {
        LOGGER.debug("DAO delete School {}", school);
        try (SqlSession sqlSession = getSession()) {
            try {
                getSchoolMapper(sqlSession).delete(school);
            } catch (RuntimeException ex) {
                LOGGER.info("Can't delete school {}", school, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
    }

    @Override
    public void deleteAll() {
        LOGGER.debug("DAO delete all");
        try (SqlSession sqlSession = getSession()) {
            try {
                getSchoolMapper(sqlSession).deleteAll();
            } catch (RuntimeException ex) {
                LOGGER.info("Can't delete all", ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
    }

    @Override
    public School insertSchoolTransactional(School school2018) {
        LOGGER.debug("Transactional DAO insert{}", school2018);
        try (SqlSession sqlSession = getSession()){
            try {
                getSchoolMapper(sqlSession).insert(school2018);
                for (Group group : school2018.getGroups()) {
                    getGroupMapper(sqlSession).insert(school2018, group);
                    for (Trainee trainee : group.getTrainees()) {
                        getTraineeMapper(sqlSession).insert(group, trainee);
                    }
                    for (Subject subject : group.getSubjects()) {
                        getGroupMapper(sqlSession).addSubjectToGroup(group, subject);
                    }
                }
            } catch (RuntimeException ex) {
                LOGGER.debug("Can't insert School {}", school2018, ex);
                sqlSession.rollback();
                throw ex;
            }
            sqlSession.commit();
        }
        return school2018;
    }
}
