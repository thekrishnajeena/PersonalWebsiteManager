package com.krishnajeena.personalwebsitemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.personalwebsitemanager.ui.screens.AddBooksScreen
import com.krishnajeena.personalwebsitemanager.ui.screens.BooksScreen
import com.krishnajeena.personalwebsitemanager.ui.screens.MeScreen
import com.krishnajeena.personalwebsitemanager.ui.screens.MessagesScreen
import com.krishnajeena.personalwebsitemanager.ui.theme.PersonalWebsiteManagerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalWebsiteManagerTheme {

                val navController = rememberNavController()
                val items = listOf(Screen.MessageScreen, Screen.BooksScreen)

                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = {
                            Text(text = "Who Knows?")
                        })
                    },
                    floatingActionButton = {
                        if(currentRoute(navController) == Screen.BooksScreen.route){

                            FloatingActionButton(onClick = {
                                navController.navigate("addBooks")

                            }){
                                Icon(Icons.Default.Add, contentDescription = "")
                            }

                    }
                    },
                    floatingActionButtonPosition = FabPosition.Center,
                    bottomBar = {
                        BottomAppBar {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                val currentRoute = currentRoute(navController) // Debug this value

                                IconButton(
                                    onClick = {
                                        navController.navigate("message") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Messages",
                                        tint = if (currentRoute == "message") Color.Black else Color.Gray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        navController.navigate("books") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBox,
                                        contentDescription = "Books",
                                        tint = if (currentRoute == "books") Color.Black else Color.Gray
                                    )
                                }


                                IconButton(
                                    onClick = {
                                        navController.navigate("me") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Me",
                                        tint = if (currentRoute == "me") Color.Black else Color.Gray
                                    )
                                }

                                }
                        }
                    }

                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.MessageScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.MessageScreen.route) { MessagesScreen() }
                        composable(Screen.BooksScreen.route) { BooksScreen() }
                        composable(Screen.AddBooksScreen.route) { AddBooksScreen(navController = navController) }
                        composable(Screen.MeScreen.route) { MeScreen(navController = navController) }
                    }
                }
            }
        }
    }

    sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
        object MessageScreen : Screen("message", "Messages", Icons.Default.Menu)
        object BooksScreen : Screen("books", "Books", Icons.Default.AccountBox)
        object AddBooksScreen : Screen("addBooks", "Add Books", Icons.Default.Add)
        object MeScreen : Screen("me", "Me", Icons.Default.Person)
    }

}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}