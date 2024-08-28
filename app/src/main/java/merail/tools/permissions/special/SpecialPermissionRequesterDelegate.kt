package merail.tools.permissions.special

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun ComponentActivity.specialPermissionRequester(
    requestedPermission: String,
) = SpecialPermissionRequesterDelegate(
    activity = this,
    requestedPermission = requestedPermission,
)

class SpecialPermissionRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedPermission: String,
) : ReadOnlyProperty<LifecycleOwner, SpecialPermissionRequester>, LifecycleEventObserver {

    private var value: SpecialPermissionRequester? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun getValue(
        thisRef: LifecycleOwner,
        property: KProperty<*>,
    ): SpecialPermissionRequester {
        thisRef.lifecycle.addObserver(this)
        return value ?: throw IllegalStateException("The field was not initialized yet, try to access it after ON_CREATE")
    }

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