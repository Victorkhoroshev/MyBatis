package net.thumbtack.school.elections.server.model;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class Idea implements Comparable<Idea>, Serializable {
    private static final long serialVersionUID = 4L;
    private final Voter author;
    private final String key;
    private final String textOfIdea;
    private int sum;
    private Float rating;
    private Map<Voter, Integer> votedVoters;
    private boolean isCommunity;

    public Idea(String key, Voter author, String idea) {
        this.author = author;
        this.key = key;
        this.textOfIdea = idea;
        setRating(5);
        setSum(5);
        setVotedVoters(new HashMap<>());
        votedVoters.put(author, 5);
    }

    private void setVotedVoters(Map<Voter, Integer> votedVoters) {
        this.votedVoters = votedVoters;
    }

    public Map<Voter, Integer> getVotedVoters() {
        return votedVoters;
    }

    public String getKey() {
        return key;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Voter getAuthor() {
        return author;
    }

    public String getTextOfIdea() {
        return textOfIdea;
    }

    public Float getRating() {
        return rating;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public boolean isCommunity() {
        return isCommunity;
    }

    public void setCommunity(boolean community) {
        isCommunity = community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idea idea = (Idea) o;
        return Objects.equals(key, idea.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public int compareTo(@NotNull Idea idea) {
        return rating.compareTo(idea.getRating());
    }
}