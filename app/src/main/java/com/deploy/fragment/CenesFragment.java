package com.deploy.fragment;

import android.support.v4.app.Fragment;

import com.deploy.activity.CenesActivity;

/**
 * Created by rohan on 10/10/17.
 */

public abstract class CenesFragment extends Fragment {

    public CenesActivity getCenesActivity() {
        return (CenesActivity) getActivity();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((CenesActivity) getActivity()).hideSoftInput(getView());
        }
    }
}