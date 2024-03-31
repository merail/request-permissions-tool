package merail.tools.permissions.special

import android.app.Activity
import android.provider.Settings
import android.view.View
import com.google.android.material.snackbar.Snackbar

class GoingToSettingsSnackbar(
    private val activity: Activity,
    private val view: View,
) {

    fun showSnackbar(
        text: String,
        actionName: String,
    ) = createSnackbar(text)
        .setAction(actionName)
        .show()

    private fun createSnackbar(text: String) = Snackbar.make(
        view,
        text,
        Snackbar.LENGTH_LONG,
    )

    private fun Snackbar.setAction(
        actionName: String,
    ) = setAction(actionName) {
        SettingsOpener.openSettings(
            activity,
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        )
    }
}
