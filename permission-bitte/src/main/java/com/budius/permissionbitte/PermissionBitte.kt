package com.budius.permissionbitte

import android.app.Application
import com.budius.permissionbitte.internal.PermissionBitteImpl
import kotlinx.coroutines.flow.Flow

/**
 * TODO: document
 */
interface PermissionBitte {

    /**
     * TODO: document
     */
    val permissions: Flow<Set<Permission>>

    /**
     * TODO: document
     */
    suspend fun request(permissions: Set<String>)

    /**
     * TODO: document
     */
    fun uninstall()

    companion object {
        /**
         * TODO: document
         */
        fun install(app: Application): PermissionBitte {
            val impl = PermissionBitteImpl(app)
            app.registerActivityLifecycleCallbacks(impl.activityTracker)
            return impl
        }
    }
}

/**
 * TODO: document
 */
data class Permission(val name: String, val state: PermissionState)

/**
 * TODO: document
 */
enum class PermissionState {
    GRANTED, REQUEST_PERMISSION, SHOW_RATIONALE, DENIED
}
