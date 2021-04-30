package net.thumbtack.school.database.mybatis.mappers;

import net.thumbtack.school.database.model.Group;
import net.thumbtack.school.database.model.Subject;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface SubjectMapper {
    @Insert("INSERT INTO `subject` (name) VALUES" +
             "(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer insert(Subject subject);

    @Select("SELECT id, name FROM `subject` WHERE id = #{id}")
    Subject getById(int id);

    @Select("SELECT * FROM `subject`")
    List<Subject> getAll();

    @Update("UPDATE `subject` SET name = #{subject.name} where id = #{subject.id}")
    void update(@Param("subject") Subject subject);

    @Delete("DELETE FROM `subject` WHERE id = #{subject.id}")
    void delete(@Param("subject") Subject subject);

    @Delete("DELETE FROM `subject`")
    void deleteAll();

    @Select("SELECT subjectid as id, name FROM group_subject WHERE groupid = #{group.id}")
    List<Subject> getByGroup(@Param("group") Group group);
}
