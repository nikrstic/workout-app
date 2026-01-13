package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.myapplication.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseContent(
    exercises: List<Exercise>,
    isLoading: Boolean,
    onExerciseClick: (Exercise) -> Unit
    ) {
    Scaffold(
        topBar = {
            TopAppBar(title= {Text("Dostupne vezbe")})
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if(isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else{
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(exercises){
                        exercise->
                        ExerciseItem(exercise = exercise, onClick = {onExerciseClick(exercise)})
                    }
                }
            }

        }
    }

}
@Composable
fun ExerciseItem(exercise: Exercise, onClick: ()-> Unit){
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    )
    {
        Column(modifier = Modifier.padding(16.dp)){
            Text(
                text = exercise.name.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: ${exercise.targetMuscles.joinToString(", ") }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel = hiltViewModel()
){
    ExerciseContent(
      exercises = viewModel.exercises,
        isLoading = viewModel.isLoading,
        onExerciseClick = {
            exercise ->
            println("Kliknuto na ${exercise.name}")
        }
    )
}
@Preview(showBackground = true)
@Preview(showBackground = true)
@Composable
fun ExercisePreview() {
    val fakeExercises = listOf(
        Exercise(
            exerciseId = "1",
            name = "Sklekovi",
            gif = "",
            targetMuscles = listOf("Grudi", "Triceps"),
            bodyParts = listOf("Gornji deo tela"),
            equipments = listOf("Nema"),
            secondaryMuscles = listOf("Ramena"),
            instructions = listOf("Postavite ruke u širini ramena", "Spustite se polako")
        ),
        Exercise(
            exerciseId = "2",
            name = "Zgibovi",
            gif = "",
            targetMuscles = listOf("Leđa", "Biceps"),
            bodyParts = listOf("Gornji deo tela"),
            equipments = listOf("Vratilo"),
            secondaryMuscles = listOf(),
            instructions = listOf("Uhvatite se za šipku", "Povucite se bradom iznad")
        )
    )
    ExerciseContent(exercises = fakeExercises, isLoading = false, onExerciseClick = {})
}