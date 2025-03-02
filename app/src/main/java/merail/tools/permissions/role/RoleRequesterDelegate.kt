package merail.tools.permissions.role

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import merail.tools.permissions.RequesterDelegate

public fun ComponentActivity.roleRequester(
    requestedRole: String,
): RoleRequesterDelegate = RoleRequesterDelegate(
    activity = this,
    requestedRole = requestedRole,
)

public class RoleRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedRole: String,
) : RequesterDelegate<RoleRequester>(
    activity = activity,
) {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (event == Lifecycle.Event.ON_CREATE) {
                if (value == null) {
                    value = RoleRequester(
                        activity = activity,
                        requestedRole = requestedRole,
                    )
                }
            }
        }
    }
}