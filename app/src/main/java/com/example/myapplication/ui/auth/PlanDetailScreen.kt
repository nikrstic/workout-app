package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.myapplication.data.auth.responses.PlanExercisesResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    viewModel: ExerciseViewModel = hiltViewModel(),
    onBack: () -> Unit
){
    LaunchedEffect(planId) {
        viewModel.loadPlanDetails(planId)
    }
    val exercises = viewModel.exerciseInPlan
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
            if (exercises.isNotEmpty()) {
                LazyColumn {
                    items(exercises) { item ->
                        ExerciseItemRow(item)
                    }
                }
            } else {
                Text(
                    "Ovaj plan još uvek nema dodatih vežbi.",
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }

}
@Composable
fun ExerciseItemRow(item: PlanExercisesResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.exerciseName,
                style = typography.titleMedium
            )
            Text(
                text = "Serije: ${item.defaultSets} | Ponavljanja: ${item.defaultReps}",
                style = typography.titleMedium
            )
        }
    }
}

