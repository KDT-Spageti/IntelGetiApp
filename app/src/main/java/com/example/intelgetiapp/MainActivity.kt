@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.intelgetiapp

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.intelgetiapp.ui.theme.IntelGetiAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelGetiAppTheme {
                Mainscreen()
            }
        }
    }

    @Composable
    fun Mainscreen() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                OneImageLoader()
                Spacer(modifier = Modifier.size(50.dp))
            }
        }
    }

    @Composable
    private fun OneImageLoader() {
        var selectUri by remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            //url == 유니크한 경로
            onResult = { uri ->
                selectUri = uri
                lifecycleScope.launch {
                    selectUri?.let {
                        UploadImage(it)
                    }
                }

            }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "기본이미지",
            modifier = Modifier
                .size(100.dp)
                .shadow(2.dp)
                .clickable {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
        )
    }

    suspend fun UploadImage(imageUri: Uri) = withContext(Dispatchers.IO)  {
        val url = "http://192.168.1.111:5000/predict"
        val client = OkHttpClient()

        val inputStream = contentResolver.openInputStream(imageUri)
        val file = createFileFromInputStream(inputStream)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "image.png",
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // Image uploaded successfully
//                Toast.makeText(context,"이미지 업로드 성공",Toast.LENGTH_SHORT).show()
                val responseBody = response.body()?.string()

                Log.d("성공함", "이미지가 올라갔다? Respones : ${responseBody?: "no data"}")
            } else {
//                Toast.makeText(context,"망함",Toast.LENGTH_SHORT).show()
                Log.e("망함", "망함")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle exception
        }
    }


    private suspend fun createFileFromInputStream(inputStream: InputStream?): File = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "image.png")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return@withContext file
    }

}

//@Composable
//fun TodoScreen() {
//    var textState by remember { mutableStateOf("Click the button to load data from server") }
//    var loadingState by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//    ) {
//        // 버튼
//        Button(
//            onClick = {
//                // 클릭 시 서버에서 데이터 받아오기
//                loadingState = true
//                GetTodoListTask { result ->
//                    textState = result ?: "Failed to get data from server"
//                    loadingState = false
//                }.execute()
//            },
//            enabled = !loadingState
//        ) {
//            Text("Load Data from Server")
//        }
//
//        // 텍스트 결과 표시
//        Text(
//            text = textState,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun TodoScreen2() {
//    var textState by remember { mutableStateOf("Click the button to load data from server") }
//    var loadingState by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//    ) {
//        // 버튼
//        Button(
//            onClick = {
//                // 클릭 시 서버에서 데이터 받아오기
//                loadingState = true
//                GetTodoListTask2 { result ->
//                    textState = if (!result.isNullOrEmpty()) {
//                        result.joinToString(", ") // List<String>을 문자열로 변환
//                    } else {
//                        "Failed to get data from server"
//                    }
//                    loadingState = false
//                }.execute()
//            },
//            enabled = !loadingState
//        ) {
//            Text("Load Data from Server")
//        }
//
//        // 텍스트 결과 표시
//        Text(
//            text = textState,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }
//}
//
//
//class GetTodoListTask(private val callback: (String?) -> Unit) : AsyncTask<Void, Void, String>() {
//    override fun doInBackground(vararg params: Void?): String? {
//        return getTodoListFromServer()
//    }
//
//    override fun onPostExecute(result: String?) {
//        super.onPostExecute(result)
//        callback(result)
//    }
//}
//
//class GetTodoListTask2(private val callback: (List<String>?) -> Unit) : AsyncTask<Void, Void, List<String>>() {
//    override fun doInBackground(vararg params: Void?): List<String>? {
//        return getTodoListFromServer2()
//    }
//
//    override fun onPostExecute(result: List<String>?) {
//        super.onPostExecute(result)
//        callback(result)
//    }
//}
//
//
//fun getTodoListFromServer(): String? {
//    val url = "http://192.168.1.111:5000/todos/"
//
//    val client = OkHttpClient()
//    val request = Request.Builder()
//        .url(url)
//        .build()
//
//    try {
//        val response = client.newCall(request).execute()
//
//        // 추가: 서버 응답 코드 확인
//        if (!response.isSuccessful) {
//            Log.e("TodoList", "Server responded with error: ${response.code()}")
//            return null
//        }
//
//        return response.body()?.string()
//    } catch (e: Exception) {
//        Log.e("TodoList", "Error: ${e.message}")
//    }
//
//    return null
//}
//
//fun getTodoListFromServer2(): List<String>? {
//    val url = "http://192.168.1.111:5000/todos/"
//
//    val client = OkHttpClient()
//    val request = Request.Builder()
//        .url(url)
//        .build()
//
//    try {
//        val response = client.newCall(request).execute()
//
//        if (!response.isSuccessful) {
//            Log.e("TodoList", "Server responded with error: ${response.code()}")
//            return null
//        }
//
//        val responseData = response.body()?.string()
//
//        // Gson을 사용하여 JSON 데이터 파싱
//        val gson = Gson()
//        val todoListResponse = gson.fromJson(responseData, TodoListResponse::class.java)
//
//        return todoListResponse?.tasks
//    } catch (e: Exception) {
//        Log.e("TodoList", "Error: ${e.message}")
//    }
//
//    return null
//}
//
//// Gson을 사용하여 파싱할 데이터 모델 정의
//data class TodoListResponse(val tasks: List<String>)

@Composable
fun CalcTest() {
    var a by remember { mutableStateOf(0.0) }
    var b by remember { mutableStateOf(0.0) }
    var result by remember { mutableStateOf<Double?>(null) } // 타입 변경

    Column {
        // a와 b 입력 필드
        OutlinedTextField(
            value = a.toString(),
            onValueChange = { a = it.toDouble() },
            label = { Text("Enter a") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = b.toString(),
            onValueChange = { b = it.toDouble() },
            label = { Text("Enter b") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // 버튼
        Button(
            onClick = {
                // 클릭 시 서버에 a와 b를 전송하고 결과를 받아옴
                GetResultFromServer(a, b) { receivedResult ->
                    result = receivedResult // 문자열을 숫자로 변환
                }.execute()
            }
        ) {
            Text("Calculate")
        }

        // 결과 표시
        result?.let {
            Text("Result: $it")
        }
    }
}

class GetResultFromServer(private val a: Double, private val b: Double, private val callback: (Double?) -> Unit) : AsyncTask<Void, Void, Double?>() {
    override fun doInBackground(vararg params: Void?): Double? {
        return getResultFromServer(a, b)
    }

    override fun onPostExecute(result: Double?) {
        super.onPostExecute(result)
        callback(result)
    }
}

fun getResultFromServer(a: Double, b: Double): Double? {
    val url = "http://192.168.1.111:5000/calculate/"

    val client = OkHttpClient()
    val requestBody = FormBody.Builder()
        .add("a", a.toString())
        .add("b", b.toString())
        .build()

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    try {
        val response = client.newCall(request).execute()

        // 서버 응답 코드 확인
        if (!response.isSuccessful) {
            Log.e("Calculation", "Server responded with error: ${response.code()}")
            return null
        }

        return response.body()?.string()?.toDouble()
    } catch (e: Exception) {
        Log.e("Calculation", "Error: ${e.message}")
    }

    return null
}
