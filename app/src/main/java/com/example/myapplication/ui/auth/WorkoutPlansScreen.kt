package com.example.myapplication.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
fun WorkoutPlanScreen(
    onPlanSelected: (Long) -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
){
    val plans = viewModel.plans

    LaunchedEffect(Unit) {
        viewModel.loadPlans()
    }

    Scaffold(topBar = {
        TopAppBar(title={
            Text("Moji planovi") }) }){padding->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(plans) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onPlanSelected(plan.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = plan.name, style = MaterialTheme.typography.displaySmall)
                        Text(text = plan.description)
                    }
                }
            }
        }
    }
}