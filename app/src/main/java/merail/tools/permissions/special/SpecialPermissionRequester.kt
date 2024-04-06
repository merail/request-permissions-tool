package merail.tools.permissions.special

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import merail.tools.permissions.R
import merail.tools.permissions.common.SettingsOpener

class SpecialPermissionRequester(
    activity: ComponentActivity,
    requestedPermission: String,
) {
    private val specialPermissionType = when (requestedPermission) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> SpecialPermissionType.ManageExternalStorage(activity)
        Manifest.permission.MANAGE_MEDIA -> SpecialPermissionType.ManageMedia(activity)
        Manifest.permission.REQUEST_INSTALL_PACKAGES -> SpecialPermissionType.RequestInstallPackages(activity)
        Manifest.permission.SCHEDULE_EXACT_ALARM -> SpecialPermissionType.ScheduleExactAlarm(activity)
        Manifest.permission.SYSTEM_ALERT_WINDOW ->  SpecialPermissionType.SystemAlertWindow(activity)
        Manifest.permission.WRITE_SETTINGS -> SpecialPermissionType.WriteSetting(activity)
        else -> SpecialPermissionType.Unknown
    }

    fun isPermissionGranted() = specialPermissionType.isGranted()

    fun requestPermission() = specialPermissionType.requestPermission()
}
