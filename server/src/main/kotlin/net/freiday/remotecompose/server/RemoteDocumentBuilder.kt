package net.freiday.remotecompose.server

import androidx.compose.remote.core.RcPlatformServices
import androidx.compose.remote.core.RcProfiles
import androidx.compose.remote.creation.RemoteComposeContext
import androidx.compose.remote.creation.RemoteComposeWriter

@Suppress("RestrictedApiAndroidX")
val jvmPlatform = object : RcPlatformServices {
    override fun imageToByteArray(image: Any): ByteArray? = null
    override fun getImageWidth(image: Any): Int = 0
    override fun getImageHeight(image: Any): Int = 0
    override fun isAlpha8Image(image: Any): Boolean = false
    override fun pathToFloatArray(path: Any): FloatArray? = null
    override fun parsePath(pathData: String): Any = Object()
    override fun log(category: RcPlatformServices.LogCategory, message: String) {
        println("[RC/$category] $message")
    }
}

@Suppress("RestrictedApiAndroidX")
fun buildDocument(
    width: Int,
    height: Int,
    contentDescription: String = "RemoteDocument",
    apiLevel: Int = 7,
    profiles: Int = RcProfiles.PROFILE_ANDROIDX or RcProfiles.PROFILE_EXPERIMENTAL,
    content: RemoteComposeContext.() -> Unit,
): ByteArray {
    val ctx = RemoteComposeContext(
        width, height, contentDescription, apiLevel, profiles, jvmPlatform, content
    )
    return ctx.mRemoteWriter.toByteArray()
}

@Suppress("RestrictedApiAndroidX")
fun buildCanvasDocument(
    width: Int,
    height: Int,
    contentDescription: String = "RemoteDocument",
    apiLevel: Int = 6,
    content: RemoteComposeWriter.() -> Unit,
): ByteArray {
    val writer = RemoteComposeWriter(width, height, contentDescription, apiLevel, 0, jvmPlatform)
    writer.content()
    return writer.toByteArray()
}

@Suppress("RestrictedApiAndroidX")
private fun RemoteComposeWriter.toByteArray(): ByteArray {
    return ByteArray(bufferSize()).also {
        System.arraycopy(buffer(), 0, it, 0, bufferSize())
    }
}
