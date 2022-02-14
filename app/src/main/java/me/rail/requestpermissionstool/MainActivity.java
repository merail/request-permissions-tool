package me.rail.requestpermissionstool;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALENDAR
    };
    private ArrayList<String> notGrantedPermissions;

    private PermissionRequester permissionRequester;

    private Button getPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionRequester = new PermissionRequester(this);

        getPermissions = findViewById(R.id.requestPermission);
        setOnGetPermissionsClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean isAllPermissionsGranted = permissionRequester.checkSelfPermissions(permissions);
        if (isAllPermissionsGranted) {
            getPermissions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> notGrantedPermissions =
                permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (notGrantedPermissions.isEmpty()) {
            getPermissions.setVisibility(View.GONE);
        } else {
            this.notGrantedPermissions = notGrantedPermissions;
            getPermissions.setVisibility(View.VISIBLE);
        }
    }

    private void setOnGetPermissionsClickListener() {
        getPermissions.setOnClickListener(view -> {
            ArrayList<String> permissionsForRationale = permissionRequester.getPermissionsForRationale(notGrantedPermissions);
            if (!permissionsForRationale.isEmpty()) {
                permissionRequester.checkSelfPermissions(permissionRequester.convertArrayListToArray(permissionsForRationale));
            }

            ArrayList<String> deniedPermissions = permissionRequester.getDeniedPermissions(notGrantedPermissions);
            if (!deniedPermissions.isEmpty()) {
                GoingToSettingsSnackbar goingToSettingsSnackbar = new GoingToSettingsSnackbar(this, view);
                goingToSettingsSnackbar.showSnackbar("You must grant permissions in Settings!", "Settings");
            }
        });
    }
}