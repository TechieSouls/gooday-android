package com.deploy.fragment.guest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.deploy.AsyncTasks.ProfileAsyncTask;
import com.deploy.R;
import com.deploy.activity.GuestActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class SignupOptions extends CenesFragment {

    private RelativeLayout rlFacebookBtn, rlGoogleBtn, rlEmailBtn;
    private LoginButton buttonJoinFB;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        View view = inflater.inflate(R.layout.fragment_guest_signup_options, container, false);

        rlFacebookBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);
        rlGoogleBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);
        rlEmailBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);

        buttonJoinFB = (LoginButton) view.findViewById(R.id.bt_fb_join);
        buttonJoinFB.registerCallback(callbackManager, facebookCallback);
        rlFacebookBtn.setOnClickListener(onClickListener);
        rlGoogleBtn.setOnClickListener(onClickListener);
        rlEmailBtn.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        return  view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_facebook_btn:
                    buttonJoinFB.performClick();
                    break;

                case R.id.rl_google_btn:

                    break;

                case R.id.rl_email_btn:

                    SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                    ((GuestActivity)getActivity()).replaceFragment(signupStepSuccessFragment, null);
                    break;


                    default:
                        System.out.println("Woww ji woww");
            }
        }
    };

    FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
            String facebookId = loginResult.getAccessToken().getUserId();
            String facebookToken = loginResult.getAccessToken().getToken();

            final User user = new User();
            user.setFacebookID(facebookId);
            user.setFacebookAuthToken(facebookToken);
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String id = object.getString("id");
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");

                        user.setName(first_name + " " +last_name);
                        //gender = object.getString("gender");
                        //String birthday = object.getString("birthday");
                        if (object.has("picture")) {
                            JSONObject dataObj = object.getJSONObject("picture");
                            user.setPicture(dataObj.getJSONObject("data").getString("url"));
                        }

                        String email;
                        if (object.has("email")) {
                            user.setEmail(object.getString("email"));
                        }

                        if (object.has("gender")) {
                            user.setGender(object.getString("gender"));
                        }

                        /*if (photo != null) {
                            new ProfileAsyncTask.DownloadFacebookImage(new ProfileAsyncTask.DownloadFacebookImage.AsyncResponse() {
                                @Override
                                public void processFinish(Bitmap response) {
                                    rivProfileRoundedImg.setVisibility(View.VISIBLE);
                                    rivProfileRoundedImg.setImageBitmap(response);
                                }
                            }).execute(photo);
                        }*/
                        userManager.addUser(user);
                        Log.i("RESULTS : ", object.getString("email"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,cover,picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.e("Cancelled", "User cancelled dialog");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e("Error : ", e.getMessage());
        }

    };


    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }

}
