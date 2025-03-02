package merail.tools.permissions

import android.app.Activity
import android.os.Build
import android.util.Log
import merail.tools.permissions.core.TAG

public abstract class PermissionRequester(
    activity: Activity,
) {
    private val permissions: Array<String> = activity.resources.getStringArray(R.array.permissions)

    private val minSdks: Array<String> = activity.resources.getStringArray(R.array.min_sdks)

    private val deprecatedPermissions: Array<String> = activity.resources.getStringArray(R.array.deprecated_permissions)

    public fun checkPermissionPreviously(
        permission: String,
    ) {
        val index = permissions.indexOf(permission)
        when {
            index == -1 -> Log.e(TAG, "Permission \"$permission\" is unknown. Can't handle it")
            minSdks[index].toInt() > Build.VERSION.SDK_INT -> Log.e(TAG, "Permission \"$permission\" requires API level ${minSdks[index].toInt()}+. You current SDK is ${Build.VERSION.SDK_INT}")
            permission in deprecatedPermissions -> Log.w(TAG, "Permission \"$permission\" is deprecated")
        }
    }
}