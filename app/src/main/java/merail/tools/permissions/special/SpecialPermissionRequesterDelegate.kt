package merail.tools.permissions.special

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import merail.tools.permissions.RequesterDelegate

fun ComponentActivity.specialPermissionRequester(
    requestedPermission: String,
) = SpecialPermissionRequesterDelegate(
    activity = this,
    requestedPermission = requestedPermission,
)

class SpecialPermissionRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedPermission: String,
) : RequesterDelegate<SpecialPermissionRequester>(
    activity = activity,
) {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            if (value == null) {
                value = SpecialPermissionRequester(
                    activity = activity,
                    requestedPermission = requestedPermission,
                )
            }
        }
    }
}