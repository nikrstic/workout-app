package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    viewModel: ExerciseViewModel = hiltViewModel(),
    onBack: () -> Unit
){
    LaunchedEffect(planId) {
        //viewModel.loadPlanDetails(planId)
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text ("Detalji plana")})},
        bottomBar = {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ){
                Text("zapocni trening")
            }
        }
    ){
        padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Lista vežbi",
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}