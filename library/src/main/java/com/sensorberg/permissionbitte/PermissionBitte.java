package com.sensorberg.permissionbitte;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Entry point for easy permission requesting.
 */
public class PermissionBitte {

	private static final String TAG = "PERMISSION_BITTE";

	/**
	 * Only ask for something if you really need it.
	 *
	 * @param activity   "this"
	 * @param bitteBitte Callback, on rotation re-attaches the callback to the implementation
	 * @return true, if you should ask for permission, false if you're good to go.
	 */
	public static boolean shouldAsk(FragmentActivity activity, @Nullable BitteBitte bitteBitte) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PermissionBitteImpl bitte = (PermissionBitteImpl) activity
					.getSupportFragmentManager()
					.findFragmentByTag(TAG);
			if (bitte != null) {
				bitte.setYesYouCan(bitteBitte);
			}
			return PermissionBitteImpl.neededPermissions(activity).length > 0;
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (PermissionBitteImpl.neededPermissions(activity).length > 0) {
				PermissionBitteImpl bitte = (PermissionBitteImpl) activity
						.getSupportFragmentManager()
						.findFragmentByTag(TAG);
				if (bitte == null) {
					bitte = new PermissionBitteImpl();
					bitte.setYesYouCan(bitteBitte);
					activity.getSupportFragmentManager()
							.beginTransaction()
							.add(bitte, TAG)
							.commitNowAllowingStateLoss();
				} else {
					bitte.setYesYouCan(bitteBitte);
				}
				return;
			}
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
