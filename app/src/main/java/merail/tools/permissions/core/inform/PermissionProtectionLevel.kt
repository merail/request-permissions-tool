package merail.tools.permissions.core.inform

internal enum class PermissionProtectionLevel(
    val code: Int,
) {
    NORMAL(0),
    DANGEROUS(1),
    SIGNATURE(2),
    SIGNATURE_OR_SYSTEM(3),
    INTERNAL(4),
    UNKNOWN(-1),
    ;

    companion object {
        fun fromCode(code: Int) = entries.firstOrNull {
            it.code == code
        } ?: UNKNOWN
    }
}