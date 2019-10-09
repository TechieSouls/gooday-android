package com.deploy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;


public class MainActivity extends CenesActivity {

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    User user;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cenesApplication = (CenesApplication)getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        user = userManager.getUser();

        if (user != null && user.getUserId() != 0) {
            startActivity(new Intent(MainActivity.this, CenesBaseActivity.class));
            finish();
        } else if (user != null) {
            startActivity(new Intent(MainActivity.this, GuestActivity.class));
            finish();
        }
    }
}
