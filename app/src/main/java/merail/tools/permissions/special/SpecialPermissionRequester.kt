package merail.tools.permissions.special

import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import merail.tools.permissions.common.SettingsOpener

class SpecialPermissionRequester(
    private val activity: AppCompatActivity,
) {

    fun checkSystemAlertWindowPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(activity)
    } else {
        true
    }

    fun requestSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        }
    }
}
