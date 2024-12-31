package com.krishnajeena.personalwebsitemanager.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    modifier: Modifier = Modifier,
    onAddBookClick: () -> Unit={}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val books = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Fetch books from Firebase
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("books")
            .get()
            .addOnSuccessListener { result ->
                val fetchedBooks = result.documents.mapNotNull { it.data }
                books.value = fetchedBooks
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch books.", Toast.LENGTH_SHORT).show()
            }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            selectedBook?.let { book ->
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()), // Enable scrolling if the content is larger
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(book["title"] as String, style = MaterialTheme.typography.headlineMedium)
                    Text(book["summary"] as String, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    book["lessons"]?.let {
                        Text("Lessons", style = MaterialTheme.typography.titleMedium)
                        (it as List<*>).forEach { lesson ->
                            Text("- $lesson")
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) {
        Scaffold(modifier = modifier) { padding ->
            if (books.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("There is nothing Anna!", style = MaterialTheme.typography.bodyLarge)
                    Text("😂", style = MaterialTheme.typography.headlineMedium)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(books.value) { book ->
                        val coverUrl = book["cover"]
BookCard(bookTitle = book["title"] as String, bookCoverUrl = coverUrl.toString(), bookStatus = book["status"] as String


) {
    selectedBook = book
    coroutineScope.launch { sheetState.show() }


}


                    }
                }
            }
        }
    }

}

@Composable
fun BookCard(
    bookTitle: String,
    bookCoverUrl: String,
    bookStatus: String,
    function: () -> Job
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(220.dp)
            .padding(8.dp).clickable{function.invoke()}

    ) {
        // Book Cover
        AsyncImage(
            model = bookCoverUrl,
            contentDescription = "Book Cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )

        // Status Overlay
        if (bookStatus.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(if(bookStatus=="Read")Color.Green
                    else Color(0xAA000000), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = bookStatus,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

