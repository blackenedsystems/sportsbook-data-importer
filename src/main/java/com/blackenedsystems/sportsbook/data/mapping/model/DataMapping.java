package com.blackenedsystems.sportsbook.data.mapping.model;

import java.time.LocalDateTime;

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
    private String parent;
    private boolean active = false;

    private LocalDateTime created;
    private String createdBy;
    private LocalDateTime updated;
    private String updatedBy;

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "DataMapping{" +
                "id=" + id +
                ", externalDataSource=" + externalDataSource +
                ", mappingType=" + mappingType +
                ", internalId='" + internalId + '\'' +
                ", externalId='" + externalId + '\'' +
                ", externalDescription='" + externalDescription + '\'' +
                ", parent='" + parent + '\'' +
                ", active=" + active +
                ", created=" + created +
                ", createdBy='" + createdBy + '\'' +
                ", updated=" + updated +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
