package net.thumbtack.school.database.jdbc;

import net.thumbtack.school.database.model.Group;
import net.thumbtack.school.database.model.School;
import net.thumbtack.school.database.model.Subject;
import net.thumbtack.school.database.model.Trainee;

import java.sql.*;
import java.util.*;

public class JdbcService {
    private static final Connection CONNECTION = JdbcUtils.getConnection();

    public static void insertTrainee(Trainee trainee) throws SQLException {
        String insertQuery = "insert into trainee values(?,?,?,?,?)";
        try (PreparedStatement statement = CONNECTION.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setNull(2, java.sql.Types.INTEGER);
            statement.setString(3, trainee.getFirstName());
            statement.setString(4, trainee.getLastName());
            statement.setInt(5, trainee.getRating());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (!resultSet.next()) {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                trainee.setId(resultSet.getInt(1));
            }
        }
    }

    public static void updateTrainee(Trainee trainee) throws SQLException {
        String updateQuery = "update trainee set firstname = ?, lastname = ?, rating = ? where id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(updateQuery)) {
            statement.setString(1, trainee.getFirstName());
            statement.setString(2, trainee.getLastName());
            statement.setInt(3, trainee.getRating());
            statement.setInt(4, trainee.getId());
            statement.executeUpdate();
        }
    }

    public static Trainee getTraineeByIdUsingColNames(int traineeId) throws SQLException {
        String selectQuery = "select * from trainee where id = " + traineeId;
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
        ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            return new Trainee(resultSet.getInt("id"), resultSet.getString("firstname"),
                    resultSet.getString("lastname"), resultSet.getInt("rating"));
        }
    }

    public static Trainee getTraineeByIdUsingColNumbers(int traineeId) throws SQLException {
        String selectQuery = "select * from trainee where id = " + traineeId;
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return null;
            }
            return new Trainee(resultSet.getInt(1), resultSet.getString(3),
                    resultSet.getString(4), resultSet.getInt(5));
        }
    }

    public static List<Trainee> getTraineesUsingColNames() throws SQLException {
        String selectQuery = "select * from trainee";
        List<Trainee> trainees = new ArrayList<>();
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                trainees.add(new Trainee(resultSet.getInt("id"), resultSet.getString("firstname"),
                        resultSet.getString("lastname"), resultSet.getInt("rating")));
            }
            return trainees;
        }
    }

    public static List<Trainee> getTraineesUsingColNumbers() throws SQLException {
        String selectQuery = "select * from trainee";
        List<Trainee> trainees = new ArrayList<>();
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                trainees.add(new Trainee(resultSet.getInt(1), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getInt(5)));
            }
            return trainees;
        }
    }

    public static void deleteTrainee(Trainee trainee) throws SQLException {
        String deleteQuery = "delete from trainee where id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(deleteQuery)) {
            statement.setInt(1, trainee.getId());
            statement.executeUpdate();
        }
    }

    public static void deleteTrainees() throws SQLException {
        String dropQuery = "delete from trainee";
        try (PreparedStatement statement = CONNECTION.prepareStatement(dropQuery)) {
            statement.executeUpdate();
        }
    }

    public static void insertSubject(Subject subject) throws SQLException {
        String insertQuery = "insert into subject values(?,?)";
        try (PreparedStatement statement = CONNECTION.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setString(2, subject.getName());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                subject.setId(generatedKeys.getInt(1));
            }
        }
    }

    public static Subject getSubjectByIdUsingColNames(int subjectId) throws SQLException {
        String selectQuery = "select * from subject where id = " + subjectId;
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            if (!resultSet.next()) {
                return null;
            }
            return new Subject(resultSet.getInt("id"), resultSet.getString("name"));
        }
    }

    public static Subject getSubjectByIdUsingColNumbers(int subjectId) throws SQLException {
        String selectQuery = "select * from subject where id = " + subjectId;
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
             ResultSet resultSet = statement.executeQuery(selectQuery)) {
            if (!resultSet.next()) {
                return null;
            }
            return new Subject(resultSet.getInt(1), resultSet.getString(2));
        }
    }

    public static void deleteSubjects() throws SQLException {
        String dropQuery = "delete from subject";
        try (PreparedStatement statement = CONNECTION.prepareStatement(dropQuery)) {
            statement.executeUpdate();
        }
    }

    public static void insertSchool(School school) throws SQLException {
        String insertQuery = "insert into school values(?,?,?)";
        try (PreparedStatement statement = CONNECTION.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setString(2, school.getName());
            statement.setInt(3, school.getYear());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (!resultSet.next()) {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                school.setId(resultSet.getInt(1));
            }
        }
    }

    public static School getSchoolByIdUsingColNames(int schoolId) throws SQLException {
        String selectQuery = "select * from school where id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery)) {
            statement.setInt(1, schoolId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new School(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getInt("year"));
            }
        }
    }

    public static School getSchoolByIdUsingColNumbers(int schoolId) throws SQLException {
        String selectQuery = "select * from school where id = ?";
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery)) {
            statement.setInt(1, schoolId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new School(resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getInt(3));
            }
        }
    }

    public static void deleteSchools() throws SQLException {
        String dropQuery = "delete from school";
        try (PreparedStatement statement = CONNECTION.prepareStatement(dropQuery)) {
            statement.executeUpdate();
        }
    }

    public static void insertGroup(School school, Group group) throws SQLException {
        String insertQuery = "insert into `group` values(?,?,?,?)";
        try (PreparedStatement statement = CONNECTION.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setInt(2, school.getId());
            statement.setString(3, group.getName());
            statement.setString(4, group.getRoom());
            statement.executeUpdate();
            try(ResultSet resultSet = statement.getGeneratedKeys()) {
                if (!resultSet.next()) {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                group.setId(resultSet.getInt(1));
            }
        }
    }

    public static School getSchoolByIdWithGroups(int id) throws SQLException {
         School school = null;
         String selectQuery = "select * from school join `group` where schoolid = " + id;
         try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
         ResultSet resultSet = statement.executeQuery()) {
             if (resultSet.next()) {
             school = new School(resultSet.getInt(1), resultSet.getString(2),
                     resultSet.getInt(3));
             school.addGroup(new Group(resultSet.getInt(4), resultSet.getString(6),
                     resultSet.getString(7)));
             while (resultSet.next()) {
                 school.addGroup(new Group(resultSet.getInt(4), resultSet.getString(6),
                         resultSet.getString(7)));
             }
            }
        }
      return school;
    }

    public static List<School> getSchoolsWithGroups() throws SQLException {
        List<School> schools = new ArrayList<>();
        SortedMap<Group, Integer> groupMap = new TreeMap<>();
        String selectQuery = "select * from school join `group`";
        try (PreparedStatement statement = CONNECTION.prepareStatement(selectQuery);
        ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int schoolId = resultSet.getInt(1);
                if (schools.stream()
                        .noneMatch(i -> i.getId() == schoolId)) {
                    schools.add(new School(schoolId, resultSet.getString(2),
                            resultSet.getInt(3)));
                }
                groupMap.put(new Group(resultSet.getInt(4), resultSet.getString(6),
                        resultSet.getString(7)), resultSet.getInt(5));
            }
            for (Map.Entry<Group, Integer> entry : groupMap.entrySet()) {
                for (School school : schools) {
                    if (entry.getValue() == school.getId()) {
                        school.addGroup(entry.getKey());
                    }
                }
            }
        }
        return schools;
    }
}
