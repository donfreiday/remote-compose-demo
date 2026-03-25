package net.freiday.remotecompose.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configurePlugins()
        configureRouting()
    }.start(wait = true)
}

fun Application.configurePlugins() {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
    }
    install(CallLogging)
}

fun Application.configureRouting() {
    val documentService = DocumentService()

    routing {
        get("/") {
            call.respondText("Remote Compose Server is running!", ContentType.Text.Plain)
        }

        get("/api/catalog") {
            call.respond(documentService.getCatalog())
        }

        get("/api/document/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            try {
                val bytes = documentService.getDocument(id)
                if (bytes != null) {
                    call.respondBytes(bytes, ContentType.Application.OctetStream)
                } else {
                    call.respondText("Document not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText(
                    "Error: ${e::class.simpleName}: ${e.message}",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
