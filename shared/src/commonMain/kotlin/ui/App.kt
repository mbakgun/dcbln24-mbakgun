package ui

import Greeting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ui.model.MessageUiState.MessageUiStateDefault
import ui.model.MessageUiState.MessageUiStateError
import ui.model.MessageUiState.MessageUiStateLoading
import ui.model.MessageUiState.MessageUiStateSuccess

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Start.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = navController::navigateUp
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = Screen.Start.name) {
                StartScreen(
                    onRestClick = { navController.navigate(Screen.REST.name) },
                    onSseClick = { navController.navigate(Screen.SSE.name) },
                    onWsClick = { navController.navigate(Screen.WS.name) }
                )
            }
            composable(route = Screen.REST.name) {
                RestScreen(
                    modifier = Modifier.padding(16.dp)
                )
            }
            composable(route = Screen.SSE.name) {
                SSEScreen(
                    modifier = Modifier.padding(16.dp)
                )
            }
            composable(route = Screen.WS.name) {
                WSScreen(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun RestScreen(
    viewModel: DemoViewModel = viewModel { DemoViewModel() },
    modifier: Modifier = Modifier,
) {
    val state by viewModel.messageUiState.collectAsStateWithLifecycle()

    Text("REST Screen", modifier = modifier)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val value = state) {
            is MessageUiStateDefault -> {
                Button(onClick = viewModel::fetchMessage) {
                    Text("Click to Fetch")
                }
            }

            is MessageUiStateLoading -> CircularProgressIndicator()
            is MessageUiStateSuccess -> Text(value.message)
            is MessageUiStateError -> Text(value.error)
        }
    }
}

@Composable
fun SSEScreen(
    viewModel: DemoViewModel = viewModel { DemoViewModel() },
    modifier: Modifier = Modifier
) {
    val state by viewModel.messageUiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text("SSE Screen")

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val value = state) {
                is MessageUiStateDefault -> {
                    Button(onClick = viewModel::fetchMessageSSE) {
                        Text("Click to Fetch SSE")
                    }
                }

                is MessageUiStateLoading -> CircularProgressIndicator()
                is MessageUiStateSuccess -> Text(value.message)
                is MessageUiStateError -> Text(value.error)
            }
        }
    }
}

@Composable
fun WSScreen(
    viewModel: DemoViewModel = viewModel { DemoViewModel() },
    modifier: Modifier = Modifier
) {
    val state by viewModel.messageUiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Text("WS Screen")

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val value = state) {
                is MessageUiStateDefault -> {
                    Button(onClick = viewModel::connectWebSocket) {
                        Text("Connect to WebSocket")
                    }
                }

                is MessageUiStateLoading -> CircularProgressIndicator()
                is MessageUiStateSuccess -> {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(value.message)
                        Spacer(modifier = Modifier.height(200.dp))
                        val sliderValue = remember { mutableStateOf(50f) }
                        Slider(
                            value = sliderValue.value,
                            onValueChange = { newValue ->
                                sliderValue.value = newValue
                                viewModel.sendWsMessage(newValue)
                            },
                            valueRange = 0f..100f,
                            modifier = Modifier
                                .rotate(90f)
                        )
                    }
                }

                is MessageUiStateError -> Text(value.error)
            }
        }
    }
}

/**
 * The `StartScreen` composable displays a welcome message and three buttons to navigate to the REST, SSE, and WS screens.
 */
@Composable
fun StartScreen(
    onRestClick: () -> Unit,
    onSseClick: () -> Unit,
    onWsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Start Screen " + Greeting().greet(),
        modifier = modifier.padding(16.dp)
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRestClick) { Text("REST") }
        Button(onClick = onSseClick) { Text("SSE") }
        Button(onClick = onWsClick) { Text("WS") }
    }
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.title) },
        modifier = modifier,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}
