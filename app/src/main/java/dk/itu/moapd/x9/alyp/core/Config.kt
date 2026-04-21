package dk.itu.moapd.x9.alyp.core

import io.github.cdimascio.dotenv.dotenv

/**
 * Config file to access a references to sensitive resources from env file.
 */
val DATABASE_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]

val FIREBASE_STORAGE_BUCKET: String = dotenv {
    directory = "/assets"
    filename = "env"
}["FIREBASE_STORAGE_BUCKET"]