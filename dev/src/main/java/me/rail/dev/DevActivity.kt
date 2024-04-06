package me.rail.dev

import android.Manifest
import android.content.pm.PermissionInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import merail.tools.permissions.inform.InternalPermissionsInformer
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.runtime.RuntimePermissionRequester
import merail.tools.permissions.runtime.RuntimePermissionState
import merail.tools.permissions.special.SpecialPermissionRequester

class DevActivity : ComponentActivity() {

    companion object {
        private const val TAG = "DevActivity"
    }

    private val runtimePermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    private val specialPermission = Manifest.permission.MANAGE_EXTERNAL_STORAGE

    private lateinit var runtimePermissionRequester: RuntimePermissionRequester
    
    private lateinit var specialPermissionRequester: SpecialPermissionRequester

    private lateinit var permissionsInformer: PermissionsInformer

    private lateinit var internalPermissionsInformer: InternalPermissionsInformer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RequestPermissionsToolTheme {
                Content()
            }
        }

        runtimePermissionRequester = RuntimePermissionRequester(
            activity = this@DevActivity,
            requestedPermissions = runtimePermissions,
        )

        specialPermissionRequester = SpecialPermissionRequester(
            activity = this@DevActivity,
            requestedPermission = specialPermission,
        )

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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
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
                .height(72.dp)
                .padding(
                    horizontal = 12.dp,
                ),
            shape = RoundedCornerShape(18.dp),
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
            specialPermissionRequester.requestPermission()
        }
    }
}