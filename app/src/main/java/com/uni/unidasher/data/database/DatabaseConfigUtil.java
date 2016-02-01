package com.uni.unidasher.data.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            ShoppingBasket.class,
            DishInfo.class,
            ShopInfo.class,
            UserAdressInfo.class
    };

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("app/src/main/res/raw/ormlite_config.txt"), classes);
    }
}
