package dk.itu.moapd.x9.alyp.core

import io.github.cdimascio.dotenv.dotenv

val DATABASE_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]

val FIREBASE_STORAGE_BUCKET: String = dotenv {
    directory = "/assets"
    filename = "env"
}["FIREBASE_STORAGE_BUCKET"]