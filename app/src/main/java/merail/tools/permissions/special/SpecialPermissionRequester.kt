package merail.tools.permissions.special

import android.Manifest
import android.util.Log
import androidx.activity.ComponentActivity
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.runtime.RuntimePermissionRequester

class SpecialPermissionRequester(
    activity: ComponentActivity,
    requestedPermission: String,
) {
    private val permissionsInformer = PermissionsInformer(activity)

    private val specialPermissionType = when (requestedPermission) {
        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> SpecialPermissionType.ManageExternalStorage(activity)
        Manifest.permission.MANAGE_MEDIA -> SpecialPermissionType.ManageMedia(activity)
        Manifest.permission.REQUEST_INSTALL_PACKAGES -> SpecialPermissionType.RequestInstallPackages(activity)
        Manifest.permission.SCHEDULE_EXACT_ALARM -> SpecialPermissionType.ScheduleExactAlarm(activity)
        Manifest.permission.SYSTEM_ALERT_WINDOW ->  SpecialPermissionType.SystemAlertWindow(activity)
        Manifest.permission.WRITE_SETTINGS -> SpecialPermissionType.WriteSetting(activity)
        else ->  {
            when {
                permissionsInformer.isUnknown(requestedPermission) -> Log.e(RuntimePermissionRequester.TAG, "Permission \"$requestedPermission\" is unknown. Can't handle it")
                permissionsInformer.isInstallTime(requestedPermission) -> Log.i(RuntimePermissionRequester.TAG, "Permission \"$requestedPermission\" is install-time and normal. Declaring this permission in the manifest is sufficient to obtain it")
                permissionsInformer.isRuntime(requestedPermission) -> Log.w(RuntimePermissionRequester.TAG, "Permission \"$requestedPermission\" is runtime. Try using RuntimePermissionRequester to get it")
                permissionsInformer.isSystem(requestedPermission) -> Log.w(RuntimePermissionRequester.TAG, "Permission \"$requestedPermission\" is system. This permission is only granted to system apps")
                else -> Log.w(RuntimePermissionRequester.TAG, "SpecialPermissionRequester currently doesn't have implementation for permission \"$requestedPermission\"")
            }
            SpecialPermissionType.Unknown
        }
    }

    fun isPermissionGranted() = specialPermissionType.isGranted()

    fun requestPermission() = specialPermissionType.requestPermission()
}
