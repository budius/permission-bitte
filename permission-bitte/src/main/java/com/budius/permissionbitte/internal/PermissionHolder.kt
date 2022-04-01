package com.budius.permissionbitte.internal

import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.budius.permissionbitte.Permission
import com.budius.permissionbitte.PermissionState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PermissionHolder : ActivityLifecycleCallbacksAdapter() {

    val permissionMap = MutableSharedFlow<Map<String, PermissionState>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val permissionsSet: Flow<Set<Permission>> = permissionMap.map { map ->
        map.mapTo(HashSet()) { Permission(it.key, it.value) }
    }.distinctUntilChanged()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (permissionMap.replayCache.isEmpty()) {
            mapPermissions(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        mapPermissions(activity)
    }

    private fun mapPermissions(activity: Activity) {
        val pm = activity.packageManager
        val info = pm.getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)

        val names: Array<String> = info.requestedPermissions
        val flags: IntArray = info.requestedPermissionsFlags
        val protection: List<Int> = names.map { pm.getPermissionInfo(it, 0).protectionCompat() }
        val rationale = names.map { activity.shouldShowRequestPermissionRationale(it) }

        val permissions = ManifestMapper.parsePermissions(names, flags, protection, rationale)
        permissionMap.tryEmit(
            ManifestMapper.mergePermission(
                current = permissionMap.replayCache.firstOrNull(),
                new = permissions
            )
        )
    }
}

private fun PermissionInfo.protectionCompat(): Int {
    return if (Build.VERSION.SDK_INT > 28) {
        this.protectionCompat28()
    } else {
        this.protectionCompatOld()
    }
}

@RequiresApi(Build.VERSION_CODES.P)
private fun PermissionInfo.protectionCompat28(): Int = this.protection

@Suppress("DEPRECATION")
private fun PermissionInfo.protectionCompatOld(): Int = this.protectionLevel
