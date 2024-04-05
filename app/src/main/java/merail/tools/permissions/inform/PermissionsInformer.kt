package merail.tools.permissions.inform

import android.content.pm.PermissionInfo
import android.os.Build
import androidx.activity.ComponentActivity
import merail.tools.permissions.R

class PermissionsInformer(
    activity: ComponentActivity,
) {

    private val packageManager = activity.packageManager

    val permissions: Array<String> = activity.resources.getStringArray(R.array.permissions)
    val installTimePermissions: Array<String> = activity.resources.getStringArray(R.array.install_time_permissions)
    val runtimePermissions: Array<String> = activity.resources.getStringArray(R.array.runtime_permissions)
    val specialPermissions: Array<String> = activity.resources.getStringArray(R.array.special_permissions)
    val systemPermissions: Array<String> = activity.resources.getStringArray(R.array.system_permissions)

    private val packageManagersPermissions by lazy {
        getPackageManagersPermissionsPermissions()
    }

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

    fun isInstallTimePM(
        permission: String,
    ) = packageManagersPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionLevel == PermissionProtectionLevel.NORMAL
    } ?: false

    fun isRuntimePM(
        permission: String,
    ) = packageManagersPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionLevel == PermissionProtectionLevel.DANGEROUS
    } ?: false

    fun isSpecialPM(
        permission: String,
    ) = packageManagersPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionFlags and PermissionInfo.PROTECTION_FLAG_APPOP == PermissionInfo.PROTECTION_FLAG_APPOP &&
                isInstallTime(permission).not()
    } ?: false

    fun isSystemPM(
        permission: String,
    ) = packageManagersPermissions.find {
        it.name == permission
    }?.run {
        (permissionProtectionLevel == PermissionProtectionLevel.SIGNATURE ||
                permissionProtectionLevel == PermissionProtectionLevel.SIGNATURE_OR_SYSTEM ||
                permissionProtectionLevel == PermissionProtectionLevel.INTERNAL) &&
                isSpecial(permission).not()
    } ?: false

    private fun getPackageManagersPermissionsPermissions(): List<PermissionInfo> = packageManager
        .getAllPermissionGroups(0)
        .toMutableList()
        .apply {
            add(null)
        }.flatMap { permissionGroupInfo ->
            packageManager.queryPermissionsByGroup(permissionGroupInfo?.name, 0)
        }

    val PermissionInfo.permissionProtectionLevel: PermissionProtectionLevel
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PermissionProtectionLevel.fromCode(protection)
        } else {
            PermissionProtectionLevel.fromCode(protectionLevel)
        }

    val PermissionInfo.permissionProtectionFlags: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            protectionFlags
        } else {
            PermissionInfo.PROTECTION_MASK_FLAGS
        }
}