package com.apps.requesttracker.presentation.mainScreen

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apps.requesttracker.R
import com.apps.requesttracker.data.db.QueryEntity
import com.apps.requesttracker.data.service.MyAccessibilityService
import com.apps.requesttracker.presentation.mainScreen.viewModel.QueryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val queryViewModel: QueryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val queries by queryViewModel.allQueries.observeAsState(emptyList())
            val isAccessibilityEnabled by queryViewModel.isAccessibilityServiceEnabled.collectAsState()

            MyApp(queries) {
                queryViewModel.delete(it)
            }

            if (isAccessibilityEnabled == false) {
                AccessibilityDialog { queryViewModel.checkAccessibilityService(this, MyAccessibilityService::class.java) }
            }
        }

        queryViewModel.checkAccessibilityService(this, MyAccessibilityService::class.java)
    }

    override fun onResume() {
        super.onResume()
        queryViewModel.checkAccessibilityService(this, MyAccessibilityService::class.java)
    }
}

@Composable
fun AccessibilityDialog(onCancel: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(text = "Accessibility Service Required") },
        text = { Text("This app requires the Accessibility Service to function properly. Please enable it in the settings.") },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MyApp(queries: List<QueryEntity>, onDeleteClick: (QueryEntity) -> Unit) {
    MaterialTheme {
        if (queries.isNotEmpty()) {
            QueryList(queries, onDeleteClick)
        } else {
            EmptyListMessage()
        }
    }
}

@Composable
fun EmptyListMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_records_available),
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            lineHeight = 45.sp
        )
        Text(
            modifier = Modifier.padding(top = 30.dp),
            text = stringResource(R.string.make_queries_so_the_list_gets_updated_dynamically),
            fontSize = 18.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )
    }
}

@Composable
fun QueryList(queries: List<QueryEntity>, onDeleteClick: (QueryEntity) -> Unit) {
    LazyColumn {
        items(queries) { query ->
            QueryItem(query, onDeleteClick)
        }
    }
}

@Composable
fun QueryItem(query: QueryEntity, onDeleteClick: (QueryEntity) -> Unit) {
    var expandedQuery by remember { mutableStateOf(false) }
    var expandedWebSiteLink by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = query.requestText,
                maxLines = if (expandedQuery) Int.MAX_VALUE else 2,
                overflow = if (expandedQuery) TextOverflow.Visible else TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedQuery = !expandedQuery }
            )
            SpacerWithDivider()
            Text(
                text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date(query.requestDateTime)
                ),
            )
            SpacerWithDivider()
            query.websiteLink?.let {
                Text(
                    text = it,
                    maxLines = if (expandedWebSiteLink) Int.MAX_VALUE else 2,
                    overflow = if (expandedWebSiteLink) TextOverflow.Visible else TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedWebSiteLink = !expandedWebSiteLink }
                )
            }
            Button(
                onClick = { onDeleteClick(query) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
private fun SpacerWithDivider() {
    Divider(
        modifier = Modifier
            .padding(top = 2.dp)
            .height(1.5.dp)
            .fillMaxWidth()
            .background(Color.Gray)
    )
    Spacer(modifier = Modifier.height(10.dp))
}
