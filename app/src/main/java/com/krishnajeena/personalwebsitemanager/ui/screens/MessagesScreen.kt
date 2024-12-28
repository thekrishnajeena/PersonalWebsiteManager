package com.krishnajeena.personalwebsitemanager.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
@Composable
fun MessagesScreen(modifier: Modifier = Modifier) {
    val msgs = remember { mutableStateListOf<Triple<String, String, String>>() }
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Load messages incrementally
    LaunchedEffect(Unit) {
        db.collection("messages").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            snapshot?.documents?.forEach { document ->
                val createdAt = document["createdAt"].toString()
                val sender = document["sender"].toString()
                val content = document["content"].toString()
                if (!msgs.any { it.first == createdAt && it.second == sender && it.third == content }) {
                    msgs.add(Triple(createdAt, sender, content))
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(4.dp)
        ) {
            items(msgs, key = { it.first }) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = msg.third, modifier = Modifier.padding(5.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = msg.second, modifier = Modifier.padding(5.dp))
                                Text(
                                    text = runCatching {
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                                            .withZone(ZoneId.systemDefault())
                                            .format(
                                                Instant.ofEpochSecond(
                                                    msg.first.substringAfter("seconds=").substringBefore(",").toLong()
                                                )
                                            )
                                    }.getOrElse { "Invalid timestamp" },
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
