package net.freiday.remotecompose.shared

import kotlinx.serialization.Serializable

@Serializable
data class DocumentCatalog(
    val documents: List<DocumentInfo>
)

@Serializable
data class DocumentInfo(
    val id: String,
    val title: String,
    val description: String,
)
