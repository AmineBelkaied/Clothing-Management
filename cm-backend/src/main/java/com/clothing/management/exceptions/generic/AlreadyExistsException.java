package com.clothing.management.exceptions.generic;

public class AlreadyExistsException extends RuntimeException {

    private final String entityName;
    private final Long entityId;
    private String entityFieldName;

    public AlreadyExistsException(String entityName, Long entityId) {
        super(entityName + " with ID " + entityId + " already exists.");
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public AlreadyExistsException(String entityName, String name) {
        super(entityName + " with name " + name + " already exists.");
        this.entityName = entityName;
        this.entityId = 0L;
    }


    public AlreadyExistsException(String entityName, Long entityId, String entityFieldName) {
        super(entityName + " with ID " + entityId + " and name " + entityFieldName + " already exists.");
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