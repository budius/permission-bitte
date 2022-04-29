package com.budius.permissionbitte.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.camera).setOnClickListener {
            lifecycleScope.launch {
                Log.d("bitte", "Requesting camera")
                bitte().request(setOf(Manifest.permission.CAMERA))
                Log.d("bitte", "Camera request complete")
            }
        }

        findViewById<Button>(R.id.locations).setOnClickListener {
            lifecycleScope.launch {
                Log.d("bitte", "Requesting location")
                bitte().request(
                    setOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                Log.d("bitte", "Location request complete")
            }
        }

        lifecycleScope.launch {
            bitte().permissions.collect { set ->
                Log.d("bitte", "Permission set size ${set.size}")
                set.forEach { Log.d("bitte", it.toString()) }
                Log.d("bitte", "eof =========")
            }
        }
    }

    private fun bitte() = (application as SampleApp).permissionBitte
}
