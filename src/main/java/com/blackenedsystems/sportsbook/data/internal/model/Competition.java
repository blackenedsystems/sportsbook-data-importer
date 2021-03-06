package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * Internal representation of a competition.  This is normally a sporting competition, e.g. the NFL, English
 * Premiership, Wimbledon (tennis), German Grand Prix, etc.  But can also be a non-sporting 'competition', e.g.
 * a general election, the Eurovision song contest, the Oscars, etc.
 *
 * For sorting (and later display) purposes, competitions are associated either with a country, or a region (world,
 * North America, Europe, etc).
 *
 * @author Alan Tibbetts
 * @since 15/03/16
 */
public class Competition extends CoreEntity {

    private int id;
    private String name;
    private int categoryId;
    private String countryCode;
    private Region region;

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String country) {
        this.countryCode = country;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + categoryId +
                ", country='" + countryCode + '\'' +
                ", region='" + region + '\'' +
                super.toString() +
                '}';
    }
}
