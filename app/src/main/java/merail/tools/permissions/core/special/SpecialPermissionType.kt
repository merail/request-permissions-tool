package merail.tools.permissions.core.special

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.Context
import android.content.pm.CrossProfileApps
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import merail.tools.permissions.core.SettingsOpener
import merail.tools.permissions.core.exceptions.SdkIncompatibilityException

internal sealed class SpecialPermissionType(
    open val permission: String,
) {
    abstract fun isGranted(): Boolean

    abstract fun requestPermission()

    @SuppressLint("InlinedApi")
    class InstantAppForegroundService(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE) {
        override fun isGranted() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity.packageManager.isInstantApp

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    @SuppressLint("InlinedApi")
    class InteractAcrossProfiles(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.INTERACT_ACROSS_PROFILES) {
        fun canRequest(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getSystemService(CrossProfileApps::class.java).canRequestInteractAcrossProfiles()
        } else {
            false
        }

        override fun isGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getSystemService(CrossProfileApps::class.java).canInteractAcrossProfiles()
        } else {
            true
        }

        override fun requestPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = activity.getSystemService(CrossProfileApps::class.java)
                    .createRequestInteractAcrossProfilesIntent()
                activity.startActivity(intent)
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
    class LoaderUsageStats(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.LOADER_USAGE_STATS) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.LOADER_USAGE_STATS,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    @SuppressLint("InlinedApi")
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
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
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
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
    class ManageOngoingCalls(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.MANAGE_ONGOING_CALLS) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.MANAGE_ONGOING_CALLS,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    @SuppressLint("InlinedApi")
    class MediaRoutingControl(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.MEDIA_ROUTING_CONTROL) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.MEDIA_ROUTING_CONTROL,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    @SuppressLint("InlinedApi")
    class PackageUsageStats(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.PACKAGE_USAGE_STATS) {
        override fun isGranted(): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
            val appOpsManager = activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                activity.packageName,
            )
            return mode == AppOpsManager.MODE_ALLOWED
        }

        override fun requestPermission() {
            SettingsOpener.openSettings(activity, Settings.ACTION_USAGE_ACCESS_SETTINGS)
        }
    }

    @SuppressLint("InlinedApi")
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
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
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
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
    class SmsFinancialTransactions(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.SMS_FINANCIAL_TRANSACTIONS) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.SMS_FINANCIAL_TRANSACTIONS,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                SettingsOpener.openSettings(activity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
    class TurnScreenOn(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.TURN_SCREEN_ON) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.TURN_SCREEN_ON,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    @SuppressLint("InlinedApi")
    class UseIccAuthWithDeviceIdentifier(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
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
            } else {
                throw SdkIncompatibilityException()
            }
        }
    }

    @SuppressLint("InlinedApi")
    class WriteSystemPreferences(
        val activity: ComponentActivity,
    ) : SpecialPermissionType(Manifest.permission.WRITE_SYSTEM_PREFERENCES) {
        override fun isGranted() = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_SYSTEM_PREFERENCES,
        ) == PackageManager.PERMISSION_GRANTED

        override fun requestPermission() = throw SdkIncompatibilityException()
    }

    class Unknown(
        override val permission: String,
    ) : SpecialPermissionType(permission) {
        override fun isGranted() = false

        override fun requestPermission() = throw SdkIncompatibilityException()
    }
}