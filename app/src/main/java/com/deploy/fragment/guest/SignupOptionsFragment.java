package com.deploy.fragment.guest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deploy.R;
import com.deploy.activity.GuestActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.util.CenesConstants;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

public class SignupOptionsFragment extends CenesFragment {

    private static String TAG = "SignupOptionsFragment";
    private static int  GOOGLE_SIGN_IN = 101;

    private RelativeLayout rlFacebookBtn, rlGoogleBtn, rlEmailBtn;
    private TextView tvTandCText;

    private LoginButton buttonJoinFB;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();

        View view = inflater.inflate(R.layout.fragment_guest_signup_options, container, false);

        rlFacebookBtn = (RelativeLayout) view.findViewById(R.id.rl_facebook_btn);
        rlGoogleBtn = (RelativeLayout) view.findViewById(R.id.rl_google_btn);
        rlEmailBtn = (RelativeLayout) view.findViewById(R.id.rl_email_btn);

        tvTandCText = (TextView) view.findViewById(R.id.tv_tandc_text);

        buttonJoinFB = (LoginButton) view.findViewById(R.id.bt_fb_join);
        //buttonJoinFB.setPermissions("email","public_profile");

        buttonJoinFB.registerCallback(callbackManager, facebookCallback);
        buttonJoinFB.setFragment(this);

        rlFacebookBtn.setOnClickListener(onClickListener);
        rlGoogleBtn.setOnClickListener(onClickListener);
        rlEmailBtn.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();
        if (loggedInUser == null) {
            loggedInUser = new User();
        }

        Spanned htmlAsSpanned = Html.fromHtml(getString(R.string.tandc_text)); // used by TextView
        tvTandCText.setText(htmlAsSpanned);
        return  view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_facebook_btn:
                    disconnectFromFacebook();
                    buttonJoinFB.performClick();
                    break;

                case R.id.rl_google_btn:
                    googleSignInCall();
                    break;

                case R.id.rl_email_btn:

                    SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                    ((GuestActivity)getActivity()).replaceFragment(signupStepSuccessFragment, SignupOptionsFragment.TAG);
                    break;

                    default:
                        System.out.println("Woww ji woww");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GOOGLE_SIGN_IN) {

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleGoogleSignInResult(task);

            }
        } else {
            System.out.println("Result False : "+data.getDataString());
        }
    }

    FacebookCallback facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("Fb status : ", "Facebook Id : " + loginResult.getAccessToken().getUserId() + ",Access Token : " + loginResult.getAccessToken().getToken());
            String facebookId = loginResult.getAccessToken().getUserId();
            String facebookToken = loginResult.getAccessToken().getToken();

            loggedInUser.setAuthType("facebook");
            loggedInUser.setFacebookId(facebookId);
            loggedInUser.setFacebookAuthToken(facebookToken);
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        String id = object.getString("id");
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");

                        loggedInUser.setName(first_name + " " +last_name);
                        //gender = object.getString("gender");
                        //String birthday = object.getString("birthday");
                        if (object.has("picture")) {
                            JSONObject dataObj = object.getJSONObject("picture");
                            loggedInUser.setPicture(dataObj.getJSONObject("data").getString("url"));
                        }

                        String email;
                        if (object.has("email")) {
                            loggedInUser.setEmail(object.getString("email"));
                        }

                        if (object.has("gender")) {
                            loggedInUser.setGender(object.getString("gender"));
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

                        userManager.deleteAll();
                        userManager.addUser(loggedInUser);

                        SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
                        ((GuestActivity)getActivity()).replaceFragment(signupStepSuccessFragment,  SignupOptionsFragment.TAG);
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


    private void signInGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    public void googleSignInCall() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().
                        requestServerAuthCode(CenesConstants.GoogleWebClientid, true).build();

        try {
            mGoogleSignInClient = GoogleSignIn.getClient(getCenesActivity(), gso);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getCenesActivity());
            if (account != null) {
                signOutGoogleAccount();
            } else {
                signInGoogleAccount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            System.out.println(account.getId());
            System.out.println(account.getIdToken());
            loggedInUser.setAuthType("google");
            loggedInUser.setName(account.getDisplayName());
            loggedInUser.setEmail(account.getEmail());
            loggedInUser.setGoogleId(account.getId());

            userManager.deleteAll();
            userManager.addUser(loggedInUser);

            SignupStepSuccessFragment signupStepSuccessFragment = new SignupStepSuccessFragment();
            ((GuestActivity)getActivity()).replaceFragment(signupStepSuccessFragment,  SignupOptionsFragment.TAG);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOutGoogleAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getCenesActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                signInGoogleAccount();
            }
        });
    }

}
