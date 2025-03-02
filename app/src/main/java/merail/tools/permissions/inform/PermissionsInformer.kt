package merail.tools.permissions.inform

import androidx.activity.ComponentActivity
import merail.tools.permissions.R

public class PermissionsInformer(
    activity: ComponentActivity,
) {
    public val permissions: Array<String> = activity.resources.getStringArray(R.array.permissions)
    public val installTimePermissions: Array<String> = activity.resources.getStringArray(R.array.install_time_permissions)
    public val runtimePermissions: Array<String> = activity.resources.getStringArray(R.array.runtime_permissions)
    public val specialPermissions: Array<String> = activity.resources.getStringArray(R.array.special_permissions)
    public val systemPermissions: Array<String> = activity.resources.getStringArray(R.array.system_permissions)

    public fun isUnknown(
        permission: String,
    ): Boolean = permission !in permissions

    public fun isInstallTime(
        permission: String,
    ): Boolean = permission in installTimePermissions

    public fun isRuntime(
        permission: String,
    ): Boolean = permission in runtimePermissions

    public fun isSpecial(
        permission: String,
    ): Boolean = permission in specialPermissions

    public fun isSystem(
        permission: String,
    ): Boolean = permission in systemPermissions
}