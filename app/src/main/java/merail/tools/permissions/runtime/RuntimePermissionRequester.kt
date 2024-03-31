package merail.tools.permissions.runtime

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class RuntimePermissionRequester(
    private val activity: AppCompatActivity,
    private val permissionsForRequest: Array<String>,
) {
    companion object {

        const val TAG = "MERAIL_TOOLS"

        private const val PERMISSIONS_PREFERENCES = "PERMISSIONS_PREFERENCES"
    }

    private val preferences = activity.getSharedPreferences(PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE)

    private var onPermissionsRequest: ((Map<String, RuntimePermissionState>) -> Unit)? = null

    private val requestPermissionLauncher = activity.registerForActivityResult(
        RequestMultiplePermissions(),
    ) { permissionsGrants ->
        val permissionsRequestResult = permissionsGrants.entries.associate { entry ->
            entry.key to when {
                entry.isPermissionDenied() -> {
                    Log.d(TAG, "Permission ${entry.key} is denied")
                    preferences.edit().putBoolean(entry.key, true).apply()
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
        onPermissionsRequest: ((Map<String, RuntimePermissionState>) -> Unit)?,
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
        ).not() && value.not() && preferences.getBoolean(key, false)

    private fun Map.Entry<String, Boolean>.isPermissionIgnored() = ActivityCompat
        .shouldShowRequestPermissionRationale(
            activity,
            key,
        ).not() && value.not() && preferences.getBoolean(key, false).not()
}
