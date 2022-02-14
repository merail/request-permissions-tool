package me.rail.requestpermissionstool;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionRequester {
    private final Activity activity;
    private static final int REQUEST_CODE_FOR_PERMISSIONS = 1;
    private ArrayList<String> permissionsForRequest;

    public PermissionRequester(Activity activity) {
        this.activity = activity;
    }

    public Boolean checkSelfPermissions(String[] permissions) {
        permissionsForRequest = new ArrayList<>();
        for (String permission : permissions) {
            checkSelfPermission(permission);
        }

        if (!permissionsForRequest.isEmpty()) {
            requestPermissions();
            return false;
        } else {
            return true;
        }
    }

    private void checkSelfPermission(String permission) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                permission
        ) != PackageManager.PERMISSION_GRANTED) {
            permissionsForRequest.add(permission);
        }
    }

    private void requestPermissions() {
        String[] permissionsForRequestArray = convertArrayListToArray(permissionsForRequest);
        ActivityCompat.requestPermissions(
                activity, permissionsForRequest.toArray(permissionsForRequestArray),
                REQUEST_CODE_FOR_PERMISSIONS
        );
    }

    public ArrayList<String> onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        if (requestCode == REQUEST_CODE_FOR_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permissions[i]);
                    }
                }
            }
        }
        return notGrantedPermissions;
    }

    public Boolean shouldShowRequestPermissionRationale(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
        );
    }

    public String[] convertArrayListToArray(ArrayList<String> arrayList) {
        String[] array = new String[arrayList.size()];
        array = arrayList.toArray(array);
        return array;
    }

    public ArrayList<String> getPermissionsForRationale(ArrayList<String> notGrantedPermissions) {
        ArrayList<String> permissionsForRationale = new ArrayList<>();
        for (String notGrantedPermission : notGrantedPermissions) {
            if (shouldShowRequestPermissionRationale(notGrantedPermission)) {
                permissionsForRationale.add(notGrantedPermission);
            }
        }
        return permissionsForRationale;
    }

    public ArrayList<String> getDeniedPermissions(ArrayList<String> notGrantedPermissions) {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String notGrantedPermission : notGrantedPermissions) {
            if (!shouldShowRequestPermissionRationale(notGrantedPermission)) {
                deniedPermissions.add(notGrantedPermission);
            }
        }
        return deniedPermissions;
    }
}
