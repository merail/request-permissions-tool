package merail.tools.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import merail.tools.permissions.core.common.WrongTimeInitializationException
import merail.tools.permissions.core.runtime.RuntimePermissionResultObserver

@Keep
class RuntimePermissionRequester {
    companion object {
        private const val PERMISSIONS_PREFERENCES = "PERMISSIONS_PREFERENCES"
    }

    private lateinit var activity: ComponentActivity

    lateinit var requestedPermissions: Array<String>

    var requestedPermission: String = ""
        set(value) {
            requestedPermissions = arrayOf(value)
        }

    private lateinit var runtimePermissionResultObserver: RuntimePermissionResultObserver

    private var onRuntimePermissionsRequestResult: ((Map<String, RuntimePermissionState>) -> Unit)? = null

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    constructor(
        activity: ComponentActivity,
        requestedPermissions: Array<String>,
    ) {
        init(
            activity = activity,
            requestedPermissions = requestedPermissions,
        )
    }

    constructor(
        activity: ComponentActivity,
        requestedPermission: String,
    ) {
        init(
            activity = activity,
            requestedPermissions = arrayOf(requestedPermission),
        )
    }

    private fun init(
        activity: ComponentActivity,
        requestedPermissions: Array<String>,
    ) {
        this.activity = activity
        this.requestedPermissions = requestedPermissions
        try {
            val preferences = activity.getSharedPreferences(PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE)
            requestPermissionLauncher = activity.registerForActivityResult(
                RequestMultiplePermissions(),
            ) { permissionsGrants ->
                val permissionsRequestResult = permissionsGrants.entries.associate { entry ->
                    runtimePermissionResultObserver.invoke(entry)
                }
                onRuntimePermissionsRequestResult?.invoke(permissionsRequestResult)
            }
            runtimePermissionResultObserver = RuntimePermissionResultObserver(
                activity = activity,
                preferences = preferences,
            )
        } catch (exception: Exception) {
            throw WrongTimeInitializationException()
        }
    }

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

}
