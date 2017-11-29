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

public class PermissionBitteImpl extends Fragment {

    private static final int BITTE_LET_ME_PERMISSION = 23;
    private WeakReference<YesYouCan> weakYesYouCan;

    public void setYesYouCan(@Nullable YesYouCan yesYouCan) {
        this.weakYesYouCan = yesYouCan == null ? null : new WeakReference<>(yesYouCan);
    }

    public PermissionBitteImpl() {
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] needed = neededPermissions(getActivity());
        if (needed.length > 0) {
            requestPermissions(needed, BITTE_LET_ME_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BITTE_LET_ME_PERMISSION && permissions.length > 0) {

            YesYouCan yesYouCan = weakYesYouCan == null ? null : weakYesYouCan.get();

            if (yesYouCan != null) {
                boolean denied = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            yesYouCan.askNicer();
                        } else {
                            yesYouCan.noYouCant();
                        }
                        denied = true;
                    }
                }
                if (!denied) {
                    yesYouCan.yesYouCan();
                }
            }
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
