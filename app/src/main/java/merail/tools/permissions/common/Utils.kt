package merail.tools.permissions.common

import android.content.pm.PermissionInfo
import android.os.Build
import merail.tools.permissions.inform.PermissionProtectionLevel

internal object Utils {

    internal const val TAG = "MERAIL_TOOLS"

    internal val PermissionInfo.permissionProtectionLevel: PermissionProtectionLevel
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PermissionProtectionLevel.fromCode(protection)
        } else {
            PermissionProtectionLevel.fromCode(
                code = protectionLevel and PermissionInfo.PROTECTION_MASK_BASE,
            )
        }

    internal val PermissionInfo.permissionProtectionFlags: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            protectionFlags
        } else {
            protectionLevel and PermissionInfo.PROTECTION_MASK_BASE.inv()
        }
}