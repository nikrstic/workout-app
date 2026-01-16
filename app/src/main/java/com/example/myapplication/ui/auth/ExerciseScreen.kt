package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.myapplication.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseContent(
    viewModel: ExerciseViewModel,
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
            val lazyExerciseItems = viewModel.exercisePagingFlow.collectAsLazyPagingItems()
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(count = lazyExerciseItems.itemCount) { index ->
                    val exercise = lazyExerciseItems[index]
                    if (exercise != null) {
                        ExerciseItem(exercise = exercise, onClick = { onExerciseClick(exercise) })
                    }
                }
                lazyExerciseItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Box(
                                    Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val e = lazyExerciseItems.loadState.refresh as LoadState.Error
                            item { Text("Greska: ${e.error.localizedMessage}") }
                        }
                    }
                }
            }

        }
    }

}
@Composable
fun ExerciseItem(exercise: Exercise, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val gifPath = "file:///android_asset/gifs/${exercise.gif}"
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gifPath)
                    .decoderFactory(GifDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = exercise.bodyParts.firstOrNull()?.uppercase() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    exercise.targetMuscles.take(2).forEach { muscle ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = muscle,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    if(viewModel.selectedExercise == null){
        ExerciseContent(
            onExerciseClick = { exercise ->
                viewModel.selectExercise(exercise)
            },
            viewModel = viewModel
        )
    } else {
        ExerciseDetailScreen(
            exercise = viewModel.selectedExercise!!,
            onBackClick = {

                viewModel.selectExercise(null)
            }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ExercisePreview() {
    val fakeExercises = listOf(
        Exercise(
            exerciseId = "1",
            name = "Sklekovi",
            gif = "https://static.exercisedb.dev/media/UFGF6gk.gif",
            targetMuscles = listOf("Grudi", "Triceps"),
            bodyParts = listOf("Gornji deo tela"),
            equipments = listOf("Nema"),
            secondaryMuscles = listOf("Ramena"),
            instructions = listOf("Postavite ruke u širini ramena", "Spustite se polako")
        ),
        Exercise(
            exerciseId = "2",
            name = "Zgibovi",
            gif = "https://static.exercisedb.dev/media/UFGF6gk.gif",
            targetMuscles = listOf("Leđa", "Biceps"),
            bodyParts = listOf("Gornji deo tela"),
            equipments = listOf("Vratilo"),
            secondaryMuscles = listOf(),
            instructions = listOf("Uhvatite se za šipku", "Povucite se bradom iznad")
        )
    )
    ExerciseContent(onExerciseClick = {}, viewModel = viewModel())
}