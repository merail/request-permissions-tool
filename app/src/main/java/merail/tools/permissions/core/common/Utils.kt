package merail.tools.permissions.core.common

import android.app.Activity
import android.content.pm.PackageManager

internal const val TAG = "MERAIL_TOOLS"

internal fun Activity.isPermissionDeclaredInManifest(
    permission: String,
) = packageManager.getPackageInfo(
    packageName,
    PackageManager.GET_PERMISSIONS,
).requestedPermissions
    ?.any {
        it == permission
    } ?: false