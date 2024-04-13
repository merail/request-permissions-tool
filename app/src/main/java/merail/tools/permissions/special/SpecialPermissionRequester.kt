package merail.tools.permissions.special

import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import merail.tools.permissions.PermissionRequester
import merail.tools.permissions.core.common.TAG
import merail.tools.permissions.core.special.SpecialPermissionType
import merail.tools.permissions.inform.PermissionsInformer

class SpecialPermissionRequester(
    private val activity: ComponentActivity,
    var requestedPermission: String,
) : PermissionRequester(activity) {
    private val permissionsInformer = PermissionsInformer(activity)

    private var onSpecialPermissionRequestResult: ((Pair<String, Boolean>) -> Unit)? = null

    private val specialPermissionType = when (requestedPermission) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> SpecialPermissionType.ManageExternalStorage(activity)
        Manifest.permission.MANAGE_MEDIA -> SpecialPermissionType.ManageMedia(activity)
        Manifest.permission.REQUEST_INSTALL_PACKAGES -> SpecialPermissionType.RequestInstallPackages(activity)
        Manifest.permission.SCHEDULE_EXACT_ALARM -> SpecialPermissionType.ScheduleExactAlarm(activity)
        Manifest.permission.SYSTEM_ALERT_WINDOW -> SpecialPermissionType.SystemAlertWindow(activity)
        Manifest.permission.WRITE_SETTINGS -> SpecialPermissionType.WriteSettings(activity)
        else ->  {
            when {
                permissionsInformer.isUnknown(requestedPermission) -> Log.e(TAG, "Permission \"$requestedPermission\" is unknown. Can't handle it")
                permissionsInformer.isInstallTime(requestedPermission) -> Log.i(TAG, "Permission \"$requestedPermission\" is install-time and normal. Declaring this permission in the manifest is sufficient to obtain it")
                permissionsInformer.isRuntime(requestedPermission) -> Log.w(TAG, "Permission \"$requestedPermission\" is runtime. Try using RuntimePermissionRequester to get it")
                permissionsInformer.isSystem(requestedPermission) -> Log.w(TAG, "Permission \"$requestedPermission\" is system. This permission is only granted to system apps")
                else -> Log.w(TAG, "SpecialPermissionRequester currently doesn't have implementation for permission \"$requestedPermission\"")
            }
            SpecialPermissionType.Unknown
        }
    }

    private var isFirstOnStartCallback = true

    private val activityLifecycleObserver: LifecycleEventObserver by lazy {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                isFirstOnStartCallback = if (isFirstOnStartCallback) {
                    false
                } else {
                    activity.lifecycle.removeObserver(activityLifecycleObserver)
                    val isPermissionGranted = isPermissionGranted()
                    if (isPermissionGranted) {
                        Log.d(TAG, "Permission \"$requestedPermission\" is granted")
                    } else {
                        Log.d(TAG, "Permission \"$requestedPermission\" is denied")
                    }
                    onSpecialPermissionRequestResult?.invoke(
                        Pair(
                            first = requestedPermission,
                            second = isPermissionGranted,
                        )
                    )
                    true
                }
            }
        }
    }

    init {
        checkPermissionPreviously(requestedPermission)
    }

    fun isPermissionGranted() = specialPermissionType.isGranted()

    fun requestPermission(
        onSpecialPermissionRequestResult: ((Pair<String, Boolean>) -> Unit)? = null,
    ) {
        this.onSpecialPermissionRequestResult = onSpecialPermissionRequestResult
        specialPermissionType.requestPermission()
        if (specialPermissionType !is SpecialPermissionType.Unknown) {
            activity.lifecycle.addObserver(activityLifecycleObserver)
        }
    }
}
