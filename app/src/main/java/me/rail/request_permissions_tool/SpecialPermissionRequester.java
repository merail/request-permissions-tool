package me.rail.request_permissions_tool;

import android.provider.Settings;

import androidx.activity.ComponentActivity;

public class SpecialPermissionRequester {

    private final ComponentActivity activity;

    public SpecialPermissionRequester(ComponentActivity activity) {
        this.activity = activity;
    }

    public Boolean checkSystemAlertWindowPermission() {
        return Settings.canDrawOverlays(activity);
    }

    public void requestSystemAlertWindowPermission() {
        SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
    }
}
