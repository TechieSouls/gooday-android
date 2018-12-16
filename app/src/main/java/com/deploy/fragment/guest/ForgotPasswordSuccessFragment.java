package com.deploy.fragment.guest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.DeviceManager;
import com.deploy.Manager.InternetManager;
import com.deploy.Manager.UrlManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.util.RoundedImageView;

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

    RoundedImageView rivProfiePic;
    TextView fpEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();

        View v = inflater.inflate(R.layout.fragment_forget_password_success, container, false);

        rivProfiePic = (RoundedImageView) v.findViewById(R.id.riv_profie_pic);
        fpEmail = (TextView) v.findViewById(R.id.fp_email);

        String userData = getArguments().getString("user");
        try {
            JSONObject user = new JSONObject(userData);
            fpEmail.setText(user.getString("email"));

        } catch (Exception e) {
            e.printStackTrace();
        }

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
}