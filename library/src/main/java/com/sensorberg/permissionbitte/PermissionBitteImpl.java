package com.sensorberg.permissionbitte;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * DO NOT USE THIS FRAGMENT DIRECTLY!
 * It's only here because fragments have to be public
 */
public class PermissionBitteImpl extends Fragment {

	private static final int BITTE_LET_ME_PERMISSION = 23;
	private WeakReference<BitteBitte> weakYesYouCan;

	public void setYesYouCan(@Nullable BitteBitte bitteBitte) {
		this.weakYesYouCan = bitteBitte == null ? null : new WeakReference<>(bitteBitte);
	}

	public PermissionBitteImpl() {
		setRetainInstance(true);
	}

	@Override public void onResume() {
		super.onResume();
		String[] needed = neededPermissions(getActivity());
		if (needed.length > 0) {
			requestPermissions(needed, BITTE_LET_ME_PERMISSION);
		} else {
			// this shouldn't happen, but just to be sure
			getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
		}
	}

	@Override public void onRequestPermissionsResult(int requestCode,
													 @NonNull String[] permissions,
													 @NonNull int[] grantResults) {
		if (requestCode == BITTE_LET_ME_PERMISSION && permissions.length > 0) {

			BitteBitte bitteBitte = weakYesYouCan == null ? null : weakYesYouCan.get();

			if (bitteBitte != null) {
				boolean denied = false;
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						if (shouldShowRequestPermissionRationale(permissions[i])) {
							bitteBitte.askNicer();
						} else {
							bitteBitte.noYouCant();
						}
						denied = true;
					}
				}
				if (!denied) {
					bitteBitte.yesYouCan();
				}
			}
			getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @NonNull
	static String[] neededPermissions(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
		} catch (PackageManager.NameNotFoundException e) { /* */ }
		List<String> needed = new ArrayList<>();
		if (info != null &&
			info.requestedPermissions != null &&
			info.requestedPermissionsFlags != null) {
			for (int i = 0; i < info.requestedPermissions.length; i++) {
				int flags = info.requestedPermissionsFlags[i];
				if ((flags & PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
					needed.add(info.requestedPermissions[i]);
				}
			}
		}
		return needed.toArray(new String[needed.size()]);
	}
}
