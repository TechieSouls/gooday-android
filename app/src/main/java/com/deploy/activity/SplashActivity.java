package com.deploy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;

import android.os.Handler;
import android.view.Window;

/**
 * Created by puneet on 11/8/17.
 */

public class SplashActivity extends CenesActivity{

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;

    ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userManager.isUserExist()){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                } else{
                    startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                    finish();
                }
            }

        }, 500);

    }
}
