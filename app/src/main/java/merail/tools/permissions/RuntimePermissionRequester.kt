package merail.tools.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class RuntimePermissionRequester(private val activity: Activity) {

    companion object {
        private const val REQUEST_CODE_FOR_PERMISSIONS = 1
    }

    private var permissionsForRequest = mutableListOf<String>()

    fun checkSelfPermissions(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (activity.isPermissionGranted(permission).not()) {
                permissionsForRequest.add(permission)
            }
        }
        return permissionsForRequest.isEmpty()
    }

    fun setPermissionsForRequest(permissionsForRequest: MutableList<String>) {
        this.permissionsForRequest = permissionsForRequest
    }

    fun requestPermissions() = ActivityCompat.requestPermissions(
        activity,
        permissionsForRequest.toTypedArray(),
        REQUEST_CODE_FOR_PERMISSIONS,
    )

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): ArrayList<String> {
        val notGrantedPermissions = ArrayList<String>()
        if (requestCode == REQUEST_CODE_FOR_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        notGrantedPermissions.add(permissions[i])
                    }
                }
            }
        }
        return notGrantedPermissions
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
