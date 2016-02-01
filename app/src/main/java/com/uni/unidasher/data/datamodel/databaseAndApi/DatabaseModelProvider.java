package com.uni.unidasher.data.datamodel.databaseAndApi;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.HashMap;

public interface DatabaseModelProvider {
    /**
     * We use one datamodel both for parsing JSON objects (using GSON) and managing database tables (using ORMLite). But they handle nested collections in different ways.
     * Gson uses normal {@link java.util.List} and OrmLite uses {@link com.j256.ormlite.dao.ForeignCollection}. To be able to use one data model, we need to implement both
     * types of collections and re-write objects from one to another every time. So for example if we have object parsed from JSON, only a normal list will be filled. If we
     * want to insert this object to database, we need to init a foreign collection and add all objects from list. We should do this also in the opposite way. If we get an
     * object from database, and want to use list of nested objects or parse it to JSON using GSON, we need to copy all objects from ForeignCollection to List. We need also
     * to make this behaviour recursive, so we need to call this method in nested objects. To make this possible, we use a HashMap of DAO's (classes prepared to manage database tables),
     * se we can access any DAO that we need.
     */
    public void rewriteCollections(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException;

    /**
     * There are some problems with deleting nested objects using ORMLite, so we need to do this recursively and define it separetly for every class.
     */
    public void deleteNestedObjects(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException;

    /**
     * If true, there is a String main key, else the main key is a long.
     */
    public boolean hasStringMainKey();
}
