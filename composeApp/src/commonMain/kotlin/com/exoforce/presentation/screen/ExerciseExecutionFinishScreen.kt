package com.exoforce.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exoforce.core.media.createSoundPlayer
import com.exoforce.presentation.component.base.AppButton
import exoforce.composeapp.generated.resources.Res
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ExerciseExecutionFinishScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundPlayer = remember { createSoundPlayer() }

    // Load Lottie animation
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie/good_job.json").decodeToString()
        )
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    val painter = rememberLottiePainter(
        composition = composition,
        progress = { progress }
    )

    // Play sound once when screen is shown
    LaunchedEffect(Unit) {
        try {
            val audioData = Res.readBytes("files/sound/good_job.mp3")
            soundPlayer.play(audioData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPlayer.release()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bien joué !",
                    style = MaterialTheme.typography.headlineMedium
                )
                // Lottie animation
                Image(
                    painter = painter,
                    contentDescription = "Good job animation",
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Finish button
                AppButton(
                    onClick = onFinish,
                    text = "Terminer !",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
