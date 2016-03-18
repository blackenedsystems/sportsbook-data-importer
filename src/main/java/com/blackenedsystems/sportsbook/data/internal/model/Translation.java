package com.blackenedsystems.sportsbook.data.internal.model;

/**
 * @author Alan Tibbetts
 * @since 16/03/16
 */
public class Translation extends CoreEntity {

    private int id;
    private EntityType entityType;
    private String language;
    private int key;
    private String translation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id=" + id +
                ", entityType=" + entityType +
                ", language='" + language + '\'' +
                ", key=" + key +
                ", translation='" + translation + '\'' +
                super.toString() +
                '}';
    }
}
