package com.sensorberg.permissionbitte;

import android.os.Build;
import android.support.v4.app.FragmentActivity;

public class PermissionBitte {

    private static final String TAG = "PERMISSION_BITTE";

    public static boolean shouldAsk(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PermissionBitteImpl.neededPermissions(activity).length > 0;
        } else {
            return false;
        }
    }

    public static void ask(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.getSupportFragmentManager().findFragmentByTag(TAG) == null) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(new PermissionBitteImpl(), TAG)
                        .commitNowAllowingStateLoss();
            }
        }
    }
}
