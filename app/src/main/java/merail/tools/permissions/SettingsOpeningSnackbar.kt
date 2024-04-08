package merail.tools.permissions

import android.app.Activity
import android.provider.Settings
import android.view.View
import androidx.annotation.Keep
import com.google.android.material.snackbar.Snackbar
import merail.tools.permissions.core.common.SettingsOpener

@Keep
class SettingsOpeningSnackbar(
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
