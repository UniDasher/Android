package com.uni.unidasher.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//import org.acra.ACRA;

/**
 * Database helper using ORMLite library. This provides the only database in the app. Database stores object types, that are located in
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "dasher_orm.db";
    private static final int DATABASE_VERSION = 1;
    //private static final String TAG = "DatabaseHelper";

    /**
     * Executor used to queueing inserting or deleting objects to database.
     */
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    /**
     * This hahMap holds all {@link RuntimeExceptionDao} objects related to every database table. This objects are used to make all basic operations
     * class, we need to use Answer.class as the key and cast the returned object to RuntimeExceptiondao<Answer, String> (if the main key in this table is a String),
     */
    private HashMap<Class, RuntimeExceptionDao<?, ?>> mClassRuntimeDaosMap;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    /**
     * It creates all required tables using ORMLite tool {@link TableUtils}. Tables are correlated to objects located in
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ShoppingBasket.class);
            TableUtils.createTable(connectionSource, DishInfo.class);
            TableUtils.createTable(connectionSource, ShopInfo.class);
            TableUtils.createTable(connectionSource, UserAdressInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        mClassRuntimeDaosMap = new HashMap<>();
        mClassRuntimeDaosMap.put(ShoppingBasket.class, getRuntimeExceptionDao(ShoppingBasket.class));
        mClassRuntimeDaosMap.put(DishInfo.class, getRuntimeExceptionDao(DishInfo.class));
        mClassRuntimeDaosMap.put(ShopInfo.class, getRuntimeExceptionDao(ShopInfo.class));
        mClassRuntimeDaosMap.put(UserAdressInfo.class, getRuntimeExceptionDao(UserAdressInfo.class));
    }

    /**
     * It inserts a new object to database. The object will be inserted with all nested objects and collections.
     *
     * @param listener This listener will be run after inserting an object to database. Inserting to database is made in separated thread, to not block the UI thread.
     *
     */
    public <T extends DatabaseModelProvider> void insertIfNotExists(final T object, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (object.hasStringMainKey()) {
                                RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(object.getClass());
                                runtimeDao.createIfNotExists(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            } else {
                                RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());
                                runtimeDao.createIfNotExists(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });
    }

    /**
     * It removes old object with the same id, if it exists, and adds a new one. There is also a method {@link #insertIfNotExists(com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider, OnDatabaseInsertedListener)}
     * but they need to be sapareted, because this method is handling correctly replacing the objects, that contain foreign collections, where the new collections are different
     * to the old one. The other method is working correctly when we need to replace objects containing nested single objects.
     *
     * @param object Object that implements {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider} and contains foreign collections
     * @param listener This listener will be run after inserting an object to database. Inserting to database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void removeOldAndInsert(final T object, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (object.hasStringMainKey()) {
                                RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(object.getClass());

                                T previousObject = runtimeDao.queryForSameId(object);
                                if (previousObject != null) {
                                    previousObject.deleteNestedObjects(mClassRuntimeDaosMap);
                                    runtimeDao.delete(previousObject);
                                }

                                runtimeDao.createOrUpdate(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            } else {
                                RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());

                                T previousObject = runtimeDao.queryForSameId(object);
                                if (previousObject != null) {
                                    previousObject.deleteNestedObjects(mClassRuntimeDaosMap);
                                    runtimeDao.delete(previousObject);
                                }

                                runtimeDao.createOrUpdate(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });
    }

    /**
     * It inserts or updates an object in database. There is also a method {@link #removeOldAndInsert(com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider, OnDatabaseInsertedListener)}
     * but they need to be sapareted, because this method is handling correctly replacing the objects, that contain nested single objects and the second one is prepared to
     * replace objects that contain foreign collections.
     *
     * @param object Object that implements {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}
     * @param listener This listener will be run after inserting an object to database. Inserting to database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void insertIfNotExistsOrUpdate(final T object, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (object.hasStringMainKey()) {
                                RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(object.getClass());
                                runtimeDao.createOrUpdate(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            } else {
                                RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());
                                runtimeDao.createOrUpdate(object);
                                object.rewriteCollections(mClassRuntimeDaosMap);
                                return null;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });
    }

    /**
     * It removes old object with the same id, if it exists, and adds a new one.
     *
     * @param list List of objects that implement {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}.
     * @param listener This listener will be run after inserting an object to database. Inserting to database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void removeOldAndInsert(final List<T> list, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (list.size() > 0) {
                                if (list.get(0).hasStringMainKey()) {
                                    RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(list.get(0).getClass());

                                    for (T object : list) {
                                        T previousObject = runtimeDao.queryForSameId(object);
                                        if (previousObject != null) {
                                            previousObject.deleteNestedObjects(mClassRuntimeDaosMap);
                                            runtimeDao.delete(previousObject);
                                        }

                                        runtimeDao.createOrUpdate(object);
                                        object.rewriteCollections(mClassRuntimeDaosMap);
                                    }
                                } else {
                                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(list.get(0).getClass());

                                    for (T object : list) {
                                        T previousObject = runtimeDao.queryForSameId(object);
                                        if (previousObject != null) {
                                            previousObject.deleteNestedObjects(mClassRuntimeDaosMap);
                                            runtimeDao.delete(previousObject);
                                        }

                                        runtimeDao.createOrUpdate(object);
                                        object.rewriteCollections(mClassRuntimeDaosMap);
                                    }
                                }
                            }
                            return null;
                        }
                    } );
                } catch (Exception e) {
//                ACRA.getErrorReporter().handleException(e);
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        } );
    }

    /**
     * Inserts list of objects to database.
     *
     * @param list List of objects that implement {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}.
     * @param listener This listener will be run after inserting an object to database. Inserting to database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void insertIfNotExists(final List<T> list, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (list.size() > 0) {
                                if (list.get(0).hasStringMainKey()) {
                                    RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(list.get(0).getClass());
                                    for (T object : list) {
                                        runtimeDao.createIfNotExists(object);
                                        object.rewriteCollections(mClassRuntimeDaosMap);
                                    }
                                } else {
                                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(list.get(0).getClass());
                                    for (T object : list) {
                                        runtimeDao.createIfNotExists(object);
                                        object.rewriteCollections(mClassRuntimeDaosMap);
                                    }
                                }
                            }
                            return null;
                        }
                    });
                } catch (Exception e) {
//                ACRA.getErrorReporter().handleException(e);
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });

    }

    /**
     * Gets all objects of specified type table from database.
     * @param clazz Any class that extends {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}.
     * @return List of objects from database of class passed as argument
     */
    public <T extends DatabaseModelProvider> List<T> getAll(Class clazz) {
        List<T> list = new ArrayList<>();
        try {
            RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(clazz);

            for (T entry : runtimeDao.queryForAll()) {
                entry.rewriteCollections(mClassRuntimeDaosMap);
                list.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Gets first object from database from specified table (class) that has a specified value of a field.
     *
     * @param clazz Any class that extends {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}.
     * @param columnName Column name (field name), that is declared in @DatabaseField annotation in this class.
     * @param fieldValue Value of this field, which we are looking for.
     * @return Returns the first object that match to the conditions, or null, if this object doesn't exist.
     */
    public <T extends DatabaseModelProvider> T getByField(Class clazz, String columnName, Object fieldValue) {
        try {
            RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(clazz);
            T object = runtimeDao.queryBuilder().where().eq(columnName, fieldValue).queryForFirst();
            if (object != null) {
                object.rewriteCollections(mClassRuntimeDaosMap);
            }
            return object;
        } catch (Exception e) {
//            ACRA.getErrorReporter().handleException(e);
        }
        return null;
    }

    /**
     * Gets all objects from database from specified table (class) that has a specified value of a field.
     *
     * @param clazz Any class that extends {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}.
     * @param columnName Column name (field name), that is declared in @DatabaseField annotation in this class.
     * @param fieldValue Value of this field, which we are looking for.
     * @return Returns list of object that match to the conditions, or empty list, if none of the objects matches to the conditions.
     */
    public <T extends DatabaseModelProvider> List<T> getListByField(Class clazz, String columnName, Object fieldValue) {
        List<T> list = new ArrayList<>();
        try {
            RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(clazz);
            list = runtimeDao.queryBuilder().where().eq(columnName, fieldValue).query();
            if (list != null) {
                for (T object : list) {
                    object.rewriteCollections(mClassRuntimeDaosMap);
                }
            }
        } catch (Exception e) {
//            ACRA.getErrorReporter().handleException(e);
        }
        return list;
    }

    /**
     * Deletes an object from database.
     *
     * @param object Object that implements {@link com.uni.unidasher.data.datamodel.databaseAndApi.DatabaseModelProvider}, and was previously got from database.
     * @param listener This listener will be run after deleting an object to database. Deleting an object from database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void delete(final T object, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (object.hasStringMainKey()) {
                                RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(object.getClass());
                                object.deleteNestedObjects(mClassRuntimeDaosMap);
                                runtimeDao.delete(object);
                            } else {
                                RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());
                                object.deleteNestedObjects(mClassRuntimeDaosMap);
                                runtimeDao.delete(object);
                            }
                            return null;
                        }
                    });
                } catch (Exception e) {
//                ACRA.getErrorReporter().handleException(e);
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });
    }

    /**
     * Deletes list of objects from database.
     *
     * @param list List of objects that implement {@link DatabaseModelProvider}, and was previously got from database.
     * @param listener This listener will be run after deleting an object to database. Deleting an object from database is made in separated thread, to not block the UI thread.
     */
    public <T extends DatabaseModelProvider> void delete(final List<T> list, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionManager.callInTransaction(connectionSource, new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            if (list.size() > 0) {
                                if (list.get(0).hasStringMainKey()) {
                                    RuntimeExceptionDao<T, String> runtimeDao = (RuntimeExceptionDao<T, String>) mClassRuntimeDaosMap.get(list.get(0).getClass());
                                    for (T object : list) {
                                        object.deleteNestedObjects(mClassRuntimeDaosMap);
                                    }
                                    runtimeDao.delete(list);
                                } else {
                                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(list.get(0).getClass());
                                    for (T object : list) {
                                        object.deleteNestedObjects(mClassRuntimeDaosMap);
                                    }
                                    runtimeDao.delete(list);
                                }
                            }
                            return null;
                        }
                    });
                } catch (Exception e) {
//                ACRA.getErrorReporter().handleException(e);
                }
                if (listener != null) {
                    listener.onDatabaseInserted();
                }
            }
        });
    }

    public static interface OnDatabaseInsertedListener {
        public void onDatabaseInserted();
    }
}