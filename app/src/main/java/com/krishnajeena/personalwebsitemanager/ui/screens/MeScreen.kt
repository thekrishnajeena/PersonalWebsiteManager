package com.krishnajeena.personalwebsitemanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.krishnajeena.personalwebsitemanager.R

@Composable
fun MeScreen(modifier: Modifier = Modifier, navController: NavHostController) {

    var blogName by remember{mutableStateOf("Who Knows!")}

    Column(modifier = Modifier.fillMaxSize()){

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            AsyncImage(model = R.drawable.prof2, contentDescription = null,
                modifier = Modifier.height(150.dp).padding(5.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            OutlinedTextField(value = blogName, onValueChange = {blogName = it})
        }


    }

}