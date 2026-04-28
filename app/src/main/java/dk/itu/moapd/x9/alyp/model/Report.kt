package dk.itu.moapd.x9.alyp.model

import com.google.firebase.database.Exclude
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
    @get:Exclude // so firebase never writes this value to DB
    val isConfirmed: Boolean = false
) : Serializable