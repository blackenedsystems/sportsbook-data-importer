package com.blackenedsystems.sportsbook.data.mapping;

/**
 * @author Alan Tibbetts
 * @since 3/3/16 16:22
 */
public class DataMapping {

    private int id;
    private ExternalDataSource externalDataSource;
    private MappingType mappingType;
    private String internalId;
    private String externalId;
    private String externalDescription;
    private String sportName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExternalDataSource getExternalDataSource() {
        return externalDataSource;
    }

    public void setExternalDataSource(ExternalDataSource externalDataSource) {
        this.externalDataSource = externalDataSource;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }
}
