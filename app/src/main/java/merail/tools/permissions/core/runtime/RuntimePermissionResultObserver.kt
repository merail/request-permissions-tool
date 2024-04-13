package merail.tools.permissions.core.runtime

import android.Manifest
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.runtime.RuntimePermissionState
import merail.tools.permissions.core.TAG

internal class RuntimePermissionResultObserver(
    private val activity: ComponentActivity,
    private val preferences: SharedPreferences,
) {
    private val permissionsInformer = PermissionsInformer(activity)

    fun invoke(entry: Map.Entry<String, Boolean>) = entry.key to when {
        entry.isUnknown() -> {
            Log.e(TAG, "Permission \"${entry.key}\" is unknown. Can't handle it")
            RuntimePermissionState.IGNORED
        }
        entry.isInstallTime() -> {
            Log.i(TAG, "Permission \"${entry.key}\" is install-time and normal. Declaring this permission in the manifest is sufficient to obtain it")
            RuntimePermissionState.GRANTED
        }
        entry.isSpecial() -> {
            Log.w(TAG, "Permission \"${entry.key}\" is special. Try using SpecialPermissionRequester to get it")
            RuntimePermissionState.DENIED
        }
        entry.isSystem() -> {
            Log.w(TAG, "Permission \"${entry.key}\" is system. This permission is only granted to system apps")
            RuntimePermissionState.DENIED
        }
        entry.isAccessBackgroundLocationDenied() -> {
            Log.w(TAG, "To be able to request permission \"${entry.key}\", first separately request permission \"${Manifest.permission.ACCESS_COARSE_LOCATION}\" or permission \"${Manifest.permission.ACCESS_FINE_LOCATION}\"")
            RuntimePermissionState.DENIED
        }
        entry.isAccessMediaLocationRequested() -> {
            Log.w(TAG, "\"${entry.key}\" is a special runtime permission without popup dialog")
            RuntimePermissionState.GRANTED
        }
        entry.isBodySensorsBackgroundDenied() -> {
            Log.w(TAG, "To be able to request permission \"${entry.key}\", first separately request permission \"${Manifest.permission.BODY_SENSORS}\"")
            RuntimePermissionState.DENIED
        }
        entry.isReadExternalStorageIncompatibility() -> {
            Log.w(TAG, "Permission \"${entry.key}\" is not available for Android 13+. Use \"READ_MEDIA...\" permissions instead")
            RuntimePermissionState.DENIED
        }
        entry.isReadMediaVisualUserSelectedIncompatibility() -> {
            Log.w(TAG, "Permission \"${entry.key}\" is available only in sequence with \"${Manifest.permission.READ_MEDIA_IMAGES}\" or \"${Manifest.permission.READ_MEDIA_VIDEO}\" permissions")
            RuntimePermissionState.DENIED
        }
        entry.isWriteExternalStorageIncompatibility() -> {
            Log.w(TAG, "Permission \"${entry.key}\" is not available for Android 11+. You can use available directories from Context.getExternalFilesDirs() or request all-files access with \"${Manifest.permission.MANAGE_EXTERNAL_STORAGE}\" permission and SpecialPermissionRequester")
            RuntimePermissionState.DENIED
        }
        entry.isDenied() -> {
            Log.i(TAG, "Permission \"${entry.key}\" is denied")
            preferences.edit().putBoolean(entry.key, true).apply()
            RuntimePermissionState.DENIED
        }
        entry.isPermanentlyDenied() -> {
            Log.i(TAG, "Permission \"${entry.key}\" is permanently denied")
            RuntimePermissionState.PERMANENTLY_DENIED
        }
        entry.isIgnored() -> {
            Log.i(TAG, "Permission \"${entry.key}\" is ignored")
            RuntimePermissionState.IGNORED
        }
        else -> {
            Log.d(TAG, "Permission \"${entry.key}\" is granted")
            RuntimePermissionState.GRANTED
        }
    }


    private fun Map.Entry<String, Boolean>.isUnknown() = permissionsInformer.isUnknown(key)

    private fun Map.Entry<String, Boolean>.isInstallTime() = permissionsInformer.isInstallTime(key)

    private fun Map.Entry<String, Boolean>.isSpecial() = permissionsInformer.isSpecial(key)

    private fun Map.Entry<String, Boolean>.isSystem() = permissionsInformer.isSystem(key)

    private fun Map.Entry<String, Boolean>.isAccessBackgroundLocationDenied(): Boolean {
        return  key == Manifest.permission.ACCESS_BACKGROUND_LOCATION && isDenied()
    }

    private fun Map.Entry<String, Boolean>.isAccessMediaLocationRequested(): Boolean {
        return  key == Manifest.permission.ACCESS_MEDIA_LOCATION
    }

    private fun Map.Entry<String, Boolean>.isBodySensorsBackgroundDenied(): Boolean {
        return  key == Manifest.permission.BODY_SENSORS_BACKGROUND && isDenied()
    }

    private fun Map.Entry<String, Boolean>.isReadExternalStorageIncompatibility(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && key == Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun Map.Entry<String, Boolean>.isReadMediaVisualUserSelectedIncompatibility(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                && key == Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                && isIgnored()
    }

    private fun Map.Entry<String, Boolean>.isWriteExternalStorageIncompatibility(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && key == Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    private fun Map.Entry<String, Boolean>.isDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        )

    private fun Map.Entry<String, Boolean>.isPermanentlyDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && preferences.getBoolean(key, false)

    private fun Map.Entry<String, Boolean>.isIgnored() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && preferences.getBoolean(key, false).not()
}