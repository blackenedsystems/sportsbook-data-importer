package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * @author Alan Tibbetts
 * @since 15/03/16
 */
public class Category extends CoreEntity {

    private int id;
    private String name;

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                super.toString() +
                '}';
    }
}

