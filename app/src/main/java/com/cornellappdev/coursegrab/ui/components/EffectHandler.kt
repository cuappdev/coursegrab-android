package com.cornellappdev.coursegrab.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Drains a ViewModel's one-shot effect [Flow] while the screen is at least STARTED, the same
 * pairing the Activities used with `lifecycleScope.launch { repeatOnLifecycle(STARTED) }`.
 *
 * Collection has to be lifecycle-aware rather than a bare `LaunchedEffect`: the effect flows
 * are `Channel.receiveAsFlow()`, so an effect delivered to a backgrounded screen is consumed
 * and gone. Stopping collection leaves it buffered until the screen comes back.
 *
 * [onEffect] is captured through [rememberUpdatedState] so a lambda that closes over fresh
 * state does not restart collection — and does not act on a stale capture.
 */
@Composable
fun <T> EffectHandler(effects: Flow<T>, onEffect: suspend (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEffect by rememberUpdatedState(onEffect)

    LaunchedEffect(effects, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effects.collect { currentOnEffect(it) }
        }
    }
}
