package com.project.autocompleteapp.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

fun <T> handleApiRequest(apiRequest: suspend () -> T): Flow<Resource<T>> = flow {
    emit(Resource.Loading())
    val response = apiRequest.invoke()
    emit(Resource.Success(response))
}.catch { emit(Resource.Error(it.message.orEmpty())) }
