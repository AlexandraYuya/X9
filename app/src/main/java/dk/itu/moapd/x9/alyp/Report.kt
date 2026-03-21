package dk.itu.moapd.x9.alyp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
import java.io.Serializable

/**
 * Entity annotation indicates that the class deines the strutcure of a table in the DB.
 * Each row represents an individual crime.
 * An app can have several DBs, hence an entity isn't automatically used by Room to create a table unless it's explicitly associated to a DB.
 */
@Entity
data class Report (
    @PrimaryKey val id: UUID,
    val title: String,
    val location: String,
    val date: Date,
    val type: String,
    val description: String,
    val severity: String,
) : Serializable