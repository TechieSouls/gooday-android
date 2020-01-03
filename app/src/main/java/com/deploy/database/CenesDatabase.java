package com.deploy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.deploy.application.CenesApplication;
import com.deploy.database.impl.CenesUserManagerImpl;
import com.deploy.database.impl.EventManagerImpl;
import com.deploy.database.impl.EventMemberManagerImpl;
import com.deploy.database.impl.MeTimeManagerImpl;
import com.deploy.database.impl.MeTimePatternManagerImpl;
import com.deploy.database.impl.NotificationManagerImpl;
import com.deploy.database.impl.UserContactManagerImpl;
import com.deploy.database.impl.UserManagerImpl;

/**
 * Created by puneet on 11/8/17.
 */

public class CenesDatabase {

    public DataBaseOpenHelper dataBaseOpenHelper;

     public CenesDatabase(CenesApplication cenesApplication) {
        // TODO Auto-generated constructor stub
        dataBaseOpenHelper = new DataBaseOpenHelper(cenesApplication);
    }

    public SQLiteDatabase getReadableDatabase() {
        return dataBaseOpenHelper.getReadableDatabase();
    }

    public SQLiteDatabase getWriteableDatabase() {
        return dataBaseOpenHelper.getWritableDatabase();
    }

    public class DataBaseOpenHelper extends SQLiteOpenHelper {

        public static final String DATA_BASE_NAME = "cenes.db";
        public static final int VERSION = 1;

        public DataBaseOpenHelper(Context context) {
            super(context, DATA_BASE_NAME, null, VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(UserManagerImpl.CreateTableQuery);
            db.execSQL(EventManagerImpl.createTableQuery);
            db.execSQL(EventMemberManagerImpl.createTableQuery);
            db.execSQL(CenesUserManagerImpl.createTableQuery);
            db.execSQL(UserContactManagerImpl.createUserContactTableQuery);
            db.execSQL(NotificationManagerImpl.CreateTableQuery);
            db.execSQL(MeTimeManagerImpl.CreateTableQuery);
            db.execSQL(MeTimePatternManagerImpl.CreateTableQuery);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }

    }
}
