package com.budius.permissionbitte.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.budius.permissionbitte.PermissionState
import kotlinx.coroutines.CompletableDeferred

internal class PermissionViewModel(
    private val request: CompletableDeferred<Map<String, PermissionState>>
) : ViewModel() {

    private var once = true

    fun shouldRequest(block: () -> Unit) {
        if (once) {
            once = false
            block()
        }
    }

    fun onResult(
        result: Map<String, Boolean>,
        shouldShowRequestPermissionRationale: (String) -> Boolean
    ) {
        val parsed = result.mapValues {
            when {
                it.value -> PermissionState.GRANTED
                shouldShowRequestPermissionRationale(it.key) -> PermissionState.SHOW_RATIONALE
                else -> PermissionState.DENIED
            }
        }
        request.complete(parsed)
    }

    internal class Factory(
        private val pendingRequest: CompletableDeferred<Map<String, PermissionState>>
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PermissionViewModel(pendingRequest) as T
        }

        companion object {

            private var _instance: Factory? = null

            fun generatePendingRequest(): CompletableDeferred<Map<String, PermissionState>> {
                val newRequest = CompletableDeferred<Map<String, PermissionState>>()
                _instance = Factory(newRequest)
                return newRequest
            }

            val instance: Factory
                get() = _instance ?: throw IllegalStateException(
                    "Factory have not been initialised"
                )
        }
    }
}
