package com.deploy.fragment.guest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.deploy.AsyncTasks.ProfileAsyncTask;
import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.R;
import com.deploy.activity.GuestActivity;
import com.deploy.application.CenesApplication;
import com.deploy.backendManager.UserApiManager;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.util.CenesUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

public class EmailSignupFragment extends CenesFragment {

    public final static String TAG = "EmailSignupFragment";

    private ImageView ivBackButton;
    private Button btnLogin, btnEmailSignup;
    private EditText etEmailField, etPasswordField, etConfirmPasswordField;

    private CenesApplication cenesApplication;
    private AlertManager alertManager;
    private UserApiManager userApiManager;
    private UserManager userManager;
    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_signup_email, container, false);

        ivBackButton = (ImageView) view.findViewById(R.id.iv_back_button);
        btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnEmailSignup = (Button) view.findViewById(R.id.bt_email_signup);


        etEmailField = (EditText) view.findViewById(R.id.et_email_field);
        etPasswordField = (EditText) view.findViewById(R.id.et_password_field);
        etConfirmPasswordField = (EditText) view.findViewById(R.id.et_confirm_password_field);

        ivBackButton.setOnClickListener(onClickListener);
        btnLogin.setOnClickListener(onClickListener);
        btnEmailSignup.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        alertManager = coreManager.getAlertManager();
        userApiManager = coreManager.getUserAppiManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();
        if (loggedInUser == null) {
            loggedInUser = new User();
        }


        new ProfileAsyncTask(cenesApplication, getActivity());


        return view;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back_button:

                    getActivity().onBackPressed();
                    break;

                case R.id.btn_login:

                    SigninFragment signinFragment = new SigninFragment();
                    ((GuestActivity)getActivity()).replaceFragment(signinFragment,  EmailSignupFragment.TAG);
                    break;

                case R.id.bt_email_signup:

                    if (isValid()) {

                        btnEmailSignup.setClickable(false);

                        if (loggedInUser.getUserId() != null && loggedInUser.getUserId() == 0) {
                            loggedInUser.setUserId(null);
                        }
                        loggedInUser.setAuthType(User.AuthenticateType.email);
                        loggedInUser.setEmail(etEmailField.getText().toString());
                        loggedInUser.setPassword(etPasswordField.getText().toString());

                        try {
                            Gson gson = new Gson();
                            JSONObject postData = new JSONObject(gson.toJson(loggedInUser));

                            new ProfileAsyncTask.SignupStepOneSuccessTask(new ProfileAsyncTask.SignupStepOneSuccessTask.AsyncResponse() {
                                @Override
                                public void processFinish(JSONObject response) {

                                    btnEmailSignup.setClickable(true);

                                    try {
                                        boolean success = response.getBoolean("success");

                                        if (success) {

                                            JSONObject data = response.getJSONObject("data");

                                            Gson gson = new Gson();
                                            loggedInUser = gson.fromJson(data.toString(), User.class);
                                            userManager.deleteAll();
                                            userManager.addUser(loggedInUser);
                                            System.out.println(userManager.getUser().toString());
                                            System.out.println(loggedInUser);
                                            SharedPreferences prefs = getActivity().getSharedPreferences("CenesPrefs", Context.MODE_PRIVATE);
                                            String token = prefs.getString("FcmToken", null);

                                            if (token != null) {
                                                JSONObject registerDeviceObj = new JSONObject();
                                                registerDeviceObj.put("deviceToken", token);
                                                registerDeviceObj.put("deviceType", "android");
                                                registerDeviceObj.put("model", CenesUtils.getDeviceModel());
                                                registerDeviceObj.put("manufacturer", CenesUtils.getDeviceManufacturer());
                                                registerDeviceObj.put("version", CenesUtils.getDeviceVersion());
                                                registerDeviceObj.put("deviceType", "android");
                                                registerDeviceObj.put("userId", loggedInUser.getUserId());
                                                new ProfileAsyncTask.DeviceTokenSyncTask(new ProfileAsyncTask.DeviceTokenSyncTask.AsyncResponse() {
                                                    @Override
                                                    public void processFinish(JSONObject response) {

                                                    }
                                                }).execute(registerDeviceObj);
                                            }

                                            SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                                            ((GuestActivity)getActivity()).clearFragmentsAndOpen(signupStepSuccessFragment);
                                        } else {

                                            String message = response.getString("message");
                                            showAlert("Alert", message);
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

    public boolean isValid() {

        StringBuffer missingFields = new StringBuffer();
        if (etEmailField.getText().toString().length() == 0) {
            if (missingFields.length() > 0) {
                missingFields.append(", ");
            }
            missingFields.append("Email");
        }


        if (etPasswordField.getText().toString().length() == 0) {
            if (missingFields.length() > 0) {
                missingFields.append(", ");
            }
            missingFields.append("Password");
        }

        if (etConfirmPasswordField.getText().toString().length() == 0) {
            if (missingFields.length() > 0) {
                missingFields.append(", ");
            }
            missingFields.append("Confirm Password");
        }

        if (etPasswordField.getText().toString().length() > 0 && etConfirmPasswordField.getText().toString().length() > 0) {
            if (!etPasswordField.getText().toString().equals(etConfirmPasswordField.getText().toString())) {
                missingFields.append("Password/Confirm Password Donot Match");

            }
        }

        if (missingFields.length() > 0) {
            alertManager.getAlert((GuestActivity)getActivity(), missingFields.toString(), "Following Information is Required", null, false, "OK");
            return false;
        }
        return true;
    }
}
