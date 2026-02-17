package dk.itu.moapd.x9.alyp

import java.util.Date
import java.util.UUID
import java.io.Serializable

/**
 * Data class made serializable to be transfered via intents
 * TODO if possible make it parcelizable instead of serializable
 */
data class Report (
    val id: UUID,
    val title: String,
    val location: String,
    val date: Date,
    val type: String,
    val description: String,
    val severity: String,
) : Serializable