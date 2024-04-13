package merail.tools.permissions

import android.app.Activity
import android.provider.Settings
import android.view.View
import com.google.android.material.snackbar.Snackbar
import merail.tools.permissions.core.common.SettingsOpener

class SettingsSnackbar(
    private val activity: Activity,
    private val view: View,
) {
    fun showSnackbar(
        text: String,
        actionName: String,
    ) = Snackbar.make(
        view,
        text,
        Snackbar.LENGTH_LONG,
    ).setAction(actionName) {
        SettingsOpener.openSettings(
            activity,
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        )
    }.show()
}
