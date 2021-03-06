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

        User userExists = fetchCenesUserByUserId(user.getUserId());
        if (userExists != null) {

            user.setUserId(userExists.getUserId());
            updateCenesUser(user);

        } else {
            this.db = cenesDatabase.getReadableDatabase();
            if (CenesUtils.isEmpty(user.getPicture())) {
                user.setPicture("");
            }
            if (CenesUtils.isEmpty(user.getPhone())) {
                user.setPhone("");
            }

            String insertQuery = "insert into cenes_users values("+user.getUserId()+", '"+user.getName()+"', '"+user.getPicture()+"'," +
                    " '"+user.getPhone()+"')";
            db.execSQL(insertQuery);
            db.close();
        }
    }

    public User fetchCenesUserByUserId(Integer userId) {
        this.db = cenesDatabase.getReadableDatabase();
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
        cursor.close();
        db.close();
        return user;
    }

    public void updateCenesUser(User user) {
        this.db = cenesDatabase.getReadableDatabase();
        db.execSQL("update cenes_users set name = '"+user.getName()+"', " +
                " picture = '"+user.getPicture()+"', phone = '"+user.getPhone()+"' where user_id = "+user.getUserId()+" ");
        db.close();
    }



    public void deleteAllUsers() {
        this.db = cenesDatabase.getReadableDatabase();
        String deleteQuery = "delete from cenes_users";
        db.execSQL(deleteQuery);
        db.close();
    }
}
