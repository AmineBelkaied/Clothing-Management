package com.clothing.management.exceptions.generic;

public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Long entityId;
    private String entityFieldName;

    public EntityNotFoundException(String entityName, Long entityId) {
        super(entityName + " with ID " + entityId + " not found.");
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String entityName, Long entityId, String entityFieldName) {
        super(entityName + " with ID " + entityId + " and name " + entityFieldName + " not found.");
        this.entityName = entityName;
        this.entityId = entityId;
        this.entityFieldName = entityFieldName;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityFieldName() {
        return entityFieldName;
    }
}