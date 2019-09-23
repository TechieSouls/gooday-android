package com.deploy.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.database.CenesDatabase;
import com.deploy.util.CenesUtils;

import java.util.List;

public class CenesUserManagerImpl  {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createTableQuery = "CREATE TABLE cenes_users (user_id INTEGER, " +
            "name TEXT, " +
            "picture TEXT, " +
            "phone TEXT)";

    public CenesUserManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    public void addUser(List<User> users) {

        for (User user: users) {
            addUser(user);
        }
    }
    public void addUser(User user){

        if (CenesUtils.isEmpty(user.getPicture())) {
            user.setPicture("");
        }
        if (CenesUtils.isEmpty(user.getPhone())) {
            user.setPhone("");
        }

        String insertQuery = "insert into cenes_users values("+user.getUserId()+", '"+user.getName()+"', '"+user.getPicture()+"'," +
                " '"+user.getPhone()+"')";
        db.execSQL(insertQuery);
    }

    public User fetchCenesUserByUserId(Integer userId) {

        User user = null;
        String query = "select * from cenes_users where user_id = "+userId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));

        }

        return user;
    }


    public void deleteAllUsers() {
        String deleteQuery = "delete from cenes_users";
        db.execSQL(deleteQuery);
    }
}