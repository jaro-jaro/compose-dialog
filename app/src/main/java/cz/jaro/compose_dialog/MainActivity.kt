package cz.jaro.compose_dialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.jaro.compose_dialog.ui.theme.ComposedialogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposedialogTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

val state = AlertDialogState()

@Composable
fun Greeting(modifier: Modifier = Modifier) {

    AlertDialog(state = state)

    Button(onClick = {
        state.show(
            confirmButton = {
                TextButton(onClick = {
                    state.show(
                        confirmButton = {

                        },
                        dismissButton = {
                            TextButton(onClick = {
                                state.hideTopMost()
                            }) {
                                Text("W")
                            }
                        },
                    )
                }) {
                    Text("aha")
                }
            },
            text = {
                Text("Bluu")
            }
        )
    }) {

    }
}