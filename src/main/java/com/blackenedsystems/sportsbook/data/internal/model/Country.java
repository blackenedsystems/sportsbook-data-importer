package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * Implementation of the iso 3166-1 standard.  See http://en.wikipedia.org/wiki/ISO_3166-1.
 *
 * @author Alan Tibbetts
 * @since 15/03/16
 */
public class Country extends CoreEntity {

    private int id;
    private String isoCode2;
    private String isoCode3;
    private String isoCodeNumeric;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsoCode2() {
        return isoCode2;
    }

    public void setIsoCode2(String isoCode2) {
        this.isoCode2 = isoCode2.toUpperCase();
    }

    public String getIsoCode3() {
        return isoCode3;
    }

    public void setIsoCode3(String isoCode3) {
        this.isoCode3 = isoCode3.toUpperCase();
    }

    public String getIsoCodeNumeric() {
        return isoCodeNumeric;
    }

    public void setIsoCodeNumeric(String isoCodeNumeric) {
        this.isoCodeNumeric = isoCodeNumeric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", isoCode2='" + isoCode2 + '\'' +
                ", isoCode3='" + isoCode3 + '\'' +
                ", isoCodeNumeric='" + isoCodeNumeric + '\'' +
                ", name='" + name + '\'' +
                super.toString() +
                '}';
    }
}
