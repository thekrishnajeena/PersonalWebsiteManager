package com.krishnajeena.personalwebsitemanager.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.krishnajeena.personalwebsitemanager.data.Book
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBooksScreen(onBookAdded: (Book) -> Unit = {}, navController: NavController) {
    val id = remember { System.currentTimeMillis().toInt() } // Auto-generated ID based on timestamp
    var title by remember { mutableStateOf("") }
    var cover by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Status") }

    var summary by remember { mutableStateOf("") }
    var keyTakeaways by remember { mutableStateOf("") }
    var lessons by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var links by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // For image storage URI

    val context: Context = LocalContext.current
    val isFormValid = title.isNotEmpty() && summary.isNotEmpty()

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadImageToFirebase(it, context) { cover = it.toString() } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Cover Input - URL or Image Picker
        OutlinedTextField(
            value = cover,
            onValueChange = { cover = it },
            label = { Text("Cover URL or Paste Image") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    // Open image picker on trailing icon click
                    if(cover.isEmpty())launcher.launch("image/*")
                }) {
                    Icon(Icons.Default.Face, contentDescription = "Pick Image")
                }
            }
        )

        // If an image URI is selected, show the image
        imageUri?.let {
            AsyncImage(model = it, contentDescription = "Selected Cover Image", modifier = Modifier.size(100.dp))
        }

        // Status Dropdown
        var expanded by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = status,
            onValueChange = {},
            label = { Text("Status") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier.clickable { expanded = !expanded } // Toggle dropdown on click
                )
            },
            readOnly = true, // Prevent manual typing
            enabled = true
        )

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Close dropdown when clicking outside
        ) {
            DropdownMenuItem(
                onClick = {
                    status = "Reading" // Update status
                    expanded = false // Close the dropdown
                },
                text = {
                    Text("Reading")
                }
            )
            DropdownMenuItem(
                onClick = {
                    status = "Read" // Update status
                    expanded = false // Close the dropdown
                },
                text = {
                    Text("Read")
                }
            )
        }


        // Summary TextArea
        OutlinedTextField(
            value = summary,
            onValueChange = { summary = it },
            label = { Text("Summary") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        // Key Takeaways Input
        OutlinedTextField(
            value = keyTakeaways,
            onValueChange = { keyTakeaways = it },
            label = { Text("Key Takeaways (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Lessons Input
        OutlinedTextField(
            value = lessons,
            onValueChange = { lessons = it },
            label = { Text("Lessons (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Notes Input
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Links Input
        OutlinedTextField(
            value = links,
            onValueChange = { links = it },
            label = { Text("Links (comma-separated)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )


        // Add Book Button
        Button(
            onClick = {
                if (cover.isEmpty()) {
                    Toast.makeText(context, "Please select or enter a cover image.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isAdding = true
                val book = hashMapOf(
                    "id" to id,
                    "title" to title,
                    "cover" to cover, // This will be the image URL
                    "status" to status,
                    "summary" to summary,
                    "keyTakeaways" to keyTakeaways.split(",").filter { it.isNotEmpty() },
                    "lessons" to lessons.split(",").filter { it.isNotEmpty() },
                    "notes" to notes.split(",").filter { it.isNotEmpty() },
                    "links" to links.split(",").filter { it.isNotEmpty() }
                )

                // Save to Firestore
                Firebase.firestore.collection("books")
                    .add(book)
                    .addOnSuccessListener {
                        isAdding = false
                        Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                      //  onBookAdded(true)  // Navigate or update UI
                    }
                    .addOnFailureListener {
                        isAdding = false
                        Toast.makeText(context, "Error adding book", Toast.LENGTH_SHORT).show()
                    }
            },
            enabled = isFormValid && !isAdding,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isAdding) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Add Book")
            }
        }
    }



}

fun uploadImageToFirebase(uri: Uri, context: Context, function: (str: String) -> Unit, ) {
    val storageReference = Firebase.storage.reference
    val imageRef = storageReference.child("book_images/${UUID.randomUUID()}.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // After uploading the image, get the download URL
               // cover = downloadUrl.toString() // Set the cover URL with the Firebase Storage URL
                function(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
}