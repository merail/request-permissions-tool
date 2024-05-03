package merail.tools.permissions.core.special

import android.Manifest
import android.app.AlarmManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import merail.tools.permissions.core.common.SettingsOpener


internal sealed class SpecialPermissionType(
    open val permission: String,
) {

    abstract fun isGranted(): Boolean

    abstract fun requestPermission()

    class ManageExternalStorage(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.MANAGE_EXTERNAL_STORAGE) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            }
        }
    }

    class ManageMedia(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.MANAGE_MEDIA) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaStore.canManageMedia(activity)
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsOpener.openSettings(activity, Settings.ACTION_REQUEST_MANAGE_MEDIA)
            }
        }
    }

    class RequestInstallPackages(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.REQUEST_INSTALL_PACKAGES) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.packageManager.canRequestPackageInstalls()
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            }
        }
    }

    class ScheduleExactAlarm(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.SCHEDULE_EXACT_ALARM) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(activity, AlarmManager::class.java)
            alarmManager?.canScheduleExactAlarms() == true
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsOpener.openSettings(activity, Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            }
        }
    }

    class SystemAlertWindow(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.SYSTEM_ALERT_WINDOW) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            }
        }
    }

    class WriteSettings(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.WRITE_SETTINGS) {
        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(activity)
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_WRITE_SETTINGS)
            }
        }
    }

    class Unknown(
        override val permission: String,
    ) : SpecialPermissionType(permission) {
        override fun isGranted() = false

        override fun requestPermission() = Unit
    }
}