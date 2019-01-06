package com.deploy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deploy.Manager.ApiManager;
import com.deploy.Manager.InternetManager;
import com.deploy.Manager.UrlManager;
import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.gathering.CreateGatheringFragment;
import com.deploy.fragment.gathering.GatheringsFragment;
import com.deploy.fragment.NavigationFragment;

import org.json.JSONObject;

/**
 * Created by mandeep on 2/11/17.
 */

public class GatheringScreenActivity extends CenesActivity {


    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    public static DrawerLayout mDrawerLayout;

    private FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;

    private ImageView footerHomeIcon, footerReminderIcon, footerAlarmIcon, footerGatheringIcon, footerDiaryIcon;
    public LinearLayout footerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();

        init();

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("push")) {

            final Long eventId = intent.getLongExtra("eventId", 0l);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    new MarkNotificationReadTask().execute(eventId);
                }
            });


            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "push");
            bundle.putLong("eventId", eventId);
            bundle.putString("message", intent.getStringExtra("message"));
            bundle.putString("title", intent.getStringExtra("title"));

//            InvitationFragment invitationFragment = new InvitationFragment();
//            invitationFragment.setArguments(bundle);

            GatheringsFragment gatheringsFragment = new GatheringsFragment();
            gatheringsFragment.setArguments(bundle);

            replaceFragment(gatheringsFragment, null);

        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("gathering_push")) {
            final Long eventId = intent.getLongExtra("eventId", 0l);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    new MarkNotificationReadTask().execute(eventId);
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", intent.getLongExtra("eventId", 0l));

            GatheringsFragment gatheringsFragment = new GatheringsFragment();
            gatheringsFragment.setArguments(bundle);

            replaceFragment(gatheringsFragment, null);
        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("list")) {
            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", intent.getLongExtra("eventId", 0l));

            CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
            createGatheringFragment.setArguments(bundle);

            replaceFragment(createGatheringFragment, null);
        } else if (intent != null && intent.getStringExtra("dataFrom") != null && intent.getStringExtra("dataFrom").equals("fabButton")) {
            replaceFragment(new CreateGatheringFragment(), null);
        } else {
            replaceFragment(new GatheringsFragment(), null);
        }
    }

    public void init() {
        footerLayout = (LinearLayout) findViewById(R.id.rl_footer);
        footerHomeIcon = (ImageView) findViewById(R.id.footer_home_icon);
        footerReminderIcon = (ImageView) findViewById(R.id.footer_reminder_icon);
        footerAlarmIcon = (ImageView) findViewById(R.id.footer_alarm_icon);
        footerGatheringIcon = (ImageView) findViewById(R.id.footer_gathering_icon);
        footerDiaryIcon = (ImageView) findViewById(R.id.footer_diary_icon);

        footerHomeIcon.setOnClickListener(onClickListener);
        footerReminderIcon.setOnClickListener(onClickListener);
        footerAlarmIcon.setOnClickListener(onClickListener);
        footerDiaryIcon.setOnClickListener(onClickListener);

        footerHomeIcon.setImageResource(R.drawable.home_icon_grey);
        footerGatheringIcon.setImageResource(R.drawable.gathering_icon_selected);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.footer_home_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, HomeScreenActivity.class));
                    finish();
                    break;
                case R.id.footer_reminder_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, ReminderActivity.class));
                    finish();
                    break;
                case R.id.footer_alarm_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, AlarmActivity.class));
                    finish();
                    break;
                case R.id.footer_diary_icon:
                    startActivity(new Intent(GatheringScreenActivity.this, DiaryActivity.class));
                    finish();
                    break;
            }
        }
    };

    class MarkNotificationReadTask extends AsyncTask<Long, Long, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(GatheringScreenActivity.this);
            ///progressDialog.setMessage("Loading...");
            //progressDialog.setIndeterminate(false);
            //progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setCancelable(false);
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {

            Long eventId = longs[0];

            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId()+"&notificationTypeId="+eventId;
            JSONObject response = apiManager.markNotificationAsReadByUserIdAndNotificatonId(user, queryStr, GatheringScreenActivity.this);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            //progressDialog.hide();
            //progressDialog.dismiss();
            //progressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void hideFooter() {
        footerLayout.setVisibility(View.GONE);
    }

    public void showFooter() {
        footerLayout.setVisibility(View.VISIBLE);
    }
}