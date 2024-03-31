package merail.tools.permissions.special

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import merail.tools.permissions.runtime.RuntimePermissionRequester

internal object SettingsOpener {

    private const val SCHEME = "package"

    fun openSettings(activity: Activity, action: String) {
        Log.d(RuntimePermissionRequester.TAG, "Settings are opening with action $action")
        val intent = Intent(
            action,
            Uri.fromParts(SCHEME, activity.packageName, null)
        )
        activity.startActivity(intent)
    }
}
