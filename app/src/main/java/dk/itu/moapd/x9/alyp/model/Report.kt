package dk.itu.moapd.x9.alyp.model

import java.io.Serializable

/**
 * Represented an incidents report submitted by a user.
 * Implements serializable to allow passing the report object between fragments via a Bundle
 */
data class Report(
    val uid: String= "",
    val title: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: Long = 0L,
    val type: String = "",
    val description: String = "",
    val severity: String = "",
    val user: String = "",
    val imageUrl: String = "",
    val upvoteCount: Int = 0
) : Serializable