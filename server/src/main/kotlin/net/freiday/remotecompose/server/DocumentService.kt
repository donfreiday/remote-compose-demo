package net.freiday.remotecompose.server

import androidx.compose.remote.core.operations.layout.managers.BoxLayout
import androidx.compose.remote.core.operations.layout.managers.ColumnLayout
import androidx.compose.remote.core.operations.layout.managers.RowLayout
import androidx.compose.remote.creation.ComponentHeight
import androidx.compose.remote.creation.ComponentWidth
import androidx.compose.remote.creation.ContinuousSec
import androidx.compose.remote.creation.Hour
import androidx.compose.remote.creation.Minutes
import androidx.compose.remote.creation.cos
import androidx.compose.remote.creation.min
import androidx.compose.remote.creation.modifiers.RecordingModifier
import androidx.compose.remote.creation.sin
import net.freiday.remotecompose.shared.DocumentCatalog
import net.freiday.remotecompose.shared.DocumentInfo

private const val WHITE = 0xffffffff.toInt()
private const val BLACK = 0xff000000.toInt()
private const val LTGRAY = 0xffcccccc.toInt()
private const val GRAY = 0xff888888.toInt()
private const val GREEN = 0xff00cc00.toInt()
private const val BLUE = 0xff0000ff.toInt()
private const val DARK_BG = 0xff1a1a2e.toInt()
private const val ACCENT = 0xffe94560.toInt()
private const val LIGHT_TEXT = 0xffeaeaea.toInt()
private const val SUBTLE_TEXT = 0xff8b949e.toInt()
private const val CARD_BG = 0xfff5f5f5.toInt()
private const val ORANGE = 0xffff9800.toInt()
private const val CAP_ROUND = 1

class DocumentService {

    private val catalog = DocumentCatalog(
        documents = listOf(
            DocumentInfo(
                id = "greeting",
                title = "Greeting Card",
                description = "A styled card with text and colored backgrounds"
            ),
            DocumentInfo(
                id = "dashboard",
                title = "Dashboard",
                description = "Stats and recent activity in a column/row layout"
            ),
            DocumentInfo(
                id = "clock",
                title = "Animated Clock",
                description = "An analog clock using Remote Compose expressions for real-time animation"
            ),
        )
    )

    fun getCatalog(): DocumentCatalog = catalog

    fun getDocument(id: String): ByteArray? {
        return when (id) {
            "greeting" -> buildGreetingDocument()
            "dashboard" -> buildDashboardDocument()
            "clock" -> buildClockDocument()
            else -> null
        }
    }

    @Suppress("RestrictedApiAndroidX")
    private fun buildGreetingDocument(): ByteArray = buildDocument(500, 400, "Greeting Card") {
        root {
            column(Modifier.fillMaxSize().background(DARK_BG).padding(24)) {
                text("Welcome to Remote Compose!", fontSize = 28f, color = ACCENT, fontWeight = 700f)
                box(Modifier.size(0, 16))
                text(
                    "This UI was built entirely on the server using " +
                        "RemoteComposeContext from the AndroidX creation-core library.",
                    fontSize = 16f,
                    color = LIGHT_TEXT,
                )
                box(Modifier.size(0, 24))
                text(
                    "The binary document was transmitted over HTTP and " +
                        "rendered natively by RemoteComposePlayer on Android.",
                    fontSize = 14f,
                    color = SUBTLE_TEXT,
                )
                box(Modifier.size(0, 24))
                box(
                    Modifier.fillMaxWidth().background(ACCENT).padding(12),
                    BoxLayout.CENTER,
                    BoxLayout.CENTER,
                ) {
                    text("Server-Driven UI", fontSize = 18f, color = WHITE, fontWeight = 700f)
                }
            }
        }
    }

