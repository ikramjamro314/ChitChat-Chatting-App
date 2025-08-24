package com.ikramjamro.chitchat.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.CCViewModel
import com.ikramjamro.chitchat.commonImage

enum class State {
    INITIAL, ACTIVE, COMPLETE
}

@Composable
fun SingleStatusScreen(navController: NavController, vm: CCViewModel, userID: String) {
    val statuses = vm.status.value.filter {
        it.user.userID == userID
    }
    if (statuses.isNotEmpty()) {
        val currentStatuses = remember {
            mutableStateOf(0)
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            commonImage(
                data = statuses[currentStatuses.value].statusImageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Row(Modifier.fillMaxWidth()) {
                statuses.forEachIndexed { index, status ->
                    CommonProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .padding(1.dp)
                            .height(7.dp),
                        state = if (currentStatuses.value < index) State.INITIAL
                        else if (currentStatuses.value == index) State.ACTIVE
                        else State.COMPLETE
                    ) {
                       if(currentStatuses.value < statuses.size-1) currentStatuses.value++ else navController.popBackStack()
                    }
                }
            }


        }

    }


}

@Composable
fun CommonProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {
    var progress = if (state == State.INITIAL) 0f else 1f
    if (state == State.ACTIVE) {
        val toggleState = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(toggleState) {
            toggleState.value = true

        }
        val p: Float by animateFloatAsState(
            if (toggleState.value) 1f else 0f,
            animationSpec = tween(5000),
            finishedListener = { onComplete.invoke() }
        )
        progress = p

    }

    LinearProgressIndicator(progress = progress, modifier = modifier, color = Color.Red)


}