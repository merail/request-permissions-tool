package merail.tools.permissions.exceptions

public class WrongTimeInitializationException(
    override val message: String = "You should initialize RuntimePermissionRequester in Activity.onCreate method!",
) : Exception(message)