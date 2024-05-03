package merail.tools.permissions.role

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import merail.tools.permissions.WrongTimeInitializationException

@RequiresApi(Build.VERSION_CODES.Q)
class RolePermissionRequester(
    activity: ComponentActivity,
    var requestedRole: String,
) {

    private val roleManager = activity.getSystemService(Context.ROLE_SERVICE) as RoleManager

    private var onRolePermissionRequestResult: ((Boolean) -> Unit)? = null

    private val requestPermissionLauncher: ActivityResultLauncher<Intent>

    init {
        try {
            requestPermissionLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                onRolePermissionRequestResult?.invoke(it.resultCode == Activity.RESULT_OK)
            }
        } catch (exception: Exception) {
            throw WrongTimeInitializationException()
        }
    }

    fun isPermissionGranted() = roleManager.isRoleHeld(requestedRole)

    fun requestPermission(
        onRolePermissionRequestResult: ((Boolean) -> Unit)? = null,
    ) {
        this.onRolePermissionRequestResult = onRolePermissionRequestResult
        val intent = roleManager.createRequestRoleIntent(requestedRole)
        requestPermissionLauncher.launch(intent)
    }
}