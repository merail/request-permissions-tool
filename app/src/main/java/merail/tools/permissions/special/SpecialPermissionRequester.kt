package merail.tools.permissions.special

import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity

class SpecialPermissionRequester(private val activity: ComponentActivity) {

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
