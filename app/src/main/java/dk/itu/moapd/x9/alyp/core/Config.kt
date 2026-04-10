package dk.itu.moapd.x9.alyp.core

import io.github.cdimascio.dotenv.dotenv

val DATABASE_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]