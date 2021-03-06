package com.deploy.fragment.guest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by mandeep on 6/10/18.
 */

public class ForgotPasswordFragment extends CenesFragment {

    public final static String TAG = "ForgotPasswordFragment";

    Button buttonForgotPasswordSubmit;
    EditText editForgotPasswordEmail;
    ImageView ivBackButton;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password, container, false);

        init();
        initializeComponents(v);

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

    public void initializeComponents(View view) {

        ivBackButton = (ImageView) view.findViewById(R.id.iv_back_button);
        buttonForgotPasswordSubmit = (Button) view.findViewById(R.id.bt_fp_submit);
        editForgotPasswordEmail = (EditText) view.findViewById(R.id.et_fp_email);

        ivBackButton.setOnClickListener(onClickListener);
        buttonForgotPasswordSubmit.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_fp_submit:

                    if (editForgotPasswordEmail.getText().toString() == null || editForgotPasswordEmail.getText().toString() == "" || editForgotPasswordEmail.getText().toString().length() == 0) {
                        Toast.makeText(getActivity(), "Email Field Cannot be left Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        final String email = editForgotPasswordEmail.getText().toString();
                        new ProfileAsyncTask(cenesApplication, getActivity());
                        new ProfileAsyncTask.ForgotPasswordRequest(new ProfileAsyncTask.ForgotPasswordRequest.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                try {
                                    if (response.getBoolean("success")) {

                                        System.out.println(response.toString());
                                        new ProfileAsyncTask.ForgotPasswordSendEmailRequest(new ProfileAsyncTask.ForgotPasswordSendEmailRequest.AsyncResponse() {
                                            @Override
                                            public void processFinish(JSONObject response) {

                                                ForgotPasswordSuccessFragment forgotPasswordSuccessFragment = new ForgotPasswordSuccessFragment();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("email", editForgotPasswordEmail.getText().toString());
                                                forgotPasswordSuccessFragment.setArguments(bundle);
                                                ((GuestActivity)getActivity()).replaceFragment(forgotPasswordSuccessFragment, ForgotPasswordSuccessFragment.TAG);

                                            }
                                        }).execute(email);

                                    } else {
                                        //Toast.makeText(getActivity().getApplicationContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("User Not Found")
                                                .setMessage("No accounts match this information")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Whatever...
                                                    }
                                                }).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(email);
                    }
                    break;
                case R.id.iv_back_button:
                    getActivity().onBackPressed();
                    break;

                case R.id.iv_bug_report:
                    break;

            }
        }
    };



}
