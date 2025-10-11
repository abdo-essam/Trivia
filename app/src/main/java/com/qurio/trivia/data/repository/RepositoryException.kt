package com.qurio.trivia.data.repository

// ========== Repository Exceptions ==========

sealed class RepositoryException(message: String) : Exception(message)

class ApiException(message: String) : RepositoryException(message)
class NoQuestionsException(message: String) : RepositoryException(message)
class InvalidParameterException(message: String) : RepositoryException(message)
class TokenNotFoundException(message: String) : RepositoryException(message)
class TokenEmptyException(message: String) : RepositoryException(message)
class UnknownApiException(message: String) : RepositoryException(message)
class NetworkException(message: String, cause: Throwable? = null) : RepositoryException(message)