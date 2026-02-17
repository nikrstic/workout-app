package com.example.myapplication.ui.auth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun WorkoutSessionScreen(
    planId: Long?,
    viewModel: SessionViewModel = hiltViewModel(),
    exerciseViewModel: ExerciseViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    LaunchedEffect(planId) {
        viewModel.startSession(planId)
        exerciseViewModel.loadPlanDetails(planId!!)
    }

    val exercises = exerciseViewModel.exerciseInPlan
    val currentIndex = viewModel.currentExerciseIndex

    if (exercises.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentExercise = exercises[currentIndex]

    Scaffold(
        topBar = {
            LinearProgressIndicator(
                progress ={ (currentIndex + 1).toFloat() / exercises.size},
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vežba ${currentIndex + 1} od ${exercises.size}",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = currentExercise.exerciseName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isResting) {
                // UI ZA PAUZU
                Text("ODMORI SE", color = MaterialTheme.colorScheme.primary)
                Text(
                    text = "${viewModel.restTimeLeft}s",
                    style = MaterialTheme.typography.displayLarge
                )
                Button(onClick = { viewModel.isResting = false }) {
                    Text("Preskoči pauzu")
                }
            } else {
                // UI ZA VEŽBU
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cilj: ${currentExercise.defaultSets} serija x ${currentExercise.defaultReps} ponavljanja")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.startRest(currentExercise.restSeconds) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Završi seriju (Pauza)")
                }

                TextButton(
                    onClick = { viewModel.nextExercise(onFinish) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val label = if (currentIndex == exercises.size - 1) "Završi trening" else "Sledeća vežba"
                    Text(label)
                }
            }
        }
    }
}