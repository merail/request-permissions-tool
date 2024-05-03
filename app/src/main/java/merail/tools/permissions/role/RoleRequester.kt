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
import merail.tools.permissions.core.role.RoleResultObserver

@RequiresApi(Build.VERSION_CODES.Q)
class RoleRequester(
    activity: ComponentActivity,
    var requestedRole: String,
) {
    private val roleManager = activity.getSystemService(Context.ROLE_SERVICE) as RoleManager

    private var onRoleRequestResult: ((Pair<String, RoleState>) -> Unit)? = null

    private val requestRoleLauncher: ActivityResultLauncher<Intent>

    init {
        try {
            val roleResultObserver = RoleResultObserver()
            requestRoleLauncher = activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) {
                val roleRequestResult = roleResultObserver.invoke(
                    role = requestedRole,
                    isGranted = it.resultCode == Activity.RESULT_OK,
                )
                onRoleRequestResult?.invoke(roleRequestResult)
            }
        } catch (exception: Exception) {
            throw WrongTimeInitializationException()
        }
    }

    fun isRoleGranted() = roleManager.isRoleHeld(requestedRole)

    fun requestRole(
        onRoleRequestResult: ((Pair<String, RoleState>) -> Unit)? = null,
    ) {
        this.onRoleRequestResult = onRoleRequestResult
        val intent = roleManager.createRequestRoleIntent(requestedRole)
        requestRoleLauncher.launch(intent)
    }
}