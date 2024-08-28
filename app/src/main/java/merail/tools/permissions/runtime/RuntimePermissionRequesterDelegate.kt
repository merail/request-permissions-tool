package merail.tools.permissions.runtime

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun ComponentActivity.runtimePermissionRequester(
    requestedPermissions: Array<String>,
) = RuntimePermissionRequesterDelegate(
    activity = this,
    requestedPermissions = requestedPermissions,
)

fun ComponentActivity.runtimePermissionRequester(
    requestedPermission: String,
) = RuntimePermissionRequesterDelegate(
    activity = this,
    requestedPermissions = arrayOf(requestedPermission),
)

class RuntimePermissionRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedPermissions: Array<String>,
) : ReadOnlyProperty<LifecycleOwner, RuntimePermissionRequester>, LifecycleEventObserver {

    private var value: RuntimePermissionRequester? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun getValue(
        thisRef: LifecycleOwner,
        property: KProperty<*>,
    ): RuntimePermissionRequester {
        thisRef.lifecycle.addObserver(this)
        return value ?: throw IllegalStateException("The field was not initialized yet, try to access it after ON_CREATE")
    }

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