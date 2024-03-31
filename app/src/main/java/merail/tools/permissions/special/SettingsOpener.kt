package merail.tools.permissions.special

import android.app.Activity
import android.content.Intent
import android.net.Uri

object SettingsOpener {

    private const val SCHEME = "package"

    fun openSettings(activity: Activity, action: String) {
        val intent = Intent(
            action,
            Uri.fromParts(SCHEME, activity.packageName, null)
        )
        activity.startActivity(intent)
    }
}
