package com.sensorberg.permissionbitte.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sensorberg.permissionbitte.PermissionBitte;
import com.sensorberg.permissionbitte.YesYouCan;

public class MainActivity extends AppCompatActivity implements YesYouCan {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // simplest implementation is:
        // PermissionBitte.ask(this, null);
        // this will simply block the user until permission is given,
        // bellow is a more "sophisticated" implementation


        if (PermissionBitte.shouldAsk(this, this)) {
            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionBitte.ask(MainActivity.this, MainActivity.this);
                    findViewById(R.id.button).setVisibility(View.GONE);
                }
            });
        } else {
            findViewById(R.id.button).setVisibility(View.GONE);
        }
    }

    @Override
    public void yesYouCan() {
        Toast.makeText(this, "YesYouCan!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noYouCant() {
        Toast.makeText(this, "NNOOOOOoooooo......", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void askNicer() {
        new AlertDialog.Builder(this)
                .setTitle("Bitte")
                .setMessage("I promise not to be a creep")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionBitte.ask(MainActivity.this, MainActivity.this);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
