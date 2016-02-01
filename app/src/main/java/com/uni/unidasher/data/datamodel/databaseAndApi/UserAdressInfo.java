package com.uni.unidasher.data.datamodel.databaseAndApi;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/26.
 */
@DatabaseTable(tableName = "PlaceOrderUserInfoTable")
public class UserAdressInfo implements DatabaseModelProvider,Serializable {
    public static final String UserId = "id";
    @DatabaseField(id = true, columnName = "id", index = true, unique = true)
    private String userId;

    @DatabaseField(columnName = "userName")
    private String userName;

    @DatabaseField(columnName = "address")
    private String address;

    @DatabaseField(columnName = "latitude")
    private String latitude;

    @DatabaseField(columnName = "longitude")
    private String longitude;

    public UserAdressInfo() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean hasStringMainKey() {
        return true;
    }

    @Override
    public void deleteNestedObjects(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }

    @Override
    public void rewriteCollections(HashMap<Class, RuntimeExceptionDao<?, ?>> classRuntimeDaosMap) throws SQLException {

    }
}
