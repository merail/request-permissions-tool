package merail.tools.permissions.special

import android.app.AlarmManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import merail.tools.permissions.common.SettingsOpener


sealed class SpecialPermissionType {

    abstract fun isGranted(): Boolean

    abstract fun requestPermission()

    class ManageExternalStorage(
        val activity: ComponentActivity,
    ) : SpecialPermissionType() {
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
    ) : SpecialPermissionType() {
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
    ) : SpecialPermissionType() {
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
    ) : SpecialPermissionType() {
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
    ) : SpecialPermissionType() {
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
    ) : SpecialPermissionType() {
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

    object Unknown : SpecialPermissionType() {
        override fun isGranted() = false

        override fun requestPermission() = Unit
    }
}