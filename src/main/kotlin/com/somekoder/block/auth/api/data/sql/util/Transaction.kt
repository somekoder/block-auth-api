package com.somekoder.block.auth.api.data.sql.util

import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.IOException
import java.sql.SQLException

suspend fun <T> transaction(
    dispatcher: CoroutineDispatcher,
    onUnhandledException: (Exception) -> T,
    body: suspend () -> T,
    onNetworkException: (IOException) -> T = { onUnhandledException(it) },
    onUniquenessException: (Exception) -> T = { onUnhandledException(it) },
) : T = newSuspendedTransaction(dispatcher) {
    return@newSuspendedTransaction try {
        body()
    } catch (e: SQLException) {
        return@newSuspendedTransaction when {
            e.isNetworkException() -> onNetworkException(IOException(e))
            e.isUniqueConstraintViolation() -> onUniquenessException(e)
            else -> throw e
        }
    } catch (e: Exception) {
        onUniquenessException(e)
    }
}

private fun SQLException.isNetworkException(): Boolean {
    return this.sqlState?.startsWith("08") == true
}

private fun SQLException.isUniqueConstraintViolation(): Boolean {
    return this.sqlState?.startsWith("23") == true &&
            (this.message?.contains("duplicate") == true ||
                    this.message?.contains("unique") == true)
}