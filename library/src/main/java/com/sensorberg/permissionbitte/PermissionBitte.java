package com.sensorberg.permissionbitte;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Entry point for easy permission requesting.
 */
public class PermissionBitte {

  private static final String TAG = "PERMISSION_BITTE";

  /**
   * Register {@link BitteBitte} listener.
   * Always call this during Activity.onCreate()
   *
   * @param activity   "this"
   * @param bitteBitte Callback, on rotation re-attaches the callback to the implementation
   */
  public static void registerCallback(FragmentActivity activity, @Nullable BitteBitte bitteBitte) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PermissionBitteImpl fragment = (PermissionBitteImpl) activity
              .getSupportFragmentManager()
              .findFragmentByTag(TAG);
      if (fragment != null) {
        fragment.setBitteBitte(bitteBitte);
      }
    }
  }

  /**
   * Check if you need to ask for permission.
   *
   * @param context a Context
   * @return true when you should ask for permission, false otherwise
   */
  public static boolean shouldAsk(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return PermissionBitteImpl.neededPermissions(context).length > 0;
    } else {
      return false;
    }
  }

  /**
   * Ask for the permission. Which permission? Anything you register on your manifest that needs it.
   * It is safe to call this every time without querying `shouldAsk`.
   * In case you call `ask` without needing any permission, bitteBitte will immediately receive `yesYouCan()`
   *
   * @param activity   "this"
   * @param bitteBitte Callback, so you know when it's all good.
   */
  public static void ask(FragmentActivity activity, @Nullable BitteBitte bitteBitte) {
    if (shouldAsk(activity)) {
      PermissionBitteImpl bitte = (PermissionBitteImpl) activity
              .getSupportFragmentManager()
              .findFragmentByTag(TAG);
      if (bitte == null) {
        bitte = new PermissionBitteImpl();
        bitte.setBitteBitte(bitteBitte);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(bitte, TAG)
                .commitNowAllowingStateLoss();
      } else {
        bitte.setBitteBitte(bitteBitte);
      }
      return;
    }

    if (bitteBitte != null) {
      bitteBitte.yesYouCan();
    }
  }

  /**
   * Just a helper methods in case the user blocks permission.
   * It goes to your application settings page for the user to enable permission again.
   *
   * @param activity "this"
   */
  public static void goToSettings(FragmentActivity activity) {
    activity.startActivity(new Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.getPackageName(), null)));
  }
}
