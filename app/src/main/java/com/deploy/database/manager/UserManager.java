package com.deploy.database.manager;

import com.deploy.bo.User;

/**
 * Created by puneet on 11/8/17.
 */

public interface UserManager {
    public void addUser(User user);
    public boolean isUserExist();
    public User getUser();
    public void updateProfilePic(User user);
    public User findUserByUserId(Long userId);
    public void updateUser(User user);
    public void deleteUserById(int userId);
    public void deleteAll();
}
