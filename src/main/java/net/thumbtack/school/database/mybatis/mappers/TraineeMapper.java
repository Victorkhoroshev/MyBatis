package net.thumbtack.school.database.mybatis.mappers;

import net.thumbtack.school.database.model.Group;
import net.thumbtack.school.database.model.Trainee;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface TraineeMapper {
    @Insert("INSERT INTO trainee (groupid, firstname, lastname, rating) VALUES" +
            "(#{group.id}, #{trainee.firstName}, #{trainee.lastName}, #{trainee.rating} )")
    @Options(useGeneratedKeys = true, keyProperty = "trainee.id", keyColumn = "id")
    Integer insert(@Param("group") Group group,@Param("trainee") Trainee trainee);

    @Select("SELECT id, firstname, lastname, rating FROM trainee WHERE id = #{id}")
    Trainee getById(int id);

    @Select("SELECT id, firstname, lastname, rating FROM trainee")
    List<Trainee> getAll();

    @Update("UPDATE trainee SET firstname = #{trainee.firstName}, lastname = #{trainee.lastName}," +
            " rating = #{trainee.rating} WHERE id = #{trainee.id}")
    void update(@Param("trainee") Trainee trainee);

    @Select({"<script>",
            "SELECT id, firstname, lastname, rating FROM trainee",
            "<where>" +
            "<if test='firstname != null'> firstname like #{firstname}",
            "</if>",
            "<if test='lastname != null'> AND lastname like #{lastname}",
            "</if>",
            "<if test='rating != null'> AND rating = #{rating}",
            "</if>",
            "</where>" +
                    "</script>"})
    List<Trainee> getAllWithParams(@Param("firstname") String firstName, @Param("lastname") String lastName,
                                   @Param("rating") Integer rating);

    @Delete("DELETE FROM trainee WHERE id = #{trainee.id}")
    void delete(@Param("trainee") Trainee trainee);

    @Delete("DELETE FROM trainee")
    void deleteAll();

    @Insert({"<script>",
            "INSERT INTO trainee (firstname, lastname, rating) VALUES",
            "<foreach item='item' collection='list' separator=','>",
            "( #{item.firstName}, #{item.lastName}, #{item.rating})",
            "</foreach>",
            "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void batchInsert(@Param("list") List<Trainee> trainees);

    @Select("SELECT id, firstname, lastname, rating FROM trainee WHERE groupid = #{group.id}")
    List<Trainee> getByGroup(@Param("group") Group group);
}
