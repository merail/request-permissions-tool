package merail.tools.permissions

import androidx.annotation.Keep

@Keep
enum class RuntimePermissionState {
    GRANTED,
    DENIED,
    IGNORED,
    PERMANENTLY_DENIED,
}