package com.example.myapplication.ui.auth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
@Composable
fun WorkoutSessionScreen(
    planId: Long?,
    viewModel: SessionViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    LaunchedEffect(planId) {
        viewModel.startSession(planId)
    }

    val exercises = viewModel.exercises
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
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / exercises.size },
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vežba ${currentIndex + 1}/${exercises.size}", style = MaterialTheme.typography.labelLarge)
                    Text("Vreme: ${viewModel.formatTime(viewModel.totalSeconds)}", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Naziv vežbe i cilj
            Text(currentExercise.exerciseName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Cilj: ${currentExercise.defaultSets} serija x ${currentExercise.defaultReps} rep", color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            if (viewModel.isResting) {
                // UI ZA ODMOR
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("ODMOR", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${viewModel.restTimeLeft}s", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Button(onClick = { viewModel.isResting = false }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Preskoči pauzu")
                    }
                }
            } else {
                // INPUT POLJA ZA SET
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.weightInput,
                            onValueChange = { viewModel.weightInput = it },
                            label = { Text("kg") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = viewModel.repsInput,
                            onValueChange = { viewModel.repsInput = it },
                            label = { Text("reps") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Button(
                            onClick = { viewModel.finishSet(currentExercise.restSeconds) },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("SET")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Odrađene serije:", modifier = Modifier.align(Alignment.Start), style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    itemsIndexed(viewModel.completedSetsList) { index, set ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Serija ${index + 1}", fontWeight = FontWeight.Bold)
                                Text("${set.weight} kg x ${set.reps}")
                                Text("✅", color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.nextExercise(onFinish) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentIndex == exercises.size - 1) Color(0xFFE91E63) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (currentIndex == exercises.size - 1) "ZAVRŠI TRENING" else "SLEDEĆA VEŽBA")
            }
        }
    }
}