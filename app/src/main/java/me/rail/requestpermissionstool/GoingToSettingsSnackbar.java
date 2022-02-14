package me.rail.requestpermissionstool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class GoingToSettingsSnackbar {
    private final String scheme = "package";
    private final Activity activity;
    private final View view;

    public GoingToSettingsSnackbar(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public void showSnackbar(String text, String actionName) {
        Snackbar goingToSettingsSnackbar = createSnackbar(text);

        goingToSettingsSnackbar = setAction(goingToSettingsSnackbar, actionName);

        goingToSettingsSnackbar.show();
    }

    private Snackbar createSnackbar(String text) {
        return Snackbar.make(
                view,
                text,
                Snackbar.LENGTH_LONG
        );
    }

    private Snackbar setAction(Snackbar snackbar, String actionName) {
        return snackbar.setAction(actionName, view1 -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts(scheme, activity.getPackageName(), null));
            activity.startActivity(intent);
        });
    }
}
