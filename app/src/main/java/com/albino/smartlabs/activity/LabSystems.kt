package com.albino.smartlabs.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albino.smartlabs.R
import com.albino.smartlabs.adapter.LabSystemAdapter
import com.albino.smartlabs.model.LabSystem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


lateinit var recyclerViewAllSystems: RecyclerView
lateinit var labSystemLayoutManager: RecyclerView.LayoutManager
lateinit var labSystemAdapter:LabSystemAdapter
lateinit var labSystemToggleSwitch:Switch
lateinit var textViewLabName:TextView
lateinit var progressBarActiveSystems:ProgressBar
lateinit var textViewActiveSystems:TextView
lateinit var lab_systems_Progressdialog:RelativeLayout
lateinit var labId:String

class LabSystems : AppCompatActivity() {



    var listOfActiveSystems= arrayListOf<LabSystem>()
    var listOfInactiveSystems= arrayListOf<LabSystem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab_systems)

        labSystemToggleSwitch=findViewById(R.id.labSystemToggleSwitch)
        textViewLabName=findViewById(R.id.textViewLabName)
        progressBarActiveSystems=findViewById(R.id.progressBarActiveSystems)
        textViewActiveSystems=findViewById(R.id.textViewActiveSystems)
        lab_systems_Progressdialog=findViewById(R.id.lab_systems_Progressdialog)


        recyclerViewAllSystems =findViewById(
            R.id.recyclerViewAllSystems
        )

        textViewLabName.text=intent.getStringExtra("labName")
        labId=intent.getStringExtra("labId")


        labSystemToggleSwitch.setOnClickListener(View.OnClickListener {

            if (labSystemToggleSwitch.isChecked){

                listOfActiveSystems= labSystemAdapter.listOfActiveSystems
                listOfInactiveSystems= labSystemAdapter.listOfInactiveSystems

                labSystemAdapter= LabSystemAdapter(this,listOfActiveSystems,listOfActiveSystems,listOfInactiveSystems,progressBarActiveSystems,textViewActiveSystems)

                recyclerViewAllSystems.adapter= labSystemAdapter

            }else
            {

                listOfActiveSystems= labSystemAdapter.listOfActiveSystems
                listOfInactiveSystems= labSystemAdapter.listOfInactiveSystems
                labSystemAdapter= LabSystemAdapter(this,listOfInactiveSystems,listOfActiveSystems,listOfInactiveSystems,progressBarActiveSystems,textViewActiveSystems)

                recyclerViewAllSystems.adapter= labSystemAdapter
            }


        })



        labSystemLayoutManager= LinearLayoutManager(this)


        try {


            lab_systems_Progressdialog.visibility= View.VISIBLE
            val queue = Volley.newRequestQueue(this)

            val url = "http://"+resources.getString(R.string.ip_address)+"/hackathon/v1/fetch_all_systems_from_lab.php?labId="+labId

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
                            val labSystem = LabSystem(
                                restaurantJsonObject.getString("current_user_id"),
                                restaurantJsonObject.getString("current_user"),
                                restaurantJsonObject.getString("course_name"),
                                restaurantJsonObject.getString("computer_id"),
                                restaurantJsonObject.getString("computer_code"),
                                restaurantJsonObject.getString("system_status")

                            )

                            if(restaurantJsonObject.getString("system_status").toInt()==1){

                                listOfActiveSystems.add(labSystem)

                            }else
                            {
                                listOfInactiveSystems.add(labSystem)
                            }

                        }

                        labSystemAdapter= LabSystemAdapter(this,listOfActiveSystems,listOfActiveSystems,listOfInactiveSystems,progressBarActiveSystems,textViewActiveSystems)

                        recyclerViewAllSystems.adapter= labSystemAdapter

                        recyclerViewAllSystems.layoutManager= labSystemLayoutManager

                        lab_systems_Progressdialog.visibility= View.INVISIBLE
                    }

                    progressBarActiveSystems.progress=((listOfActiveSystems.size.toFloat()/(listOfInactiveSystems.size.toFloat()+listOfActiveSystems.size.toFloat()))*100).toInt()

                    textViewActiveSystems.text=listOfActiveSystems.size.toString()+"/"+(listOfInactiveSystems.size.toInt()+listOfActiveSystems.size.toInt())

                },
                Response.ErrorListener {

                    println("error12"+it)
                    Toast.makeText(
                        this,
                        "mSome Error occurred!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                    lab_systems_Progressdialog.visibility= View.INVISIBLE
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
            lab_systems_Progressdialog.visibility= View.INVISIBLE

        }


    }


}
