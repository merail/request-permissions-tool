package merail.tools.permissions.role

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun ComponentActivity.roleRequester(
    requestedRole: String,
) = RoleRequesterDelegate(
    activity = this,
    requestedRole = requestedRole,
)

class RoleRequesterDelegate(
    private val activity: ComponentActivity,
    private val requestedRole: String,
) : ReadOnlyProperty<LifecycleOwner, RoleRequester>, LifecycleEventObserver {

    private var value: RoleRequester? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun getValue(
        thisRef: LifecycleOwner,
        property: KProperty<*>,
    ): RoleRequester {
        thisRef.lifecycle.addObserver(this)
        return value ?: throw IllegalStateException("The field was not initialized yet, try to access it after ON_CREATE")
    }

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