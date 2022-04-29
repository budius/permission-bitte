package com.budius.permissionbitte.sample

import android.app.Application
import com.budius.permissionbitte.PermissionBitte

class SampleApp : Application() {

    lateinit var permissionBitte: PermissionBitte

    override fun onCreate() {
        super.onCreate()
        permissionBitte = PermissionBitte.install(this)
    }
}
