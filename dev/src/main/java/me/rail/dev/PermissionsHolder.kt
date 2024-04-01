package me.rail.dev

import android.content.res.Configuration
import android.util.Log
import androidx.activity.ComponentActivity
import java.util.Locale

class PermissionsHolder(
    private val activity: ComponentActivity,
) {
    fun test() {
        val config = Configuration()

        config.locale = Locale("en")
        val pm = activity.packageManager
        val groupList = pm.getAllPermissionGroups(0)
        // ungrouped permissions
        groupList.add(null)
        for (permissionGroup in groupList) {
            val name = permissionGroup?.name
            Log.d("AppLog", "permission group `$name`")
            try {
                val permissions = pm.queryPermissionsByGroup(name, 0)
                for (permission in permissions) {
                    if (permission.name.startsWith("android.permission.")) {
                        Log.d(
                            "AppLog",
                            "${permission.name} - ${permission.protection} - ${
                                permission.descriptionRes
                            }"
                        )
                    }
                }
            } catch (ex: Exception) {
                Log.d("AppLog", "exception while getting permissions of permission group: `$name`")
            }
            Log.d("AppLog", "-----")
        }
    }
}