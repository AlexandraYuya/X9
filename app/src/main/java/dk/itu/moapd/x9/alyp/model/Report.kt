package dk.itu.moapd.x9.alyp.model

import android.net.Uri
import java.io.Serializable

/**
 * Entity annotation indicates that the class deines the strutcure of a table in the DB.
 * Each row represents an individual crime.
 * An app can have several DBs, hence an entity isn't automatically used by Room to create a table unless it's explicitly associated to a DB.
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