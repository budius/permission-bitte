package com.sensorberg.permissionbitte.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sensorberg.permissionbitte.PermissionBitte;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PermissionBitte.shouldAsk(this)) {
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionBitte.ask(MainActivity.this);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionBitte.shouldAsk(this)) {
            findViewById(R.id.button).setVisibility(View.GONE);
        }
    }
}
