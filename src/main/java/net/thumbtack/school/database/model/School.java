package net.thumbtack.school.database.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class School {
    private int id;
    private String name;
    private int year;
    private List<Group> groups;

    public School() {

    }

    public School(int id, String name, int year, List<Group> groups) {
        setId(id);
        setName(name);
        setYear(year);
        setGroups(groups);
    }

    public School(int id, String name, int year) {
        this(id, name, year, new ArrayList<>());
    }

    public School(String name, int year) {
        this(0, name, year);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return year == school.year &&
                Objects.equals(name, school.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year);
    }
}
