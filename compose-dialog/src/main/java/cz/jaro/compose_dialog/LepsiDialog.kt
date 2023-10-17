package cz.jaro.compose_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.window.DialogProperties

sealed interface AlertDialogInfo {
    data class Material(
        val confirmButton: @Composable context(AlertDialogScope) () -> Unit,
        val modifier: Modifier = Modifier,
        val dismissButton: @Composable (context(AlertDialogScope) () -> Unit)? = null,
        val onDismissed: (() -> Unit)? = null,
        val icon: @Composable (context(AlertDialogScope) () -> Unit)? = null,
        val title: @Composable (context(AlertDialogScope) () -> Unit)? = null,
        val content: @Composable (context(ColumnScope, AlertDialogScope) () -> Unit)? = null,
        val properties: DialogProperties = DialogProperties(),
    ) : AlertDialogInfo

    data class Plain(
        val modifier: Modifier = Modifier,
        val onDismissed: (() -> Unit)? = null,
        val content: @Composable (AlertDialogScope.() -> Unit)? = null,
        val properties: DialogProperties = DialogProperties(),
    ) : AlertDialogInfo
}

interface AlertDialogScope {
    fun hide()
}

private class AlertDialogScopeImpl(
    private val state: AlertDialogStateImpl,
): AlertDialogScope {
    override fun hide() = state.hideTopMost()
}

interface AlertDialogState {
    fun show(info: AlertDialogInfo)
}

fun AlertDialogState(): AlertDialogState = AlertDialogStateImpl()

private class AlertDialogStateImpl : AlertDialogState {

    var infos: List<AlertDialogInfo> by mutableStateOf(emptyList())
        private set

    override fun show(info: AlertDialogInfo) {
        this.infos += info
    }

    fun hideTopMost() {
        this.infos = this.infos.dropLast(1)
    }
}

fun AlertDialogState.show(
    confirmButton: @Composable context(AlertDialogScope) () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (context(AlertDialogScope) () -> Unit)? = null,
    onDismissed: (() -> Unit)? = null,
    icon: @Composable (context(AlertDialogScope) () -> Unit)? = null,
    title: @Composable (context(AlertDialogScope) () -> Unit)? = null,
    content: @Composable (context(ColumnScope, AlertDialogScope) () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) = show(
    AlertDialogInfo.Material(
        confirmButton, modifier, dismissButton, onDismissed, icon, title, content, properties
    )
)

fun AlertDialogState.show(
    modifier: Modifier = Modifier,
    onDismissed: (() -> Unit)? = null,
    content: @Composable (AlertDialogScope.() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) = show(
    AlertDialogInfo.Plain(
        modifier, onDismissed, content, properties
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    state: AlertDialogState,
) {
    state as AlertDialogStateImpl

    val scope: AlertDialogScope = remember {
        AlertDialogScopeImpl(state)
    }
    state.infos.forEach { info ->
        when(info) {
            is AlertDialogInfo.Material -> androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    state.hideTopMost()
                    info.onDismissed?.invoke()
                },
                confirmButton = {
                    info.confirmButton(scope)
                },
                modifier = info.modifier,
                dismissButton = {
                    info.dismissButton?.invoke(scope)
                },
                icon = {
                    info.icon?.invoke(scope)
                },
                title = {
                    info.title?.invoke(scope)
                },
                text = {
                    info.content?.let {
                        Column(
                            Modifier.fillMaxWidth()
                        ) {
                            it(this, scope)
                        }
                    }
                },
                properties = info.properties
            )
            is AlertDialogInfo.Plain -> androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    state.hideTopMost()
                    info.onDismissed?.invoke()
                },
                modifier = info.modifier,
                content = {
                    info.content?.invoke(scope)
                },
                properties = info.properties
            )
        }
    }
}

fun Modifier.autoFocus() = composed {

    val focusRequester = remember { FocusRequester() }
    val windowInfo = LocalWindowInfo.current

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                focusRequester.requestFocus()
            }
        }
    }

    focusRequester(focusRequester)
}