package me.rail.dev

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rail.dev.ui.theme.RequestPermissionsToolTheme
import me.rail.dev.ui.theme.Typography
import merail.tools.debug.InternalPermissionsInformer
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.role.roleRequester
import merail.tools.permissions.runtime.RuntimePermissionState
import merail.tools.permissions.runtime.runtimePermissionRequester
import merail.tools.permissions.special.specialPermissionRequester

class DevActivity : ComponentActivity() {

    companion object {
        private const val TAG = "DevActivity"
    }

    private val runtimePermissionRequester by runtimePermissionRequester(
        requestedPermissions = arrayOf(
            Manifest.permission.ACCEPT_HANDOVER,
        ),
    )

    private val specialPermissionRequester by specialPermissionRequester(
        requestedPermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    )

    @delegate:RequiresApi(Build.VERSION_CODES.Q)
    private val roleRequester by roleRequester(
        requestedRole = RoleManager.ROLE_CALL_SCREENING,
    )

    private lateinit var permissionsInformer: PermissionsInformer

    private lateinit var internalPermissionsInformer: InternalPermissionsInformer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RequestPermissionsToolTheme {
                Content()
            }
        }

        permissionsInformer = PermissionsInformer(this@DevActivity)

        internalPermissionsInformer = InternalPermissionsInformer(
            activity = this@DevActivity,
            permissions = permissionsInformer.permissions,
        )
    }

    @Preview(
        showBackground = true,
    )
    @Composable
    private fun Content() {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Button(
                onClick = {
                    getMinSdks()
                },
                text = "Get min sdks",
            )

            Button(
                onClick = {
                    getInstallTimePermissions()
                },
                text = "Get install-time permissions",
            )

            Button(
                onClick = {
                    getRuntimePermissions()
                },
                text = "Get runtime permissions",
            )

            Button(
                onClick = {
                    getSpecialPermissions()
                },
                text = "Get special permissions",
            )

            Button(
                onClick = {
                    getSystemPermissions()
                },
                text = "Get system permissions",
            )

            Button(
                onClick = {
                    checkPermissionsIntFlags()
                },
                text = "Check permissions int flags",
            )

            Button(
                onClick = {
                    checkPermissionsStringFlags()
                },
                text = "Check permissions string flags",
            )

            Button(
                onClick = {
                    requestRuntimePermissions()
                },
                text = "Request runtime permissions",
            )

            Button(
                onClick = {
                    requestSpecialPermissions()
                },
                text = "Request special permissions",
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Button(
                    onClick = {
                        requestRoles()
                    },
                    text = "Request roles",
                )
            }
        }
    }

    @Composable
    private fun Button(
        onClick: () -> Unit,
        text: String,
    ) {
        Button(
            onClick = {
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(
                    minWidth = 72.dp,
                )
                .padding(12.dp),
            contentPadding = PaddingValues(
                vertical = 20.dp,
            ),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
            ),
        ) {
            Text(
                text = text,
                style = Typography.titleLarge,
            )
        }
    }

    private fun getMinSdks() {
        permissionsInformer.permissions.forEach { permission ->
            Log.d(TAG, "${requiredApiPermissions.find { it.second == permission }?.first ?: -1}")
        }
    }

    private fun getInstallTimePermissions() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (isInstallTime(it.name)) {
                    Log.d(TAG, it.name)
                }
            }
        }
    }

    private fun getRuntimePermissions() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (isRuntime(it.name)) {
                    Log.d(TAG, it.name)
                }
            }
        }
    }

    private fun getSpecialPermissions() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (isSpecial(it.name)) {
                    Log.d(TAG, it.name)
                }
            }
        }
    }

    private fun getSystemPermissions() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (isSystem(it.name)) {
                    Log.d(TAG, it.name)
                }
            }
        }
    }

    private fun checkPermissionsIntFlags() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(TAG, "${it.name} ${it.protection or it.protectionFlags} ${it.protectionFlags} ${it.protection}")
                } else {
                    Log.d(TAG, "${it.name} ${it.protectionLevel} ${it.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE.inv()} ${it.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE}")
                }
            }
        }
    }

    private fun checkPermissionsStringFlags() {
        internalPermissionsInformer.apply {
            packageManagerPermissions.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.d(TAG, "${it.name} ${protectionToString(it.protection, it.protectionFlags)}")
                } else {
                    Log.d(TAG, "${it.name} ${protectionToString(it.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE, it.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE.inv())}")
                }
            }
        }
    }
    
    private fun requestRuntimePermissions() {
        runtimePermissionRequester.requestPermissions {
            it.entries.forEach { entry ->
                if (entry.value == RuntimePermissionState.GRANTED) {
                    Log.d(TAG, "${entry.key} ${entry.value}")
                } else {
                    when {
                        permissionsInformer.isInstallTime(entry.key) -> {
                            Log.d(TAG, "${entry.key} is INSTALL-TIME")
                        }
                        permissionsInformer.isRuntime(entry.key) -> {
                            Log.d(TAG, "${entry.key} is RUNTIME")
                        }
                        permissionsInformer.isSpecial(entry.key) -> {
                            Log.d(TAG, "${entry.key} is SPECIAL")
                        }
                        permissionsInformer.isSystem(entry.key) -> {
                            Log.d(TAG, "${entry.key} is SYSTEM")
                        }
                        else -> {
                            Log.d(TAG, "${entry.key} is UNDEFINED")
                        }
                    }
                }
            }
        }
    }

    private fun requestSpecialPermissions() {
        if (specialPermissionRequester.isPermissionGranted().not()) {
            specialPermissionRequester.requestPermission {
                Log.d(TAG, it.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestRoles() {
        if (roleRequester.isRoleGranted().not()) {
            roleRequester.requestRole {
                Log.d(TAG, it.toString())
            }
        }
    }
}

val deprecatedPermissions = listOf(
    Manifest.permission.BIND_CARRIER_MESSAGING_SERVICE,
    Manifest.permission.BIND_CHOOSER_TARGET_SERVICE,
    Manifest.permission.GET_TASKS,
    Manifest.permission.PERSISTENT_ACTIVITY,
    Manifest.permission.PROCESS_OUTGOING_CALLS,
    Manifest.permission.READ_INPUT_STATE,
    Manifest.permission.RESTART_PACKAGES,
    Manifest.permission.SET_PREFERRED_APPLICATIONS,
    Manifest.permission.SMS_FINANCIAL_TRANSACTIONS,
    Manifest.permission.USE_FINGERPRINT,
)

val requiredApiPermissions = listOf(
    Pair(28,Manifest.permission.ACCEPT_HANDOVER),
    Pair(29,Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    Pair(31,Manifest.permission.ACCESS_BLOBS_ACROSS_USERS),
    Pair(29,Manifest.permission.ACCESS_MEDIA_LOCATION),
    Pair(23,Manifest.permission.ACCESS_NOTIFICATION_POLICY),
    Pair(29,Manifest.permission.ACTIVITY_RECOGNITION),
    Pair(26,Manifest.permission.ANSWER_PHONE_CALLS),
    Pair(26,Manifest.permission.BIND_AUTOFILL_SERVICE),
    Pair(29,Manifest.permission.BIND_CALL_REDIRECTION_SERVICE),
    Pair(29,Manifest.permission.BIND_CARRIER_MESSAGING_CLIENT_SERVICE),
    Pair(22,Manifest.permission.BIND_CARRIER_MESSAGING_SERVICE),
    Pair(23,Manifest.permission.BIND_CARRIER_SERVICES),
    Pair(23,Manifest.permission.BIND_CHOOSER_TARGET_SERVICE),
    Pair(31,Manifest.permission.BIND_COMPANION_DEVICE_SERVICE),
    Pair(24,Manifest.permission.BIND_CONDITION_PROVIDER_SERVICE),
    Pair(30,Manifest.permission.BIND_CONTROLS),
    Pair(34,Manifest.permission.BIND_CREDENTIAL_PROVIDER_SERVICE),
    Pair(23,Manifest.permission.BIND_INCALL_SERVICE),
    Pair(23,Manifest.permission.BIND_MIDI_DEVICE_SERVICE),
    Pair(30,Manifest.permission.BIND_QUICK_ACCESS_WALLET_SERVICE),
    Pair(24,Manifest.permission.BIND_QUICK_SETTINGS_TILE),
    Pair(24,Manifest.permission.BIND_SCREENING_SERVICE),
    Pair(23,Manifest.permission.BIND_TELECOM_CONNECTION_SERVICE),
    Pair(33,Manifest.permission.BIND_TV_INTERACTIVE_APP),
    Pair(26,Manifest.permission.BIND_VISUAL_VOICEMAIL_SERVICE),
    Pair(24,Manifest.permission.BIND_VR_LISTENER_SERVICE),
    Pair(31,Manifest.permission.BLUETOOTH_ADVERTISE),
    Pair(31,Manifest.permission.BLUETOOTH_CONNECT),
    Pair(31,Manifest.permission.BLUETOOTH_SCAN),
    Pair(33,Manifest.permission.BODY_SENSORS_BACKGROUND),
    Pair(29,Manifest.permission.CALL_COMPANION_APP),
    Pair(34,Manifest.permission.CONFIGURE_WIFI_DISPLAY),
    Pair(34,Manifest.permission.CREDENTIAL_MANAGER_QUERY_CANDIDATE_CREDENTIALS),
    Pair(34,Manifest.permission.CREDENTIAL_MANAGER_SET_ALLOWED_PROVIDERS),
    Pair(34,Manifest.permission.CREDENTIAL_MANAGER_SET_ORIGIN),
    Pair(33,Manifest.permission.DELIVER_COMPANION_MESSAGES),
    Pair(34,Manifest.permission.DETECT_SCREEN_CAPTURE),
    Pair(34,Manifest.permission.ENFORCE_UPDATE_OWNERSHIP),
    Pair(34,Manifest.permission.EXECUTE_APP_ACTION),
    Pair(28,Manifest.permission.FOREGROUND_SERVICE),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_CAMERA),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_HEALTH),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_LOCATION),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_MICROPHONE),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE),
    Pair(34,Manifest.permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED),
    Pair(23,Manifest.permission.GET_ACCOUNTS_PRIVILEGED),
    Pair(31,Manifest.permission.HIDE_OVERLAY_WINDOWS),
    Pair(31,Manifest.permission.HIGH_SAMPLING_RATE_SENSORS),
    Pair(26,Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE),
    Pair(30,Manifest.permission.INTERACT_ACROSS_PROFILES),
    Pair(34,Manifest.permission.LAUNCH_CAPTURE_CONTENT_ACTIVITY_FOR_NOTE),
    Pair(32,Manifest.permission.LAUNCH_MULTI_PANE_SETTINGS_DEEP_LINK),
    Pair(30,Manifest.permission.LOADER_USAGE_STATS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_LOCK_STATE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ACCESSIBILITY),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ACCOUNT_MANAGEMENT),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_FULL),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ACROSS_USERS_SECURITY_CRITICAL),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_AIRPLANE_MODE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_APPS_CONTROL),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_APP_RESTRICTIONS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_APP_USER_DATA),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_AUDIO_OUTPUT),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_AUTOFILL),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_BACKUP_SERVICE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_BLUETOOTH),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_BUGREPORT),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_CALLS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_CAMERA),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_CERTIFICATES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_COMMON_CRITERIA_MODE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_DEBUGGING_FEATURES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_DEFAULT_SMS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_DEVICE_IDENTIFIERS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_DISPLAY),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_FACTORY_RESET),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_FUN),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_INPUT_METHODS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_INSTALL_UNKNOWN_SOURCES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_KEEP_UNINSTALLED_PACKAGES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_KEYGUARD),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_LOCALE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_LOCATION),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_LOCK),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_LOCK_CREDENTIALS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_LOCK_TASK),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_METERED_DATA),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_MICROPHONE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_MOBILE_NETWORK),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_MODIFY_USERS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_MTE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_NEARBY_COMMUNICATION),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_NETWORK_LOGGING),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_ORGANIZATION_IDENTITY),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_OVERRIDE_APN),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PACKAGE_STATE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PHYSICAL_MEDIA),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PRINTING),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PRIVATE_DNS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PROFILES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PROFILE_INTERACTION),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_PROXY),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_QUERY_SYSTEM_UPDATES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_RESET_PASSWORD),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_RESTRICT_PRIVATE_DNS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_RUNTIME_PERMISSIONS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_RUN_IN_BACKGROUND),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SAFE_BOOT),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SCREEN_CAPTURE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SCREEN_CONTENT),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SECURITY_LOGGING),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SETTINGS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SMS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_STATUS_BAR),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SUPPORT_MESSAGE),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SUSPEND_PERSONAL_APPS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SYSTEM_APPS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SYSTEM_DIALOGS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_SYSTEM_UPDATES),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_TIME),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_USB_DATA_SIGNALLING),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_USB_FILE_TRANSFER),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_USERS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_VPN),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_WALLPAPER),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_WIFI),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_WINDOWS),
    Pair(34,Manifest.permission.MANAGE_DEVICE_POLICY_WIPE_DATA),
    Pair(30,Manifest.permission.MANAGE_EXTERNAL_STORAGE),
    Pair(31,Manifest.permission.MANAGE_MEDIA),
    Pair(31,Manifest.permission.MANAGE_ONGOING_CALLS),
    Pair(26,Manifest.permission.MANAGE_OWN_CALLS),
    Pair(33,Manifest.permission.MANAGE_WIFI_INTERFACES),
    Pair(33,Manifest.permission.MANAGE_WIFI_NETWORK_SELECTION),
    Pair(33,Manifest.permission.NEARBY_WIFI_DEVICES),
    Pair(30,Manifest.permission.NFC_PREFERRED_PAYMENT_INFO),
    Pair(28,Manifest.permission.NFC_TRANSACTION_EVENT),
    Pair(33,Manifest.permission.OVERRIDE_WIFI_CONFIG),
    Pair(23,Manifest.permission.PACKAGE_USAGE_STATS),
    Pair(33,Manifest.permission.POST_NOTIFICATIONS),
    Pair(34,Manifest.permission.PROVIDE_OWN_AUTOFILL_SUGGESTIONS),
    Pair(34,Manifest.permission.PROVIDE_REMOTE_CREDENTIALS),
    Pair(30,Manifest.permission.QUERY_ALL_PACKAGES),
    Pair(33,Manifest.permission.READ_ASSISTANT_APP_SEARCH_DATA),
    Pair(33,Manifest.permission.READ_BASIC_PHONE_STATE),
    Pair(33,Manifest.permission.READ_HOME_APP_SEARCH_DATA),
    Pair(33,Manifest.permission.READ_MEDIA_AUDIO),
    Pair(33,Manifest.permission.READ_MEDIA_IMAGES),
    Pair(33,Manifest.permission.READ_MEDIA_VIDEO),
    Pair(34,Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED),
    Pair(33,Manifest.permission.READ_NEARBY_STREAMING_POLICY),
    Pair(26,Manifest.permission.READ_PHONE_NUMBERS),
    Pair(30,Manifest.permission.READ_PRECISE_PHONE_STATE),
    Pair(33,Manifest.permission.REQUEST_COMPANION_PROFILE_APP_STREAMING),
    Pair(33,Manifest.permission.REQUEST_COMPANION_PROFILE_AUTOMOTIVE_PROJECTION),
    Pair(33,Manifest.permission.REQUEST_COMPANION_PROFILE_COMPUTER),
    Pair(34,Manifest.permission.REQUEST_COMPANION_PROFILE_GLASSES),
    Pair(34,Manifest.permission.REQUEST_COMPANION_PROFILE_NEARBY_DEVICE_STREAMING),
    Pair(31,Manifest.permission.REQUEST_COMPANION_PROFILE_WATCH),
    Pair(26,Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND),
    Pair(33,Manifest.permission.REQUEST_COMPANION_SELF_MANAGED),
    Pair(31,Manifest.permission.REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND),
    Pair(26,Manifest.permission.REQUEST_COMPANION_USE_DATA_IN_BACKGROUND),
    Pair(26,Manifest.permission.REQUEST_DELETE_PACKAGES),
    Pair(23,Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
    Pair(23,Manifest.permission.REQUEST_INSTALL_PACKAGES),
    Pair(31,Manifest.permission.REQUEST_OBSERVE_COMPANION_DEVICE_PRESENCE),
    Pair(29,Manifest.permission.REQUEST_PASSWORD_COMPLEXITY),
    Pair(34,Manifest.permission.RUN_USER_INITIATED_JOBS),
    Pair(31,Manifest.permission.SCHEDULE_EXACT_ALARM),
    Pair(29,Manifest.permission.SMS_FINANCIAL_TRANSACTIONS),
    Pair(31,Manifest.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND),
    Pair(33,Manifest.permission.START_VIEW_APP_FEATURES),
    Pair(29,Manifest.permission.START_VIEW_PERMISSION_USAGE),
    Pair(33,Manifest.permission.SUBSCRIBE_TO_KEYGUARD_LOCKED_STATE),
    Pair(34,Manifest.permission.TURN_SCREEN_ON),
    Pair(31,Manifest.permission.UPDATE_PACKAGES_WITHOUT_USER_ACTION),
    Pair(28,Manifest.permission.USE_BIOMETRIC),
    Pair(33,Manifest.permission.USE_EXACT_ALARM),
    Pair(23,Manifest.permission.USE_FINGERPRINT),
    Pair(29,Manifest.permission.USE_FULL_SCREEN_INTENT),
    Pair(31,Manifest.permission.USE_ICC_AUTH_WITH_DEVICE_IDENTIFIER),
    Pair(31,Manifest.permission.UWB_RANGING),
)