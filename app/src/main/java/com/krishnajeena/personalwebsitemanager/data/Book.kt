package com.krishnajeena.personalwebsitemanager.data

data class Book(
    val id: Int,
    val title: String,
    val cover: String, // URL or local file path
    val status: String,
    val summary: String,
    val keyTakeaways: List<String>,
    val lessons: List<String>,
    val notes: List<String>,
    val links: List<String>
)
