package merail.tools.permissions.runtime

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import merail.tools.permissions.RequesterDelegate

public fun ComponentActivity.runtimePermissionRequester(
    requestedPermissions: Array<String>,
): RuntimePermissionRequesterDelegate = RuntimePermissionRequesterDelegate(
    activity = this,
    requestedPermissions = requestedPermissions,
)

public fun ComponentActivity.runtimePermissionRequester(
    requestedPermission: String,
): RuntimePermissionRequesterDelegate = RuntimePermissionRequesterDelegate(
    activity = this,
    requestedPermissions = arrayOf(requestedPermission),
)

public class RuntimePermissionRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedPermissions: Array<String>,
) : RequesterDelegate<RuntimePermissionRequester>(
    activity = activity,
) {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            if (value == null) {
                value = RuntimePermissionRequester(
                    activity = activity,
                    requestedPermissions = requestedPermissions,
                )
            }
        }
    }
}