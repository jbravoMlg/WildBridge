package dji.sampleV5.aircraft.webrtc

import dji.sampleV5.aircraft.BuildConfig

enum class WebRTCDroneCameraProfile(val displayName: String) {
    MINI_4_PRO("DJI Mini 4 Pro"),
    MATRICE_350("DJI Matrice 350 RTK")
}

data class WebRTCResolutionProfile(
    val cameraProfile: WebRTCDroneCameraProfile,
    val rank: Int,
    val label: String,
    val width: Int,
    val height: Int,
    val bitrate: Int,
    val fps: Int = 15
) {
    val detailLabel: String
        get() = "$rank - ${cameraProfile.displayName}: $label ${width}x${height}"

    fun toMediaOptions() = WebRTCMediaOptions(
        videoResolutionWidth = width,
        videoResolutionHeight = height,
        fps = fps,
        videoBitrate = bitrate,
        videoCodec = "H264"
    )
}

object WebRTCResolutionProfiles {
    private val mini4ProProfiles = listOf(
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MINI_4_PRO, 1, "Full HD", 1920, 1080, 5_000_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MINI_4_PRO, 2, "HD", 1280, 720, 2_000_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MINI_4_PRO, 3, "SD", 640, 480, 900_000)
    )

    private val matrice350Profiles = listOf(
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MATRICE_350, 1, "Full HD", 1920, 1080, 5_000_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MATRICE_350, 2, "HD", 1280, 720, 2_000_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MATRICE_350, 3, "qHD", 960, 540, 1_200_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MATRICE_350, 4, "L2 LiDAR", 640, 512, 900_000),
        WebRTCResolutionProfile(WebRTCDroneCameraProfile.MATRICE_350, 5, "Thermal Low", 336, 256, 450_000)
    )

    fun activeCameraProfile(): WebRTCDroneCameraProfile {
        return if (BuildConfig.DRONE_CONTROL_PROFILE_M350) {
            WebRTCDroneCameraProfile.MATRICE_350
        } else {
            WebRTCDroneCameraProfile.MINI_4_PRO
        }
    }

    fun profiles(cameraProfile: WebRTCDroneCameraProfile = activeCameraProfile()): List<WebRTCResolutionProfile> {
        return when (cameraProfile) {
            WebRTCDroneCameraProfile.MINI_4_PRO -> mini4ProProfiles
            WebRTCDroneCameraProfile.MATRICE_350 -> matrice350Profiles
        }
    }

    fun defaultProfile(cameraProfile: WebRTCDroneCameraProfile = activeCameraProfile()): WebRTCResolutionProfile {
        if (cameraProfile == WebRTCDroneCameraProfile.MINI_4_PRO) {
            return profiles(cameraProfile).firstOrNull { it.width == 1920 && it.height == 1080 }
                ?: profiles(cameraProfile).first()
        }

        return profiles(cameraProfile).firstOrNull { it.width == 1280 && it.height == 720 }
            ?: profiles(cameraProfile).first()
    }
}
