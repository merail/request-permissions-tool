package merail.tools.permissions.runtime

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

const val TAG = "MERAIL_TOOLS"

class RuntimePermissionRequester(
    private val activity: AppCompatActivity,
    private val permissionsForRequest: Array<String>,
) {
    private var onPermissionsRequest: ((Map<RuntimePermission, RuntimePermissionState>) -> Unit)? = null

    private val sharedPrefs = activity.getSharedPreferences("test", Context.MODE_PRIVATE)

    private val requestPermissionLauncher = activity.registerForActivityResult(
        RequestMultiplePermissions(),
    ) { permissionsGrants ->
        val permissionsRequestResult = permissionsGrants.entries.associate { entry ->
            RuntimePermission(entry.key) to when {
                entry.isPermissionDenied() -> {
                    Log.d(TAG, "Permission ${entry.key} is denied")
                    sharedPrefs.edit().putBoolean(entry.key, true).apply()
                    RuntimePermissionState.DENIED
                }
                entry.isPermissionPermanentlyDenied() -> {
                    Log.d(TAG, "Permission ${entry.key} is permanently denied")
                    RuntimePermissionState.PERMANENTLY_DENIED
                }
                entry.isPermissionIgnored() -> {
                    Log.d(TAG, "Permission ${entry.key} is ignored")
                    RuntimePermissionState.IGNORED
                }
                else -> {
                    Log.d(TAG, "Permission ${entry.key} is granted")
                    RuntimePermissionState.GRANTED
                }
            }
        }
        onPermissionsRequest?.invoke(permissionsRequestResult)
    }

    fun areAllPermissionsGranted() = permissionsForRequest.none { permission ->
        isPermissionGranted(permission).not()
    }

    fun requestPermissions(
        onPermissionsRequest: ((Map<RuntimePermission, RuntimePermissionState>) -> Unit)?,
    ) {
        this.onPermissionsRequest = onPermissionsRequest
        requestPermissionLauncher.launch(permissionsForRequest)
    }

    private fun isPermissionGranted(
        permission: String,
    ) = ActivityCompat.checkSelfPermission(
        activity,
        permission,
    ) == PackageManager.PERMISSION_GRANTED

    private fun Map.Entry<String, Boolean>.isPermissionDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        )

    private fun Map.Entry<String, Boolean>.isPermissionPermanentlyDenied() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && sharedPrefs.getBoolean(key, false)

    private fun Map.Entry<String, Boolean>.isPermissionIgnored() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && sharedPrefs.getBoolean(key, false).not()
}
