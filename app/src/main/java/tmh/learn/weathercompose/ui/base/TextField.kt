package tmh.learn.weathercompose.ui.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AppOutlinedTextField(
    modifier: Modifier = Modifier,
    title: String,
    onSearchQueryChanged: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onSearchQueryChanged(it)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { AppText(modifier = modifier, text = title) },
        singleLine = true
    )
}