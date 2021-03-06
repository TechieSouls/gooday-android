package com.deploy.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.activity.CenesBaseActivity;
import com.deploy.activity.DiaryActivity;
import com.deploy.activity.GatheringScreenActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.util.RoundedImageView;

import java.util.List;

/**
 * Created by mandeep on 12/9/18.
 */

public class AboutUsFragment  extends CenesFragment {

    public final static String TAG = "AboutUsFragment";

    RelativeLayout rlVersionUpdateButton;
    TextView tvVersionText;
    private RoundedImageView homeProfilePic;
    private ImageView homeIcon;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;

    private User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_about_us, container, false);

        homeProfilePic = (RoundedImageView) v.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) v.findViewById(R.id.home_icon);

        setFragmentManager();


        rlVersionUpdateButton = (RelativeLayout) v.findViewById(R.id.rl_version_update_btn);
        tvVersionText = (TextView) v.findViewById(R.id.tv_version_text);

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.circleCrop();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            Glide.with(getActivity()).load(user.getPicture()).apply(requestOptions).into(homeProfilePic);
        }


        homeIcon.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
        rlVersionUpdateButton.setOnClickListener(onClickListener);

        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            tvVersionText.setText("Version "+info.versionName);
            /*Toast.makeText(getActivity(),
                    "PackageName = " + info.packageName + "\nVersionCode = "
                            + info.versionCode + "\nVersionName = "
                            + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();*/

        } catch (Exception e) {
            e.printStackTrace();
        }


        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.home_icon:
                    getActivity().onBackPressed();
                    break;

                case R.id.home_profile_pic:
                    if (getActivity() instanceof CenesBaseActivity) {
                        ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;
                case R.id.rl_version_update_btn:
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.about_us_version_update_link)));
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getActivity() instanceof GatheringScreenActivity) {
                ((GatheringScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof DiaryActivity) {
                ((DiaryActivity) getActivity()).hideFooter();
            }
        } catch (Exception e) {

        }
    }


    FragmentManager fragmentManager;

    public Fragment getFragmentPresentInBackStack() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && (fragment instanceof NavigationFragment))
                    return fragment;
            }
        }
        return null;
    }

    public void setFragmentManager() {
        if (getActivity() instanceof GatheringScreenActivity) {
            fragmentManager = ((GatheringScreenActivity) getActivity()).fragmentManager;
        } else if (getActivity() instanceof DiaryActivity) {
            fragmentManager = ((DiaryActivity) getActivity()).fragmentManager;
        }
    }
}
