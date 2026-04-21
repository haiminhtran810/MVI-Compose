package tmh.learn.weathercompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import org.koin.androidx.compose.koinViewModel
import tmh.learn.weathercompose.domain.entity.DailyForecast
import tmh.learn.weathercompose.domain.entity.Forecast
import tmh.learn.weathercompose.domain.entity.HourlyForecast
import tmh.learn.weathercompose.domain.entity.Location
import tmh.learn.weathercompose.domain.entity.Weather
import tmh.learn.weathercompose.ui.theme.WeatherComposeTheme
import tmh.learn.weathercompose.ui.WeatherUiState
import tmh.learn.weathercompose.ui.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherComposeTheme {
                WeatherRoute()
            }
        }
    }
}

@Composable
private fun WeatherRoute(
    viewModel: WeatherViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(state.requestLocationPermission) {
        if (state.requestLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            viewModel.onPermissionRequestConsumed()
        }
    }

    WeatherContent(
        state = state,
        onAutoDetectLocation = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.onAutoDetectLocationClicked(hasPermission)
        },
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSelectLocation = viewModel::onLocationSelected,
        onRemoveSavedLocation = viewModel::onRemoveSavedLocation
    )
}

@Composable
private fun WeatherContent(
    state: WeatherUiState,
    onAutoDetectLocation: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectLocation: (Location) -> Unit,
    onRemoveSavedLocation: (Location) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeaderCard(
                    state = state,
                    onAutoDetectLocation = onAutoDetectLocation
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearchQueryChanged(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search location") },
                    singleLine = true
                )
            }

            if (state.isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Text(
                            text = "Loading weather...",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            state.errorMessage?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (state.searchResults.isNotEmpty()) {
                item {
                    SectionTitle("Search results")
                }
                items(state.searchResults) { location ->
                    SearchLocationItem(location = location, onSelectLocation = onSelectLocation)
                }
            }

            if (state.forecast?.hourly?.isNotEmpty() == true) {
                item {
                    SectionTitle("Hourly forecast")
                    HourlyForecastRow(state.forecast.hourly)
                }
            }

            if (state.weather != null) {
                item {
                    SectionTitle("Weather details")
                    WeatherHighlightsRow(state.weather)
                }
            }

            if (state.forecast?.daily?.isNotEmpty() == true) {
                item {
                    SectionTitle("Next days")
                    DailyForecastSection(state.forecast.daily)
                }
            }

            if (state.savedLocations.isNotEmpty()) {
                item {
                    SectionTitle("Saved locations")
                    SavedLocationsRow(
                        locations = state.savedLocations,
                        onSelectLocation = onSelectLocation,
                        onRemoveSavedLocation = onRemoveSavedLocation
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCard(
    state: WeatherUiState,
    onAutoDetectLocation: () -> Unit
) {
    val locationText = state.currentLocation?.let {
        if (it.country.isBlank()) it.name else "${it.name}, ${it.country}"
    } ?: "Pick a location"

    val description = state.weather?.description.orEmpty()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current weather",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = formatToday(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = locationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Button(onClick = onAutoDetectLocation) {
                    Text("Auto-detect")
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = state.weather?.temperature?.let { "${it.toInt()}°" } ?: "--°",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Feels like ${state.weather?.feelsLike?.toInt() ?: "--"}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (description.isNotBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                state.weather?.iconId?.takeIf { it.isNotBlank() }?.let { iconId ->
                    AsyncImage(
                        model = iconUrl(iconId),
                        contentDescription = description.ifBlank { "Weather icon" },
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherHighlightsRow(weather: Weather) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            MetricCard(
                title = "Humidity",
                value = "${weather.humidity}%",
                subtitle = "Air moisture"
            )
        }
        item {
            MetricCard(
                title = "Wind",
                value = "${"%.1f".format(weather.windSpeed)} m/s",
                subtitle = "Wind speed"
            )
        }
        item {
            MetricCard(
                title = "Feels Like",
                value = "${weather.feelsLike.toInt()}°",
                subtitle = "Perceived temp"
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SearchLocationItem(
    location: Location,
    onSelectLocation: (Location) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectLocation(location) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (location.country.isBlank()) location.name else "${location.name}, ${location.country}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Lat ${"%.2f".format(location.latitude)}, Lon ${"%.2f".format(location.longitude)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun HourlyForecastRow(hourly: List<HourlyForecast>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(hourly) { hour ->
            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = formatHour(hour.timeEpoch), style = MaterialTheme.typography.labelLarge)
                    AsyncImage(
                        model = iconUrl(hour.iconId),
                        contentDescription = hour.description,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(text = "${hour.temperature.toInt()}°", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
private fun DailyForecastSection(daily: List<DailyForecast>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        daily.forEachIndexed { index, day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = iconUrl(day.iconId),
                        contentDescription = day.description,
                        modifier = Modifier.size(30.dp)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = formatDay(day.dateEpoch), style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = day.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text(
                    text = "${day.maxTemp.toInt()}° / ${day.minTemp.toInt()}°",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            if (index < daily.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SavedLocationsRow(
    locations: List<Location>,
    onSelectLocation: (Location) -> Unit,
    onRemoveSavedLocation: (Location) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(locations) { location ->
            AssistChip(
                onClick = { onSelectLocation(location) },
                label = { Text(if (location.country.isBlank()) location.name else "${location.name}, ${location.country}") },
                trailingIcon = {
                    IconButton(onClick = { onRemoveSavedLocation(location) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove location"
                        )
                    }
                }
            )
        }
    }
}

private fun formatHour(epochSeconds: Long): String {
    val date = Date(epochSeconds * 1000L)
    return SimpleDateFormat("ha", Locale.getDefault()).format(date)
}

private fun formatDay(epochSeconds: Long): String {
    val date = Date(epochSeconds * 1000L)
    return SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date)
}

private fun formatToday(): String {
    return SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
}

private fun iconUrl(iconId: String): String = "https://openweathermap.org/img/wn/${iconId}@2x.png"

@Preview(showBackground = true)
@Composable
private fun WeatherContentPreview() {
    WeatherComposeTheme {
        WeatherContent(
            state = WeatherUiState(
                currentLocation = Location("Ho Chi Minh City", "VN", 10.77, 106.69),
                weather = Weather(
                    temperature = 31.0,
                    feelsLike = 35.0,
                    description = "scattered clouds",
                    iconId = "03d",
                    humidity = 75,
                    windSpeed = 3.1,
                    conditionCode = 802
                ),
                forecast = Forecast(
                    hourly = listOf(
                        HourlyForecast(1713680400, 30.0, "02d", "few clouds"),
                        HourlyForecast(1713684000, 31.0, "03d", "scattered clouds"),
                        HourlyForecast(1713687600, 29.0, "10d", "light rain")
                    ),
                    daily = listOf(
                        DailyForecast(1713657600, 27.0, 33.0, "02d", "few clouds"),
                        DailyForecast(1713744000, 26.0, 32.0, "10d", "light rain")
                    )
                ),
                searchResults = listOf(
                    Location("Da Nang", "VN", 16.05, 108.20),
                    Location("Singapore", "SG", 1.35, 103.81)
                ),
                savedLocations = listOf(
                    Location("Tokyo", "JP", 35.68, 139.69),
                    Location("Bangkok", "TH", 13.75, 100.50)
                )
            ),
            onAutoDetectLocation = {},
            onSearchQueryChanged = {},
            onSelectLocation = {},
            onRemoveSavedLocation = {}
        )
    }
}