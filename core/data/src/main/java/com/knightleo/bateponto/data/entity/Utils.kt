package com.knightleo.bateponto.data.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.encodeToString() =
    if(canBeSerialized) Json.encodeToString(this) else Json.encodeToString(toString())

inline fun <reified T> String.decode(): T = Json.decodeFromString(this)

inline fun <reified T> String.decodeSafe(): T? = runCatching { decode<T>() }.getOrNull()

@PublishedApi
internal inline val <reified T> T.canBeSerialized: Boolean
    get() = T::class.java.annotations.any { it is Serializable }