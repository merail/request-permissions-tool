package merail.tools.permissions.special

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import merail.tools.permissions.PermissionRequester
import merail.tools.permissions.core.special.SpecialPermissionResultObserver
import merail.tools.permissions.core.special.SpecialPermissionType

public class SpecialPermissionRequester(
    private val activity: ComponentActivity,
    requestedPermission: String,
) : PermissionRequester(activity) {
    public var requestedPermission: String = requestedPermission
        set(value) {
            checkPermissionPreviously(value)
            field = value
        }

    private val specialPermissionResultObserver = SpecialPermissionResultObserver(
        activity = activity,
    )

    private var onSpecialPermissionRequestResult: ((Pair<String, SpecialPermissionState>) -> Unit)? = null

    private val specialPermissionType = when (requestedPermission) {
        Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE -> SpecialPermissionType.InstantAppForegroundService(activity)
        Manifest.permission.INTERACT_ACROSS_PROFILES -> SpecialPermissionType.InteractAcrossProfiles(activity)
        Manifest.permission.LOADER_USAGE_STATS -> SpecialPermissionType.LoaderUsageStats(activity)
        Manifest.permission.MANAGE_EXTERNAL_STORAGE -> SpecialPermissionType.ManageExternalStorage(activity)
        Manifest.permission.MANAGE_MEDIA -> SpecialPermissionType.ManageMedia(activity)
        Manifest.permission.MANAGE_ONGOING_CALLS -> SpecialPermissionType.ManageOngoingCalls(activity)
        Manifest.permission.MEDIA_ROUTING_CONTROL -> SpecialPermissionType.MediaRoutingControl(activity)
        Manifest.permission.PACKAGE_USAGE_STATS -> SpecialPermissionType.PackageUsageStats(activity)
        Manifest.permission.REQUEST_INSTALL_PACKAGES -> SpecialPermissionType.RequestInstallPackages(activity)
        Manifest.permission.SCHEDULE_EXACT_ALARM -> SpecialPermissionType.ScheduleExactAlarm(activity)
        Manifest.permission.SMS_FINANCIAL_TRANSACTIONS -> SpecialPermissionType.SmsFinancialTransactions(activity)
        Manifest.permission.SYSTEM_ALERT_WINDOW -> SpecialPermissionType.SystemAlertWindow(activity)
        Manifest.permission.TURN_SCREEN_ON -> SpecialPermissionType.TurnScreenOn(activity)
        Manifest.permission.USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER -> SpecialPermissionType.UseIccAuthWithDeviceIdentifier(activity)
        Manifest.permission.WRITE_SETTINGS -> SpecialPermissionType.WriteSettings(activity)
        Manifest.permission.WRITE_SYSTEM_PREFERENCES -> SpecialPermissionType.WriteSystemPreferences(activity)
        else -> SpecialPermissionType.Unknown(requestedPermission)
    }

    private var isFirstOnStartCallback = true

    private val activityLifecycleObserver: LifecycleEventObserver by lazy {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                isFirstOnStartCallback = if (isFirstOnStartCallback) {
                    false
                } else {
                    activity.lifecycle.removeObserver(activityLifecycleObserver)
                    val permissionRequestResult = specialPermissionResultObserver.invoke(
                        type = specialPermissionType,
                        isGranted = isPermissionGranted(),
                    )
                    onSpecialPermissionRequestResult?.invoke(permissionRequestResult)
                    true
                }
            }
        }
    }

    init {
        checkPermissionPreviously(requestedPermission)
    }

    public fun isPermissionGranted(): Boolean = specialPermissionType.isGranted()

    public fun requestPermission(
        onSpecialPermissionRequestResult: ((Pair<String, SpecialPermissionState>) -> Unit)? = null,
    ) {
        this.onSpecialPermissionRequestResult = onSpecialPermissionRequestResult
        try {
            specialPermissionType.requestPermission()
            activity.lifecycle.addObserver(activityLifecycleObserver)
        } catch (exception: Exception) {
            specialPermissionResultObserver.invoke(
                type = specialPermissionType,
                isGranted = false,
            )
        }
    }
}
