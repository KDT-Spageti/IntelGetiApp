//package com.example.intelgetiapp
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import android.os.AsyncTask
//import android.util.Log
//import okhttp3.OkHttpClient
//import okhttp3.Request
//
//class MainActivity2 : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // AsyncTask를 사용하여 백그라운드에서 서버에서 데이터 가져오기
//        GetTodoListTask().execute()
//    }
//
//    private inner class GetTodoListTask : AsyncTask<Void, Void, String>() {
//        override fun doInBackground(vararg params: Void?): String? {
//            return getTodoListFromServer()
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
//            if (result != null) {
//                Log.d("TodoList", result)
//                // 여기에서 결과를 처리하거나 UI에 표시
//            } else {
//                Log.e("TodoList", "Failed to get data from server.")
//            }
//        }
//    }
//
//    private fun getTodoListFromServer(): String? {
//        val url = "http://127.0.0.1:5000/todos/"
//
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        try {
//            val response = client.newCall(request).execute()
//            return response.body()?.string()
//        } catch (e: Exception) {
//            Log.e("TodoList", "Error: ${e.message}")
//        }
//
//        return null
//    }
//}