    @Suppress("RestrictedApiAndroidX")
    private fun buildDashboardDocument(): ByteArray = buildDocument(500, 600, "Dashboard") {
        root {
            column(Modifier.fillMaxSize().background(CARD_BG).padding(16)) {
                text("Dashboard", fontSize = 26f, color = BLACK, fontWeight = 700f)
                box(Modifier.size(0, 16))

                row(
                    Modifier.fillMaxWidth(),
                    RowLayout.SPACE_EVENLY,
                    RowLayout.CENTER,
                ) {
                    column(Modifier.background(WHITE).padding(12), ColumnLayout.CENTER, ColumnLayout.CENTER) {
                        text("1,284", fontSize = 22f, color = GREEN, fontWeight = 700f)
                        text("Users", fontSize = 12f, color = GRAY)
                    }
                    column(Modifier.background(WHITE).padding(12), ColumnLayout.CENTER, ColumnLayout.CENTER) {
                        text("\$12.4K", fontSize = 22f, color = BLUE, fontWeight = 700f)
                        text("Revenue", fontSize = 12f, color = GRAY)
                    }
                    column(Modifier.background(WHITE).padding(12), ColumnLayout.CENTER, ColumnLayout.CENTER) {
                        text("384", fontSize = 22f, color = ORANGE, fontWeight = 700f)
                        text("Orders", fontSize = 12f, color = GRAY)
                    }
                }

                box(Modifier.size(0, 24))

                text("Recent Activity", fontSize = 20f, color = BLACK, fontWeight = 600f)
                box(Modifier.size(0, 8))

                column(Modifier.fillMaxWidth().padding(8, 6, 8, 6)) {
                    text("New user registered", fontSize = 14f, color = BLACK)
                    text("2 min ago", fontSize = 12f, color = GRAY)
                }
                column(Modifier.fillMaxWidth().padding(8, 6, 8, 6)) {
                    text("Order #1042 completed", fontSize = 14f, color = BLACK)
                    text("15 min ago", fontSize = 12f, color = GRAY)
                }
                column(Modifier.fillMaxWidth().padding(8, 6, 8, 6)) {
                    text("Payment received", fontSize = 14f, color = BLACK)
                    text("1 hour ago", fontSize = 12f, color = GRAY)
                }
                column(Modifier.fillMaxWidth().padding(8, 6, 8, 6)) {
                    text("Server deployment", fontSize = 14f, color = BLACK)
                    text("3 hours ago", fontSize = 12f, color = GRAY)
                }
            }
        }
    }

    @Suppress("RestrictedApiAndroidX")
    private fun buildClockDocument(): ByteArray = buildCanvasDocument(500, 500, "Clock") {
        root {
            box(RecordingModifier().fillMaxSize(), BoxLayout.START, BoxLayout.START) {
                canvas(RecordingModifier().fillMaxSize()) {
                    val w = ComponentWidth()
                    val h = ComponentHeight()
                    val cx = w / 2f
                    val cy = h / 2f
                    val rad = min(cx, cy)
                    val rounding = rad / 4f

                    rcPaint.setColor(BLUE).commit()
                    drawRoundRect(
                        0f, 0f,
                        w.toFloat(), h.toFloat(),
                        rounding.toFloat(), rounding.toFloat(),
                    )

                    rcPaint.setColor(WHITE).setStrokeWidth(2f).commit()
                    for (i in 0 until 12) {
                        save()
                        rotate((i * 30f), cx.toFloat(), cy.toFloat())
                        drawLine(
                            cx.toFloat(), (cy - rad * 0.85f).toFloat(),
                            cx.toFloat(), (cy - rad * 0.75f).toFloat()
                        )
                        restore()
                    }

                    rcPaint.setColor(GRAY).setStrokeWidth(32f)
                        .setStrokeCap(CAP_ROUND).commit()
                    save()
                    rotate((Minutes() * 6f).toFloat(), cx.toFloat(), cy.toFloat())
                    drawLine(
                        cx.toFloat(), cy.toFloat(),
                        cx.toFloat(), (cy - rad * 0.8f).toFloat()
                    )
                    restore()

                    rcPaint.setColor(LTGRAY).setStrokeWidth(16f)
                        .setStrokeCap(CAP_ROUND).commit()
                    save()
                    rotate((Hour() * 30f).toFloat(), cx.toFloat(), cy.toFloat())
                    drawLine(
                        cx.toFloat(), cy.toFloat(),
                        cx.toFloat(), (cy - rad / 2f).toFloat()
                    )
                    restore()

                    rcPaint.setColor(WHITE).setStrokeWidth(4f).commit()
                    val pi2over60 = (2 * Math.PI.toFloat() / 60f)
                    drawLine(
                        cx.toFloat(), cy.toFloat(),
                        (w / 2f + rad * sin(ContinuousSec() * pi2over60)).toFloat(),
                        (h / 2f - rad * cos(ContinuousSec() * pi2over60)).toFloat(),
                    )
                }
            }
        }
    }
}
