package com.sensorberg.permissionbitte;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionBitteImpl extends Fragment {

    public PermissionBitteImpl() {
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] need = neededPermissions(getActivity());
        if (need.length > 0) {
            requestPermissions(need, 23);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 23) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
            getFragmentManager().beginTransaction().remove(this).commitNowAllowingStateLoss();
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
