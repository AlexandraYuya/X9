package dk.itu.moapd.x9.alyp

import java.util.Date
import java.util.UUID
import java.io.Serializable

data class Report (
    val id: UUID,
    val title: String,
    val location: String,
    val date: Date,
    val type: String,
    val description: String,
    val severity: String,
) : Serializable