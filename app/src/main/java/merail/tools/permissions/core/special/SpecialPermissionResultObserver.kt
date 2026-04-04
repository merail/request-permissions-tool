package merail.tools.permissions.core.special

import android.util.Log
import androidx.activity.ComponentActivity
import merail.tools.permissions.core.TAG
import merail.tools.permissions.core.isPermissionDeclaredInManifest
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.special.SpecialPermissionState

internal class SpecialPermissionResultObserver(
    private val activity: ComponentActivity,
) {
    private val permissionsInformer = PermissionsInformer(activity)

    fun invoke(
        type: SpecialPermissionType,
        isGranted: Boolean,
    ) = with(type) {
        permission to when {
            permissionsInformer.isUnknown(permission) -> {
                Log.e(TAG, "Permission \"$permission\" is unknown. Can't handle it")
                SpecialPermissionState.DENIED
            }
            activity.isPermissionDeclaredInManifest(permission).not() -> {
                Log.e(TAG, "Permission \"$permission\" isn't declared in Manifest!")
                SpecialPermissionState.DENIED
            }
            permissionsInformer.isInstallTime(permission) -> {
                Log.i(TAG, "Permission \"$permission\" is install-time and normal. Declaring this permission in the manifest is sufficient to obtain it")
                SpecialPermissionState.DENIED
            }
            permissionsInformer.isRuntime(permission) -> {
                Log.w(TAG, "Permission \"$permission\" is runtime. Try using RuntimePermissionRequester to get it")
                SpecialPermissionState.DENIED
            }
            permissionsInformer.isSystem(permission) -> {
                Log.w(TAG, "Permission \"$permission\" is system. This permission is only granted to system apps")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.InteractAcrossProfiles && type.canRequest().not() -> {
                Log.w(TAG, "Permission \"$permission\" requires a work profile to be set up on the device")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.InstantAppForegroundService && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to instant apps automatically")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.LoaderUsageStats && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to privileged system apps")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.ManageOngoingCalls && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted through CompanionDeviceManager association")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.MediaRoutingControl && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to system apps")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.SmsFinancialTransactions && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to system apps on API 29-30 and deprecated since API 31")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.TurnScreenOn && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to privileged system apps")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.UseIccAuthWithDeviceIdentifier && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to apps with carrier privileges")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.WriteSystemPreferences && isGranted.not() -> {
                Log.w(TAG, "Permission \"$permission\" is only granted to privileged system apps")
                SpecialPermissionState.DENIED
            }
            type is SpecialPermissionType.Unknown -> {
                Log.w(TAG, "SpecialPermissionRequester currently doesn't have implementation for permission \"$permission\"")
                SpecialPermissionState.DENIED
            }
            isGranted -> {
                Log.d(TAG, "Permission \"$permission\" is granted")
                SpecialPermissionState.GRANTED
            }
            else -> {
                Log.d(TAG, "Permission \"$permission\" is denied")
                SpecialPermissionState.DENIED
            }
        }
    }
}