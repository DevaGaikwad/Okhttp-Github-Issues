package com.deva.github_issues

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_github.*
import kotlinx.android.synthetic.main.activity_github.progress_bar
import kotlinx.android.synthetic.main.activity_issue_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class GithubActivity : AppCompatActivity() {

    private var requestQueue: RequestQueue? = null
    val data = ArrayList<GithubViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github)


        requestQueue = Volley.newRequestQueue(this)
        progress_bar.visibility= View.GONE

        if (isOnline(this))
            callGithubIssues()
        else
            localGithubIssues()
    }

    private fun localGithubIssues() {
        Toast.makeText(this,"Device is offline so loading offline content",Toast.LENGTH_LONG).show()
        val sharedPreference = getSharedPreferences("github_issues", Context.MODE_PRIVATE)
        val data_string = sharedPreference.getString("issues", "")
        if (data_string != null) {
            try {
                val jsonArray = JSONArray(data_string)
                for (i in 0 until jsonArray.length()) {
                    val issue = jsonArray.getJSONObject(i)
                    val cmntUrl=issue.getString("comments_url")
                    val title = issue.getString("title")
                    val description = issue.getString("body")
                    val updated_time = issue.getString("updated_at")

                    val user = issue.getJSONObject("user")
                    val uname = user.getString("login")
                    val uImage = user.getString("avatar_url")

                    val item = GithubViewModel(uImage, uname, title, description, updated_time,cmntUrl)
                    data.add(item)
                }

                val adapter = CustomAdapter(data)
                issues_recycler.adapter = adapter
                adapter.onItemClick={ issue->
                    val intent = Intent(this, IssueDetailsActivity::class.java)
                    intent.putExtra("issue_item",issue)
                    startActivity(intent)
                }


            } catch (e: JSONException) {
                Log.e("localGithubIssues", "" + e.message)
            }
        }
    }

    private fun callGithubIssues() {
        progress_bar.visibility= View.VISIBLE

        val url = "https://api.github.com/repos/square/okhttp/issues"
        /*val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            progress_bar.visibility= View.GONE
            try {
                //val jsonArray= response
               // for (i in 0 until jsonArray.length()) {
                   // val issue = jsonArray.getJSONObject(i)
                   *//* val title = issue.getString("title")
                    val description=issue.getString("body")
                    val updated_time=issue.getString("updated_at")

                    val user=issue.getJSONObject("user")
                    val uname=user.getString("login")
                    val uImage=user.getString("avatar_url")

                    val item=GithubViewModel(uImage,uname,title,description,updated_time)
                    data.add(item)*//*
               // }

                //val adapter=CustomAdapter(data)
                //issues_recycler.adapter=adapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, {
                error -> error.printStackTrace()
                progress_bar.visibility= View.GONE
        })*/

        val sr: StringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                //Log.e("GithubIssueAct",response)
                progress_bar.visibility= View.GONE
                try {
                    val jsonArray= JSONArray(response)

                     for (i in 0 until jsonArray.length()) {
                     val issue = jsonArray.getJSONObject(i)
                         val cmntUrl=issue.getString("comments_url")

                     val title = issue.getString("title")
                    val description=issue.getString("body")
                    val updated_time=issue.getString("updated_at")

                    val user=issue.getJSONObject("user")
                    val uname=user.getString("login")
                    val uImage=user.getString("avatar_url")

                    val item=GithubViewModel(uImage,uname,title,description,updated_time,cmntUrl)
                    data.add(item)
                     }

                    val adapter = CustomAdapter(data)
                    issues_recycler.adapter = adapter
                    adapter.onItemClick={ issue->
                        val intent = Intent(this, IssueDetailsActivity::class.java)
                        intent.putExtra("issue_item",issue)
                        startActivity(intent)
                    }

                    val sharedPreference =  getSharedPreferences("github_issues", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("issues",jsonArray.toString())
                    editor.apply()

                    //Log.e("githubissuesAct data op",data.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //your error
                progress_bar.visibility= View.GONE
                error.message?.let { Log.e("GithubAct", it) }
            }){}

        requestQueue?.add(sr)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}
