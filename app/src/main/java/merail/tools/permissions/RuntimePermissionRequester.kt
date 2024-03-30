package merail.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class RuntimePermissionRequester(
    private val activity: AppCompatActivity,
    private val permissionsForRequest: Array<String>,
) {

    private lateinit var onPermissionsRequest: (List<String>) -> Unit

    private val requestPermissionLauncher = activity.registerForActivityResult(
        RequestMultiplePermissions(),
    ) { permissionsGrants ->
        val notGrantedPermissions = permissionsGrants
            .filter {
                it.value
            }.keys.toList()
        Log.d("BBBBBBBB", notGrantedPermissions.toString())
        onPermissionsRequest(notGrantedPermissions)
    }

    fun areAllPermissionsGranted() = permissionsForRequest.none { permission ->
        activity.isPermissionGranted(permission).not()
    }

    fun requestPermissions(
        onPermissionsRequest: (List<String>) -> Unit,
    ) {
        this.onPermissionsRequest = onPermissionsRequest
        requestPermissionLauncher.launch(permissionsForRequest)
    }

    private fun shouldShowRequestPermissionRationale(
        permission: String,
    ) = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        permission,
    )

    fun getPermissionsForRationale(notGrantedPermissions: ArrayList<String>): ArrayList<String> {
        val permissionsForRationale = ArrayList<String>()
        for (notGrantedPermission in notGrantedPermissions) {
            if (shouldShowRequestPermissionRationale(notGrantedPermission)) {
                permissionsForRationale.add(notGrantedPermission)
            }
        }
        return permissionsForRationale
    }

    fun getDeniedPermissions(notGrantedPermissions: ArrayList<String>): ArrayList<String> {
        val deniedPermissions = ArrayList<String>()
        for (notGrantedPermission in notGrantedPermissions) {
            if (shouldShowRequestPermissionRationale(notGrantedPermission).not()) {
                deniedPermissions.add(notGrantedPermission)
            }
        }
        return deniedPermissions
    }

    private fun Activity.isPermissionGranted(
        permission: String,
    ) = ActivityCompat.checkSelfPermission(
        this,
        permission,
    ) == PackageManager.PERMISSION_GRANTED
}
