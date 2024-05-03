package merail.tools.permissions.core.role

import android.util.Log
import merail.tools.permissions.core.common.TAG
import merail.tools.permissions.role.RoleState

internal class RoleResultObserver {
    fun invoke(
        role: String,
        isGranted: Boolean,
    ) = role to when {
        isGranted -> {
            Log.d(TAG, "Role \"$role\" is granted")
            RoleState.GRANTED
        }

        else -> {
            Log.d(TAG, "Role \"$role\" is denied")
            RoleState.DENIED
        }
    }
}