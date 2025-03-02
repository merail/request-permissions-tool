package merail.tools.permissions.core

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log

internal object SettingsOpener {

    private const val SCHEME = "package"

    fun openSettings(activity: Activity, action: String) {
        Log.d(TAG, "Settings are opening with action $action")
        val intent = Intent(
            action,
            Uri.fromParts(SCHEME, activity.packageName, null),
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        activity.startActivity(intent)
    }
}
