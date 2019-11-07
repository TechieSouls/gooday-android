package com.deploy.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.guest.GuestFragment;
import com.deploy.fragment.guest.SignupOptionsFragment;
import com.deploy.fragment.guest.SignupStep1Fragment;
import com.deploy.util.CenesUtils;

import java.util.List;

/**
 * Created by mandeep on 18/9/18.
 */

public class GuestActivity extends CenesActivity {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        fragmentManager = getSupportFragmentManager();

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        User user = userManager.getUser();

        /*if (user == null) {
            replaceFragment(new GuestFragment(), null);
        } else */if (user == null) {
            replaceFragment(new SignupStep1Fragment(), null);
        } else {
            replaceFragment(new SignupOptionsFragment(), null);
        }
        fragmentManager.beginTransaction().commit();

        ActivityCompat.requestPermissions(GuestActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 101);
    }

    public void replaceFragment(Fragment fragment, String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearFragmentsAndOpen(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, null);
    }

    public FragmentManager getHomeActivityFragmentManager() {
        return fragmentManager;
    }

    public Fragment getVisibleFragment() {
        fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    //@Override
    //public void onBackPressed() {
      //  this.moveTaskToBack(true);
    //}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //If permission granted
       System.out.print(
               "Permission Added");
    }

}
