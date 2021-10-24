package com.budius.permissionbitte.internal

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import androidx.annotation.RequiresApi
import com.budius.permissionbitte.Permission
import com.budius.permissionbitte.PermissionBitte
import com.budius.permissionbitte.PermissionState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class PermissionBitteImpl(private val app: Application) :
    PermissionBitte,
    ActivityTracker.Listener {

    private var isAlive = true

    private val _permissions = MutableSharedFlow<Map<String, PermissionState>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    internal val activityTracker = ActivityTracker(this)

    override val permissions: Flow<Set<Permission>> = _permissions.map { map ->
        map.mapTo(HashSet(), { Permission(it.key, it.value) })
    }.distinctUntilChanged()

    override suspend fun request(permissions: Set<String>) {
        if (isAlive.not()) {
            throw IllegalStateException(
                "Attempt to request permission after uninstall() was called"
            )
        }

        val request = PermissionViewModel.Factory.generatePendingRequest()
        activityTracker.activity
            .supportFragmentManager
            .beginTransaction()
            .add(PermissionFragment.create(permissions.toTypedArray()), PermissionFragment.TAG)
            .commitNow()

        val result: Map<String, PermissionState> = request.await()

        _permissions.tryEmit(
            ManifestMapper.mergePermission(
                current = _permissions.replayCache.firstOrNull(),
                new = result
            )
        )
    }

    override fun uninstall() {
        isAlive = false
        app.unregisterActivityLifecycleCallbacks(activityTracker)
    }

    override fun onActivity(activity: Activity) {
        val pm = activity.packageManager
        val info = pm.getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)

        val names: Array<String> = info.requestedPermissions
        val flags: IntArray = info.requestedPermissionsFlags
        val protection: List<Int> = names.map { pm.getPermissionInfo(it, 0).protectionCompat() }
        val rationale = names.map { activity.shouldShowRequestPermissionRationale(it) }

        val permissions = ManifestMapper.parsePermissions(names, flags, protection, rationale)
        _permissions.tryEmit(
            ManifestMapper.mergePermission(
                current = _permissions.replayCache.firstOrNull(),
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
