package merail.tools.permissions.common

class WrongTimeInitializationException(
    override val message: String = "You should initialize RuntimePermissionRequester in Activity.onCreate method!",
) : Exception(message)