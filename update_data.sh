#!/bin/bash
cd /Users/belive/Documents/Belive/Code/WeatherCompose

# 1. Update libs.versions.toml
cat << 'INNER_EOF' >> gradle/libs.versions.toml
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
INNER_EOF

# 2. Update settings.gradle.kts
echo 'include(":data")' >> settings.gradle.kts

# 3. Create directories
mkdir -p data/src/main/java/tmh/learn/weathercompose/data/remote/dto
mkdir -p data/src/main/java/tmh/learn/weathercompose/data/mapper
mkdir -p data/src/main/java/tmh/learn/weathercompose/data/repository

# 4. AndroidManifest.xml
cat << 'INNER_EOF' > data/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest package="tmh.learn.weathercompose.data" />
INNER_EOF

# 5. build.gradle.kts
cat << 'INNER_EOF' > data/build.gradle.kts
import java.util.Properties

val localProps = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "tmh.learn.weathercompose.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        val apiKey = localProps.getProperty("OPENWEATHER_API_KEY") ?: "MISSING_API_KEY"
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"\$apiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi.kotlin)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.moshi.kotlin.codegen)
}
INNER_EOF

# 6. DTOs
cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/remote/dto/WeatherResponseDto.kt
package tmh.learn.weathercompose.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponseDto(
    @Json(name = "weather") val weather: List<WeatherDto>,
    @Json(name = "main") val main: MainDto,
    @Json(name = "wind") val wind: WindDto
)

@JsonClass(generateAdapter = true)
data class WeatherDto(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)

@JsonClass(generateAdapter = true)
data class MainDto(
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "humidity") val humidity: Int
)

@JsonClass(generateAdapter = true)
data class WindDto(
    @Json(name = "speed") val speed: Double
)
INNER_EOF

cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/remote/dto/ForecastResponseDto.kt
package tmh.learn.weathercompose.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponseDto(
    @Json(name = "list") val list: List<ForecastItemDto>
)

@JsonClass(generateAdapter = true)
data class ForecastItemDto(
    @Json(name = "dt") val dt: Long,
    @Json(name = "main") val main: MainDto,
    @Json(name = "weather") val weather: List<WeatherDto>,
    @Json(name = "wind") val wind: WindDto
)
INNER_EOF

cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/remote/dto/GeocodingResponseDto.kt
package tmh.learn.weathercompose.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponseDto(
    @Json(name = "name") val name: String,
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "country") val country: String?
)
INNER_EOF

# 7. Mappers
cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/mapper/Mappers.kt
package tmh.learn.weathercompose.data.mapper

import tmh.learn.weathercompose.data.remote.dto.ForecastItemDto
import tmh.learn.weathercompose.data.remote.dto.ForecastResponseDto
import tmh.learn.weathercompose.data.remote.dto.GeocodingResponseDto
import tmh.learn.weathercompose.data.remote.dto.WeatherResponseDto
import tmh.learn.weathercompose.domain.entity.DailyForecast
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.HourlyForecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather

fun WeatherResponseDto.toDomain(): Weather {
    val weatherInfo = weather.firstOrNull()
    return Weather(
        temperature = main.temp,
        feelsLike = main.feelsLike,
        description = weatherInfo?.description ?: "",
        iconId = weatherInfo?.icon ?: "",
        humidity = main.humidity,
        windSpeed = wind.speed,
        conditionCode = weatherInfo?.id ?: 0
    )
}

fun ForecastResponseDto.toDomain(): Forecast {
    val hourly = list.take(8).map {
        val weatherInfo = it.weather.firstOrNull()
        HourlyForecast(
            timeEpoch = it.dt,
            temperature = it.main.temp,
            iconId = weatherInfo?.icon ?: "",
            description = weatherInfo?.description ?: ""
        )
    }

    val daily = list.groupBy { it.dt / 86400 }.values.map { dayList ->
        val minTemp = dayList.minOf { it.main.temp }
        val maxTemp = dayList.maxOf { it.main.temp }
        val firstInDay = dayList.first()
        val weatherInfo = firstInDay.weather.firstOrNull()
        DailyForecast(
            dateEpoch = firstInDay.dt,
            minTemp = minTemp,
            maxTemp = maxTemp,
            iconId = weatherInfo?.icon ?: "",
            description = weatherInfo?.description ?: ""
        )
    }.take(5)

    return Forecast(
        hourly = hourly,
        daily = daily
    )
}

fun GeocodingResponseDto.toDomain(): Location {
    return Location(
        name = name,
        country = country ?: "",
        latitude = lat,
        longitude = lon
    )
}
INNER_EOF

# 8. OpenWeatherApi
cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/remote/OpenWeatherApi.kt
package tmh.learn.weathercompose.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import tmh.learn.weathercompose.data.remote.dto.ForecastResponseDto
import tmh.learn.weathercompose.data.remote.dto.GeocodingResponseDto
import tmh.learn.weathercompose.data.remote.dto.WeatherResponseDto

interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeatherResponseDto

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): ForecastResponseDto

    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): List<GeocodingResponseDto>
}
INNER_EOF

# 9. Repositories
cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/repository/WeatherRepositoryImpl.kt
package tmh.learn.weathercompose.data.repository

import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.mapper.toDomain
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: OpenWeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            val response = api.getCurrentWeather(
                lat = latitude,
                lon = longitude,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> {
        return try {
            val response = api.getForecast(
                lat = latitude,
                lon = longitude,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
INNER_EOF

cat << 'INNER_EOF' > data/src/main/java/tmh/learn/weathercompose/data/repository/LocationRepositoryImpl.kt
package tmh.learn.weathercompose.data.repository

import tmh.learn.weathercompose.data.BuildConfig
import tmh.learn.weathercompose.data.mapper.toDomain
import tmh.learn.weathercompose.data.remote.OpenWeatherApi
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val api: OpenWeatherApi
) : LocationRepository {

    override suspend fun searchLocation(query: String): Result<List<Location>> {
        return try {
            val response = api.searchLocation(
                query = query,
                apiKey = BuildConfig.OPENWEATHER_API_KEY
            )
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentDeviceLocation(): Result<Location> {
        return Result.failure(NotImplementedError("Device location not implemented yet"))
    }

    override suspend fun getSavedLocations(): Result<List<Location>> {
        return Result.success(emptyList())
    }

    override suspend fun saveLocation(location: Location) {
    }

    override suspend fun removeLocation(location: Location) {
    }
}
INNER_EOF

