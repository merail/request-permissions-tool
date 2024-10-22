package merail.tools.permissions

import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import merail.tools.permissions.exceptions.WrongTimeAccessException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class RequesterDelegate<T>(
    activity: ComponentActivity,
) : ReadOnlyProperty<LifecycleOwner, T>, LifecycleEventObserver {

    var value: T? = null

    init {
        @Suppress("LeakingThis")
        activity.lifecycle.addObserver(this)
    }

    override fun getValue(
        thisRef: LifecycleOwner,
        property: KProperty<*>,
    ): T {
        thisRef.lifecycle.addObserver(this)
        return value ?: throw WrongTimeAccessException()
    }
}