package me.rail.dev

import android.Manifest
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
import merail.tools.permissions.inform.PermissionsInformer
import merail.tools.permissions.runtime.RuntimePermissionRequester
import merail.tools.permissions.runtime.RuntimePermissionState
import merail.tools.permissions.special.SpecialPermissionRequester

class DevActivity : ComponentActivity() {

    companion object {
        private const val TAG = "DevActivity"
    }

    private val runtimePermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
    )

    private val specialPermission = Manifest.permission.SYSTEM_ALERT_WINDOW

    private lateinit var runtimePermissionRequester: RuntimePermissionRequester
    
    private lateinit var specialPermissionRequester: SpecialPermissionRequester

    private lateinit var permissionsInformer: PermissionsInformer

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
            CheckPermissionsButton()

            RequestRuntimePermissionsButton()

            RequestSpecialPermissionsButton()
        }
    }

    @Composable
    private fun CheckPermissionsButton() {
        Button(
            onClick = {
                permissionsInformer.permissions.forEach {
                    if (permissionsInformer.isSystem(it)) {
                        Log.d(TAG, it)
                    }
                }
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
                text = "Check permissions",
                style = Typography.titleLarge,
            )
        }
    }

    @Composable
    private fun RequestRuntimePermissionsButton() {
        Button(
            onClick = {
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
                text = "Request runtime permissions",
                style = Typography.titleLarge,
            )
        }
    }

    @Composable
    private fun RequestSpecialPermissionsButton() {
        Button(
            onClick = {
                if (specialPermissionRequester.isPermissionGranted().not()) {
                    specialPermissionRequester.requestPermission()
                }
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
                text = "Request special permissions",
                style = Typography.titleLarge,
            )
        }
    }
}