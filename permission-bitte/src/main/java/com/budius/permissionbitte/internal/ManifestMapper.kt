package com.budius.permissionbitte.internal

import android.content.pm.PackageInfo
import android.content.pm.PermissionInfo
import com.budius.permissionbitte.PermissionState
import com.budius.permissionbitte.PermissionState.*

internal object ManifestMapper {

    internal fun parsePermissions(
        names: Array<String>,
        flags: IntArray,
        protection: List<Int>,
        rationale: List<Boolean>
    ) = names.mapIndexedNotNull { index, name ->
        val state = stateOf(flags[index], protection[index], rationale[index])
        state?.let { name to it }
    }.associate { it }

    private fun stateOf(flags: Int, protection: Int, showRationale: Boolean): PermissionState? {
        return when {
            protection != PermissionInfo.PROTECTION_DANGEROUS -> null
            ((flags and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) -> GRANTED
            showRationale -> SHOW_RATIONALE
            else -> REQUEST_PERMISSION
        }
    }

    fun mergePermission(
        current: Map<String, PermissionState>?,
        new: Map<String, PermissionState>
    ): Map<String, PermissionState> {

        if (current == null) {
            return new
        }

        // current is guaranteed to have ALL the values, so it should be the base of the operation
        return current.mapValues { entry ->
            val newState: PermissionState? = new[entry.key]
            if (newState == null) {
                entry.value
            } else {
                combineStates(entry.value, newState)
            }
        }
    }

    private fun combineStates(current: PermissionState, new: PermissionState): PermissionState {
        return when {
            new == GRANTED -> GRANTED
            new == SHOW_RATIONALE -> SHOW_RATIONALE
            new == DENIED || current == DENIED -> DENIED
            else -> new
        }
    }
}
