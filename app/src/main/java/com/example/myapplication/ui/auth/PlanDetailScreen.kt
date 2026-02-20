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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.myapplication.data.auth.responses.PlanExercisesResponse
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    viewModel: ExerciseViewModel = hiltViewModel(),
    navController: NavController,
    onBack: () -> Unit
){
    LaunchedEffect(planId) {
        viewModel.loadPlanDetails(planId)
        viewModel.setPlanId(planId)
    }
    val exercises = viewModel.exerciseInPlan
    Scaffold(
        topBar = { TopAppBar(title = { Text ("Detalji plana")},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                }
            }
        )},
        bottomBar = {
            Button(
                onClick = { navController.navigate("active_workout/$planId")},
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
                        ExerciseItemRow(item=item) {
                            viewModel.deleteExercise(item.id)
                        }
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
fun ExerciseItemRow(
    item: PlanExercisesResponse,
    onDeleteClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ){
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.exerciseName,
                    style = typography.titleMedium
                )
                Text(
                    text = "Serije: ${item.defaultSets} | Ponavljanja: ${item.defaultReps}",
                    style = typography.titleMedium
                )
            }
            IconButton(onClick = { onDeleteClick(item.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Obriši vežbu",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}