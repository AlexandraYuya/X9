package dk.itu.moapd.x9.alyp.core

import io.github.cdimascio.dotenv.dotenv

/**
 * Config file to access a references to sensitive resources from env file.
 */
private val env = dotenv {
    directory = "/assets"
    filename = "env"
}
val DATABASE_URL: String = env["DATABASE_URL"]
val FIREBASE_STORAGE_BUCKET: String = env["FIREBASE_STORAGE_BUCKET"]