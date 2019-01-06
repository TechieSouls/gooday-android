package com.deploy.leolin.shortcurtbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.deploy.leolin.shortcurtbadger.Badger;
import com.deploy.leolin.shortcurtbadger.ShortcutBadgeException;
import com.deploy.leolin.shortcurtbadger.util.BroadcastHelper;

import java.util.Arrays;
import java.util.List;


/**
 * @author leolin
 */
public class AsusHomeBadger implements Badger {

    private static final String INTENT_ACTION = IntentConstants.DEFAULT_INTENT_ACTION;
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_BADGE_COUNT, badgeCount);
        intent.putExtra(INTENT_EXTRA_PACKAGENAME, componentName.getPackageName());
        intent.putExtra(INTENT_EXTRA_ACTIVITY_NAME, componentName.getClassName());
        intent.putExtra("badge_vip_count", 0);

        BroadcastHelper.sendDefaultIntentExplicitly(context, intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.asus.launcher");
    }
}