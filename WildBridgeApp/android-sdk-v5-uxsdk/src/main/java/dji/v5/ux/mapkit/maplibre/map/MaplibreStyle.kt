package dji.v5.ux.mapkit.maplibre.map

import dji.v5.ux.mapkit.core.Mapkit

/**
 * @author feel.feng
 * @time 2024/07/10 17:27
 * @description: 升级9.5.x后 style 被移除
 */
object MaplibreStyle {
	@JvmField
	val MAPBOX_STREETS: String = styleUrl(
		mapboxStyle = "mapbox://styles/mapbox/streets-v11",
		mapTilerStyle = "streets-v2"
	)

	@JvmField
	val SATELLITE_STREETS: String = styleUrl(
		mapboxStyle = "mapbox://styles/mapbox/satellite-streets-v11",
		mapTilerStyle = "hybrid"
	)

	@JvmField
	val SATELLITE: String = styleUrl(
		mapboxStyle = "mapbox://styles/mapbox/satellite-v9",
		mapTilerStyle = "satellite"
	)

	private fun styleUrl(mapboxStyle: String, mapTilerStyle: String): String {
		val token = runCatching { Mapkit.getMapboxAccessToken() }.getOrNull().orEmpty().trim()
		return if (token.startsWith("pk.")) {
			mapboxStyle
		} else {
			"https://api.maptiler.com/maps/$mapTilerStyle/style.json?key=$token"
		}
	}
}