package merail.tools.permissions.runtime

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.app.ActivityCompat
import merail.tools.permissions.common.Utils.TAG
import merail.tools.permissions.common.WrongTimeInitializationException
import merail.tools.permissions.inform.PermissionsInformer

class RuntimePermissionRequester(
    private val activity: ComponentActivity,
    private val requestedPermissions: Array<String>,
) {
    companion object {
        private const val PERMISSIONS_PREFERENCES = "PERMISSIONS_PREFERENCES"
    }

    private val permissionsInformer = PermissionsInformer(activity)

    private val preferences = try {
        activity.getSharedPreferences(PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE)
    } catch (exception: Exception) {
        throw WrongTimeInitializationException()
    }

    private var onRuntimePermissionsRequestResult: ((Map<String, RuntimePermissionState>) -> Unit)? = null

    private val requestPermissionLauncher = try {
        activity.registerForActivityResult(
            RequestMultiplePermissions(),
        ) { permissionsGrants ->
            onRequestPermissionsResult(permissionsGrants)
        }
    } catch (exception: Exception) {
        throw WrongTimeInitializationException()
    }

    constructor(
        activity: ComponentActivity,
        requestedPermission: String,
    ) : this(activity, arrayOf(requestedPermission))

    fun isPermissionGranted(
        permission: String,
    ) = ActivityCompat.checkSelfPermission(
        activity,
        permission,
    ) == PackageManager.PERMISSION_GRANTED

    fun areAllPermissionsGranted() = requestedPermissions.none { permission ->
        isPermissionGranted(permission).not()
    }

    fun requestPermissions(
        onRuntimePermissionsRequestResult: ((Map<String, RuntimePermissionState>) -> Unit)? = null,
    ) {
        this.onRuntimePermissionsRequestResult = onRuntimePermissionsRequestResult
        requestPermissionLauncher.launch(requestedPermissions)
    }

    private fun onRequestPermissionsResult(
        permissionsGrants: Map<String, Boolean>,
    ) {
        val permissionsRequestResult = permissionsGrants.entries.associate { entry ->
            entry.key to when {
                permissionsInformer.isUnknown(entry.key) -> {
                    Log.e(TAG, "Permission \"${entry.key}\" is unknown. Can't handle it")
                    RuntimePermissionState.IGNORED
                }
                permissionsInformer.isInstallTime(entry.key) -> {
                    Log.i(TAG, "Permission \"${entry.key}\" is install-time and normal. Declaring this permission in the manifest is sufficient to obtain it")
                    RuntimePermissionState.GRANTED
                }
                permissionsInformer.isSpecial(entry.key) -> {
                    Log.w(TAG, "Permission \"${entry.key}\" is special. Try using SpecialPermissionRequester to get it")
                    RuntimePermissionState.DENIED
                }
                permissionsInformer.isSystem(entry.key) -> {
                    Log.w(TAG, "Permission \"${entry.key}\" is system. This permission is only granted to system apps")
                    RuntimePermissionState.DENIED
                }
                entry.key == Manifest.permission.ACCESS_BACKGROUND_LOCATION && entry.isPermissionDenied() -> {
                    Log.w(TAG, "To be able to request permission \"${entry.key}\", first separately request permission \"${Manifest.permission.ACCESS_COARSE_LOCATION}\" or permission \"${Manifest.permission.ACCESS_FINE_LOCATION}\"")
                    RuntimePermissionState.DENIED
                }
                entry.key == Manifest.permission.ACCESS_MEDIA_LOCATION -> {
                    Log.w(TAG, "\"${entry.key}\" is a special runtime permission without popup dialog")
                    RuntimePermissionState.GRANTED
                }
                entry.key == Manifest.permission.BODY_SENSORS_BACKGROUND && entry.isPermissionDenied() -> {
                    Log.w(TAG, "To be able to request permission \"${entry.key}\", first separately request permission \"${Manifest.permission.BODY_SENSORS}\"")
                    RuntimePermissionState.DENIED
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (entry.key == Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Log.w(TAG, "Permission \"${entry.key}\" is not available for Android 13+. Use \"READ_MEDIA...\" permissions instead")
                    RuntimePermissionState.DENIED
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && (entry.key == Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) && entry.isPermissionIgnored() -> {
                    Log.w(TAG, "Permission \"${entry.key}\" is available only in sequence with \"${Manifest.permission.READ_MEDIA_IMAGES}\" or \"${Manifest.permission.READ_MEDIA_VIDEO}\" permissions")
                    RuntimePermissionState.DENIED
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && (entry.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    Log.w(TAG, "Permission \"${entry.key}\" is not available for Android 11+. You can use available directories from Context.getExternalFilesDirs() or request all-files access with \"${Manifest.permission.MANAGE_EXTERNAL_STORAGE}\" permission and SpecialPermissionRequester")
                    RuntimePermissionState.DENIED
                }
                entry.isPermissionDenied() -> {
                    Log.i(TAG, "Permission \"${entry.key}\" is denied")
                    preferences.edit().putBoolean(entry.key, true).apply()
                    RuntimePermissionState.DENIED
                }
                entry.isPermissionPermanentlyDenied() -> {
                    Log.i(TAG, "Permission \"${entry.key}\" is permanently denied")
                    RuntimePermissionState.PERMANENTLY_DENIED
                }
                entry.isPermissionIgnored() -> {
                    Log.i(TAG, "Permission \"${entry.key}\" is ignored")
                    RuntimePermissionState.IGNORED
                }
                else -> {
                    Log.d(TAG, "Permission \"${entry.key}\" is granted")
                    RuntimePermissionState.GRANTED
                }
            }
        }
        onRuntimePermissionsRequestResult?.invoke(permissionsRequestResult)
    }

    private fun Map.Entry<String, Boolean>.isPermissionDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        )

    private fun Map.Entry<String, Boolean>.isPermissionPermanentlyDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && preferences.getBoolean(key, false)

    private fun Map.Entry<String, Boolean>.isPermissionIgnored() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && preferences.getBoolean(key, false).not()
}
