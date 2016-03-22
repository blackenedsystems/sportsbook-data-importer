package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * @author Alan Tibbetts
 * @since 15/03/16
 */
public class Event extends CoreEntity {
    private int id;
    private String name;
    private int competitionId;

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

    public int getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(int competitionId) {
        this.competitionId = competitionId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", competitionId=" + competitionId +
                super.toString() +
                '}';
    }
}
