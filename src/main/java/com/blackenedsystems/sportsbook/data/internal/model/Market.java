package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * @author Alan Tibbetts
 * @since 15/03/16
 */
public class Market extends CoreEntity {
    private int id;
    private String name;
    private MarketType marketType;
    private String instructions;

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

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "Market{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", marketType=" + marketType +
                ", instructions='" + instructions + '\'' +
                super.toString() +
                '}';
    }
}
