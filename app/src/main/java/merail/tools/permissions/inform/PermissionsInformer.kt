package merail.tools.permissions.inform

import androidx.activity.ComponentActivity
import merail.tools.permissions.R

class PermissionsInformer(
    activity: ComponentActivity,
) {

    val permissions: Array<String> = activity.resources.getStringArray(R.array.permissions)
    val installTimePermissions: Array<String> = activity.resources.getStringArray(R.array.install_time_permissions)
    val runtimePermissions: Array<String> = activity.resources.getStringArray(R.array.runtime_permissions)
    val specialPermissions: Array<String> = activity.resources.getStringArray(R.array.special_permissions)
    val systemPermissions: Array<String> = activity.resources.getStringArray(R.array.system_permissions)

    fun isInstallTime(
        permission: String,
    ) = installTimePermissions.contains(permission)

    fun isRuntime(
        permission: String,
    ) = runtimePermissions.contains(permission)

    fun isSpecial(
        permission: String,
    ) = specialPermissions.contains(permission)

    fun isSystem(
        permission: String,
    ) = systemPermissions.contains(permission)
}