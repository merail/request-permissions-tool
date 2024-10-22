package merail.tools.permissions.exceptions

class WrongTimeAccessException(
    override val message: String = "The requester wasn't initialized yet. Try to access it after Activity.onCreate method completion",
) : Exception(message)