package net.freiday.remotecompose.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import net.freiday.remotecompose.shared.DocumentCatalog
import net.freiday.remotecompose.shared.DocumentInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

sealed interface UiState {
    data object Loading : UiState
    data class CatalogLoaded(val catalog: DocumentCatalog) : UiState
    data class DocumentLoaded(val info: DocumentInfo, val bytes: ByteArray) : UiState
    data class Error(val message: String) : UiState
}

class RemoteComposeViewModel : ViewModel() {

    // Use 10.0.2.2 for Android emulator to reach host localhost
    private val baseUrl = "http://10.0.2.2:8080"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val catalog = withContext(Dispatchers.IO) { fetchCatalog() }
                _uiState.value = UiState.CatalogLoaded(catalog)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load catalog: ${e.message}")
            }
        }
    }

    fun loadDocument(info: DocumentInfo) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val bytes = withContext(Dispatchers.IO) { fetchDocument(info.id) }
                _uiState.value = UiState.DocumentLoaded(info, bytes)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load document: ${e.message}")
            }
        }
    }

    private fun fetchCatalog(): DocumentCatalog {
        val request = Request.Builder().url("$baseUrl/api/catalog").build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: throw Exception("Empty response")
        return json.decodeFromString<DocumentCatalog>(body)
    }

    private fun fetchDocument(id: String): ByteArray {
        val request = Request.Builder().url("$baseUrl/api/document/$id").build()
        val response = client.newCall(request).execute()
        return response.body?.bytes() ?: throw Exception("Empty response")
    }
}
