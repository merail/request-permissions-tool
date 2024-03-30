package merail.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class RuntimePermissionRequester(
    private val activity: AppCompatActivity,
    private val permissionsForRequest: Array<String>,
) {
    private var onPermissionsRequest: ((List<String>) -> Unit)? = null

    private val requestPermissionLauncher = activity.registerForActivityResult(
        RequestMultiplePermissions(),
    ) { permissionsGrants ->
        val deniedPermissions = permissionsGrants
            .filter {
                it.value.not()
            }.keys.toList()
        onPermissionsRequest?.invoke(deniedPermissions)
    }

    fun areAllPermissionsGranted() = permissionsForRequest.none { permission ->
        activity.isPermissionGranted(permission).not()
    }

    fun requestPermissions(
        onPermissionsRequest: ((List<String>) -> Unit)?,
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
