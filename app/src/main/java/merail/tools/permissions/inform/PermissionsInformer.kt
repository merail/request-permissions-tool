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

    fun isUnknown(
        permission: String,
    ) = permission !in permissions

    fun isInstallTime(
        permission: String,
    ) = permission in installTimePermissions

    fun isRuntime(
        permission: String,
    ) = permission in runtimePermissions

    fun isSpecial(
        permission: String,
    ) = permission in specialPermissions

    fun isSystem(
        permission: String,
    ) = permission in systemPermissions
}