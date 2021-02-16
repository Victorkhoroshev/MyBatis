package net.thumbtack.school.elections.server.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Idea implements Comparable<Idea>, Serializable {
    //REVU: предложение может быть без автора?
    private @Nullable Voter author;
    private final String key;
    private final String textOfIdea;
    private int sum;
    private Float rating;
    private Map<Voter, Integer> votedVoters;

    public Idea(String key, Voter author, String idea) {
        setAuthor(author);
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

    public void setAuthor(@Nullable Voter author) {
        this.author = author;
    }

    public String getKey() {
        return key;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public @Nullable Voter getAuthor() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idea idea = (Idea) o;
        return key.equals(idea.key);
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
