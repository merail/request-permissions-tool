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
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    entry.key,
                ) -> {
                    Log.d(TAG, "Permission ${entry.key} is denied")
                    sharedPrefs.edit().putBoolean(entry.key, true).apply()
                    RuntimePermissionState.DENIED
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    entry.key,
                ).not() && entry.value.not() -> if (sharedPrefs.getBoolean(entry.key, false)) {
                    Log.d(TAG, "Permission ${entry.key} is permanently denied")
                    RuntimePermissionState.PERMANENT_DENIED
                } else {
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
        activity.isPermissionGranted(permission).not()
    }

    fun requestPermissions(
        onPermissionsRequest: ((Map<RuntimePermission, RuntimePermissionState>) -> Unit)?,
    ) {
        this.onPermissionsRequest = onPermissionsRequest
        requestPermissionLauncher.launch(permissionsForRequest)
    }

    private fun Activity.isPermissionGranted(
        permission: String,
    ) = ActivityCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED
}
