package merail.tools.debug

import android.content.pm.PermissionInfo
import androidx.activity.ComponentActivity

class InternalPermissionsInformer(
    activity: ComponentActivity,
    private val permissions: Array<String> = arrayOf(),
) {

    private val packageManager = activity.packageManager

    val packageManagerPermissions by lazy {
        packageManager
            .getAllPermissionGroups(0)
            .toMutableList()
            .apply {
                add(null)
            }.flatMap { permissionGroupInfo ->
                packageManager.queryPermissionsByGroup(permissionGroupInfo?.name, 0)
            }
            .filter {
                it.name in permissions
            }
    }

    fun isInstallTime(
        permission: String,
    ) = packageManagerPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionLevel == PermissionProtectionLevel.NORMAL
    } ?: false

    fun isRuntime(
        permission: String,
    ) = packageManagerPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionLevel == PermissionProtectionLevel.DANGEROUS
    } ?: false

    fun isSpecial(
        permission: String,
    ) = packageManagerPermissions.find {
        it.name == permission
    }?.run {
        permissionProtectionFlags and PermissionInfo.PROTECTION_FLAG_APPOP != 0 &&
                isInstallTime(permission).not()
    } ?: false

    fun isSystem(
        permission: String,
    ) = packageManagerPermissions.find {
        it.name == permission
    }?.run {
        (permissionProtectionLevel == PermissionProtectionLevel.SIGNATURE ||
                permissionProtectionLevel == PermissionProtectionLevel.SIGNATURE_OR_SYSTEM ||
                permissionProtectionLevel == PermissionProtectionLevel.INTERNAL) &&
                isSpecial(permission).not()
    } ?: false

    fun protectionToString(
        level: Int,
        flags: Int,
    ): String {
        val protectionLevel = StringBuilder()
        when (level) {
            PermissionProtectionLevel.NORMAL.code -> protectionLevel.append("normal")
            PermissionProtectionLevel.DANGEROUS.code -> protectionLevel.append("dangerous")
            PermissionProtectionLevel.SIGNATURE.code -> protectionLevel.append("signature")
            PermissionProtectionLevel.SIGNATURE_OR_SYSTEM.code -> protectionLevel.append("signatureOrSystem")
            PermissionProtectionLevel.INTERNAL.code -> protectionLevel.append("internal")
            else -> protectionLevel.append("????")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_PRIVILEGED != 0) {
            protectionLevel.append("|privileged")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_DEVELOPMENT != 0) {
            protectionLevel.append("|development")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_APPOP != 0) {
            protectionLevel.append("|appop")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_PRE23 != 0) {
            protectionLevel.append("|pre23")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_INSTALLER != 0) {
            protectionLevel.append("|installer")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_VERIFIER != 0) {
            protectionLevel.append("|verifier")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_PREINSTALLED != 0) {
            protectionLevel.append("|preinstalled")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_SETUP != 0) {
            protectionLevel.append("|setup")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_INSTANT != 0) {
            protectionLevel.append("|instant")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_RUNTIME_ONLY != 0) {
            protectionLevel.append("|runtime")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_OEM != 0) {
            protectionLevel.append("|oem")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_VENDOR_PRIVILEGED != 0) {
            protectionLevel.append("|vendorPrivileged")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_SYSTEM_TEXT_CLASSIFIER != 0) {
            protectionLevel.append("|textClassifier")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_DOCUMENTER != 0) {
            protectionLevel.append("|textClassifier")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_CONFIGURATOR != 0) {
            protectionLevel.append("|configurator")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_INCIDENT_REPORT_APPROVER != 0) {
            protectionLevel.append("|incidentReportApprover")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_APP_PREDICTOR != 0) {
            protectionLevel.append("|appPredictor")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_COMPANION != 0) {
            protectionLevel.append("|companion")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_RETAIL_DEMO != 0) {
            protectionLevel.append("|retailDemo")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_RECENTS != 0) {
            protectionLevel.append("|recents")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_ROLE != 0) {
            protectionLevel.append("|role")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_KNOWN_SIGNER != 0) {
            protectionLevel.append("|knownSigner")
        }
        if (flags and PermissionProtectionFlags.PROTECTION_FLAG_MODULE != 0) {
            protectionLevel.append("|module")
        }
        return protectionLevel.toString()
    }
}