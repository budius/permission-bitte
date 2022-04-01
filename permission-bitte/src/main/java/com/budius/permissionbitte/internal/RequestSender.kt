package com.budius.permissionbitte.internal

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.budius.permissionbitte.PermissionState
import com.budius.permissionbitte.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.ref.WeakReference

class RequestSender(
    private val permissionMap: MutableSharedFlow<Map<String, PermissionState>>
) : ActivityLifecycleCallbacksAdapter() {

    private var resumed: WeakReference<ComponentActivity>? = null
    private var ongoing: CompletableDeferred<Map<String, PermissionState>>? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        (activity as? ComponentActivity)?.apply {
            rootView().setTag(R.id.permission_bitte_result_launcher, requestListener(this))
        }
    }

    override fun onActivityResumed(activity: Activity) {
        (activity as? ComponentActivity)?.apply {
            resumed = WeakReference(this)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumed = null
    }


    suspend fun request(permissions: Set<String>) = withRequest { request ->
        findLauncher().launch(permissions.toTypedArray())
        permissionMap.emit(
            ManifestMapper.mergePermission(
                current = permissionMap.replayCache.firstOrNull(),
                new = request.await()
            )
        )
    }

    private fun findLauncher(): ActivityResultLauncher<Array<String>> {
        val launcher = resumed?.get()?.rootView()?.getTag(R.id.permission_bitte_result_launcher)
        return launcher as? ActivityResultLauncher<Array<String>>
            ?: throw IllegalStateException("No ComponentActivity currently resumed")
    }

    private fun withRequest(call: (CompletableDeferred<Map<String, PermissionState>>) -> Unit) {
        if (ongoing != null) {
            throw IllegalStateException("Only one ongoing request at a time")
        }
        try {
            call(CompletableDeferred<Map<String, PermissionState>>().also { ongoing = it })
        } finally {
            ongoing = null
        }
    }

    private fun requestListener(activity: ComponentActivity): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            val parsed = map.mapValues {
                when {
                    it.value -> PermissionState.GRANTED
                    activity.shouldShowRequestPermissionRationale(it.key) -> PermissionState.SHOW_RATIONALE
                    else -> PermissionState.DENIED
                }
            }
            ongoing?.complete(parsed)
        }
    }
}

private fun ComponentActivity.rootView(): View {
    return findViewById(android.R.id.content)
}
