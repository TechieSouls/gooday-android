package com.deploy.fragment.guest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.deploy.AsyncTasks.ProfileAsyncTask;
import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.DeviceManager;
import com.deploy.Manager.InternetManager;
import com.deploy.Manager.UrlManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.R;
import com.deploy.activity.GuestActivity;
import com.deploy.activity.SignInActivity;
import com.deploy.application.CenesApplication;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;

import org.json.JSONObject;

/**
 * Created by mandeep on 7/10/18.
 */

public class ForgotPasswordSuccessFragment  extends CenesFragment {

    public final static String TAG = "ForgotPasswordSuccessFragment";

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    EditText etPassword, etConfirmPassword;
    Button forgetPasswordResetButton;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password_success, container, false);

        etPassword = (EditText) v.findViewById(R.id.et_fp_password);
        etConfirmPassword = (EditText) v.findViewById(R.id.et_fp_confirm_passwword);
        forgetPasswordResetButton = (Button) v.findViewById(R.id.bt_fp_submit);

        email = getArguments().getString("email");
        forgetPasswordResetButton.setOnClickListener(onClickListener);

        init();
        return v;
    }

    public void init() {
        //----------------------------------
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        validationManager = coreManager.getValidatioManager();
        internetManager = coreManager.getInternetManager();
        urlManager = coreManager.getUrlManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_fp_submit:

                    if (etPassword.getText().toString().length() == 0) {
                        showAlert("Alert", "Password canot be left empty");
                    } else if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                        showAlert("Alert", "Passwords Donot Match");
                    } else if (etPassword.getText().length() < 8 || etConfirmPassword.getText().length() > 16) {
                        showAlert("Alert", "Check Password \nRequirements");
                    } else {


                        try {
                            JSONObject postData = new JSONObject();
                            postData.put("email", email);
                            postData.put("password", etPassword.getText().toString());
                            postData.put("requestFrom", "App");

                            new ProfileAsyncTask(cenesApplication, getActivity());
                            new ProfileAsyncTask.UpdatePasswordTask(new ProfileAsyncTask.UpdatePasswordTask.AsyncResponse() {
                                @Override
                                public void processFinish(JSONObject response) {

                                    try {

                                        boolean success = response.getBoolean("success");
                                        if (success == true) {

                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("Reset Successful")
                                                    .setMessage("")
                                                    .setCancelable(false)
                                                    .setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            startActivity(new Intent((GuestActivity)getActivity(), GuestActivity.class));
                                                            getActivity().finish();

                                                        }
                                                    }).show();


                                        } else {

                                            showAlert("Alert", response.getString("message"));
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).execute(postData);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

}
