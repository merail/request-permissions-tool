package me.rail.dev

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rail.dev.ui.theme.RequestPermissionsToolTheme
import me.rail.dev.ui.theme.Typography
import merail.tools.permissions.runtime.RuntimePermissionRequester

class DevActivity : ComponentActivity() {

    private val requestedPermissions = arrayOf(

        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.READ_CALL_LOG,
    )

    private lateinit var runtimePermissionRequester: RuntimePermissionRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RequestPermissionsToolTheme {
                Content()
            }
        }

        runtimePermissionRequester = RuntimePermissionRequester(
            activity = this@DevActivity,
            requestedPermissions = requestedPermissions,
        )
    }

    @Preview(
        showBackground = true,
    )
    @Composable
    private fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = {
                    if (runtimePermissionRequester.areAllPermissionsGranted().not()) {
                        runtimePermissionRequester.requestPermissions {
                            Log.d("DevActivity", it.toString())
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
                    text = "Request permission",
                    style = Typography.titleLarge,
                )
            }
        }
    }
}