package com.indooratlas.android.sdk.examples;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.indooratlas.android.sdk.examples.wayfinding.WayfindingOverlayActivity;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Main entry into IndoorAtlas examples. Displays all example activities as list and opens selected
 * activity. Activities are populated from AndroidManifest metadata.
 */
public class ListExamplesActivity extends AppCompatActivity {

    private static final String TAG = "IAExample";

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next = (Button) findViewById(R.id.Button01);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), WayfindingOverlayActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });

        if (!isSdkConfigured()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.configuration_incomplete_title)
                    .setMessage(R.string.configuration_incomplete_message)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
            return;
        }

        ensurePermissions();

    }

    /**
     * Checks that we have access to required information, if not ask for users permission.
     */
    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // We dont have access to FINE_LOCATION (Required by Google Maps example)
            // IndoorAtlas SDK has minimum requirement of COARSE_LOCATION to enable WiFi scanning
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.location_permission_request_title)
                        .setMessage(R.string.location_permission_request_rationale)
                        .setPositiveButton(R.string.permission_button_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "request permissions");
                                ActivityCompat.requestPermissions(ListExamplesActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ListExamplesActivity.this,
                                        R.string.location_permission_denied_message,
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            } else {

                // ask user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:

                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.location_permission_denied_message,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    /**
     * Adapter for example activities.
     */


    private boolean isSdkConfigured() {
        return !"api-key-not-set".equals(getString(R.string.indooratlas_api_key))
                && !"api-secret-not-set".equals(getString(R.string.indooratlas_api_secret));
    }


}
