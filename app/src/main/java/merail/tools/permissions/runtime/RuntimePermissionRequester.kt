package merail.tools.permissions.runtime

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.app.ActivityCompat
import merail.tools.permissions.PermissionRequester
import merail.tools.permissions.core.runtime.RuntimePermissionResultObserver
import merail.tools.permissions.exceptions.WrongTimeInitializationException

public class RuntimePermissionRequester(
    private val activity: ComponentActivity,
    requestedPermissions: Array<String>,
) : PermissionRequester(activity) {

    public companion object {
        private const val PERMISSIONS_PREFERENCES = "PERMISSIONS_PREFERENCES"
    }

    public var requestedPermissions: Array<String> = requestedPermissions
        set(value) {
            value.forEach {
                checkPermissionPreviously(it)
            }
            field = value
        }

    public var requestedPermission: String = ""
        set(value) {
            requestedPermissions = arrayOf(value)
        }

    private var onRuntimePermissionsRequestResult: ((Map<String, RuntimePermissionState>) -> Unit)? = null

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    init {
        try {
            requestedPermissions.forEach {
                checkPermissionPreviously(it)
            }
            val preferences = activity.getSharedPreferences(PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE)
            val runtimePermissionResultObserver = RuntimePermissionResultObserver(
                activity = activity,
                preferences = preferences,
            )
            requestPermissionLauncher = activity.registerForActivityResult(
                RequestMultiplePermissions(),
            ) { permissionsGrants ->
                val permissionsRequestResult = permissionsGrants.entries.associate { entry ->
                    runtimePermissionResultObserver.invoke(entry)
                }
                onRuntimePermissionsRequestResult?.invoke(permissionsRequestResult)
            }
        } catch (exception: Exception) {
            throw WrongTimeInitializationException()
        }
    }

    public constructor(
        activity: ComponentActivity,
        requestedPermission: String,
    ) : this(activity, arrayOf(requestedPermission))

    public fun isPermissionGranted(
        permission: String,
    ): Boolean = ActivityCompat.checkSelfPermission(
        activity,
        permission,
    ) == PackageManager.PERMISSION_GRANTED

    public fun areAllPermissionsGranted(): Boolean = requestedPermissions.none { permission ->
        isPermissionGranted(permission).not()
    }

    public fun requestPermissions(
        onRuntimePermissionsRequestResult: ((Map<String, RuntimePermissionState>) -> Unit)? = null,
    ) {
        this.onRuntimePermissionsRequestResult = onRuntimePermissionsRequestResult
        requestPermissionLauncher.launch(requestedPermissions)
    }
}
