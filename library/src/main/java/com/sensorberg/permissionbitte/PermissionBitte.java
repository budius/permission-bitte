package com.sensorberg.permissionbitte;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class PermissionBitte {

    private static final String TAG = "PERMISSION_BITTE";

    public static boolean shouldAsk(FragmentActivity activity, @Nullable YesYouCan yesYouCan) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionBitteImpl bitte = (PermissionBitteImpl) activity
                    .getSupportFragmentManager()
                    .findFragmentByTag(TAG);
            if (bitte != null) {
                bitte.setYesYouCan(yesYouCan);
            }
            return PermissionBitteImpl.neededPermissions(activity).length > 0;
        } else {
            return false;
        }
    }

    public static void ask(FragmentActivity activity, @Nullable YesYouCan yesYouCan) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionBitteImpl bitte = (PermissionBitteImpl) activity
                    .getSupportFragmentManager()
                    .findFragmentByTag(TAG);
            if (bitte == null) {
                bitte = new PermissionBitteImpl();
                bitte.setYesYouCan(yesYouCan);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(bitte, TAG)
                        .commitNowAllowingStateLoss();
            } else {
                bitte.setYesYouCan(yesYouCan);
            }
        }
    }

    public static void cancel(FragmentActivity activity) {
        Fragment bitte = activity
                .getSupportFragmentManager()
                .findFragmentByTag(TAG);
        if (bitte != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(bitte)
                    .commitAllowingStateLoss();
        }
    }
}
