package com.albino.smartlabs.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albino.smartlabs.R
import com.albino.smartlabs.adapter.DashboardAdapter
import com.albino.smartlabs.model.Lab
import com.albino.smartlabs.network.NetworkConfig
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


lateinit var recyclerViewAllLabs:RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var dasboard_Progressdialog:RelativeLayout
lateinit var dashboardAdapter:DashboardAdapter
lateinit var buttonStudentMode:Button
var listOfLabs= arrayListOf<Lab>()


class DashBoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        buttonStudentMode=findViewById(R.id.buttonStudentMode)
        recyclerViewAllLabs =findViewById(R.id.recyclerViewAllLabs)
        dasboard_Progressdialog=findViewById(R.id.dasboard_Progressdialog)

        buttonStudentMode.setOnClickListener(View.OnClickListener {

            val intent = Intent(this, AllocateSystemActivity::class.java)

            startActivity(intent)
        })

        layoutManager= LinearLayoutManager(this)


    }

    override fun onResume() {
        fetchData()
        super.onResume()
    }


    fun fetchData(){

        listOfLabs.clear()
        try {

            dasboard_Progressdialog.visibility= View.VISIBLE

            val queue = Volley.newRequestQueue(this)

            val url = "http://"+resources.getString(R.string.ip_address)+"/hackathon/v1/fetch_all_labs_for_lab_assistant.php?labAssistantId=1012180001"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {

                    val responseJsonObjectData = it.getJSONObject("data")

                    val success = responseJsonObjectData.getBoolean("success")

                    if (success) {

                        println("Response12 is " + it)

                        val data = responseJsonObjectData.getJSONArray("data")

                        for (i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val labObject = Lab(
                                restaurantJsonObject.getString("lab_id"),
                                restaurantJsonObject.getString("lab_name"),
                                restaurantJsonObject.getString("lab_count_active_systems"),
                                restaurantJsonObject.getString("lab_count_all_systems")

                            )
                            listOfLabs.add(labObject)

                        }

                        dashboardAdapter= DashboardAdapter(this,listOfLabs)

                        recyclerViewAllLabs.adapter= dashboardAdapter

                        recyclerViewAllLabs.layoutManager= layoutManager



                        dasboard_Progressdialog.visibility=View.INVISIBLE
                    }
                },
                Response.ErrorListener {

                    println("error12"+it)
                    Toast.makeText(
                        this,
                        "mSome Error occurred!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = "acdc385cfd7264"

                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } catch (e: JSONException) {
            Toast.makeText(
                this,
                "Some unexpected error occured!!!",
                Toast.LENGTH_SHORT
            ).show()

        }


    }
}
