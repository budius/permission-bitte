package com.budius.permissionbitte.internal

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

internal class ActivityTracker(
    private val listener: Listener
) : Application.ActivityLifecycleCallbacks {

    private var firstTime = true

    private var reference: WeakReference<FragmentActivity>? = null
    val activity: FragmentActivity
        get() = reference?.get() ?: throw IllegalStateException(
            "Activity reference is not initialised yet"
        )

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (firstTime) {
            firstTime = false
            listener.onActivity(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        // no-op
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is FragmentActivity) reference = WeakReference(activity)
        listener.onActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        // no-op
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        // no-op
    }

    interface Listener {
        fun onActivity(activity: Activity)
    }
}
