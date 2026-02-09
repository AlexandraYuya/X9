package dk.itu.moapd.x9.alyp.ui.main

data class Report(
    val title: String,
    val location: String,
    val date: String,
    val type: String,
    val description: String,
    val severity: String,
)