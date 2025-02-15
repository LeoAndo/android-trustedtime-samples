package com.example.composesample

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composesample.ui.theme.ComposeSampleTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

// Use TrustedTimeClient anywhere in your app
fun test1(context: Context): Pair<Long, String> {
    val myApp = context as MyApp
    val trustedTimeClient =
        myApp.trustedTimeClient ?: throw IllegalStateException("TrustedTimeClient is not available")
    // jp) computeCurrentUnixEpochMillis()の戻り値がnullになるケースが不明
    // en) It is unknown when the return value of computeCurrentUnixEpochMillis() becomes null.
    val currentTimeMillis = trustedTimeClient.computeCurrentUnixEpochMillis()!!
    val instant = trustedTimeClient.computeCurrentInstant()
    val currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    val formattedString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime)
    return Pair(currentTimeMillis, formattedString)
}

// Use in short-lived components like Activity
suspend fun test2(context: Context): Pair<Long, String> {
    return suspendCoroutine { continuation ->
        TrustedTimeClientAccessor.getTrustedTimeClientTask(context)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val trustedTimeClient: TrustedTimeClient = task.result!!
                    // jp) computeCurrentUnixEpochMillis()の戻り値がnullになるケースが不明
                    // en) It is unknown when the return value of computeCurrentUnixEpochMillis() becomes null.
                    val currentTimeMillis = trustedTimeClient.computeCurrentUnixEpochMillis()!!
                    val instant = trustedTimeClient.computeCurrentInstant()
                    val currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                    val formattedString =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime)
                    continuation.resume(Pair(currentTimeMillis, formattedString))
                } else {
                    // Handle error
                    // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                    // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                    val exception = task.exception
                    continuation.resumeWithException(
                        IllegalStateException("TrustedTimeClient is not available", exception)
                    )
                }
            })
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var result1 by remember { mutableStateOf("") }
    var result2 by remember { mutableStateOf("") }
    var coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    GreetingStateless(
        modifier = modifier, onClick = {
            val (currentTimeMillis, formattedString) = test1(context.applicationContext)
            result1 = currentTimeMillis.toString()
            result2 = formattedString
        }, onClick2 = {
            coroutineScope.launch {
                val (currentTimeMillis, formattedString) = test2(context.applicationContext)
                result1 = currentTimeMillis.toString()
                result2 = formattedString
            }
        }, result1 = result1, result2 = result2
    )
}

@Composable
fun GreetingStateless(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onClick2: () -> Unit,
    result1: String,
    result2: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onClick) {
            Text("test1")
        }
        Button(onClick = onClick2) {
            Text("test2")
        }
        Text(text = result1)
        Text(text = result2)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview_Init() {
    ComposeSampleTheme {
        GreetingStateless(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            onClick = {}, onClick2 = {}, result1 = "", result2 = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview_Result() {
    ComposeSampleTheme {
        GreetingStateless(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            onClick = {},
            onClick2 = {},
            result1 = "1739560041059",
            result2 = "2025-02-15 04:07:21"
        )
    }
}