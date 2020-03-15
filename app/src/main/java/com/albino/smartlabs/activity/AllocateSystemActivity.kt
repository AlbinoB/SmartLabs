package com.albino.smartlabs.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.albino.smartlabs.R
import com.albino.smartlabs.adapter.DashboardAdapter
import com.albino.smartlabs.model.Lab
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


lateinit var erpNo: EditText
lateinit var contextParam:Context
class AllocateSystemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allocate_system)

        erpNo=findViewById(R.id.erpNo)

        contextParam=this


        erpNo.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (erpNo.text.isNotBlank() && erpNo.text.length==10){
                    try {


                        val queue = Volley.newRequestQueue(contextParam)

                        println(erpNo.text.toString()+"sssss")

                        val url = "http://192.168.0.15/hackathon/v1/allocate_system_to_user.php?userId="+erpNo.text

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Request.Method.GET,
                            url,
                            null,
                            Response.Listener {


                                val success = it.getBoolean("success")

                                if (success) {

                                    println("Response12 is " + it)

                                    Toast.makeText(contextParam,it.getString("message").toString(),Toast.LENGTH_LONG).show()
                                }

                                erpNo.text.clear()
                            },
                            Response.ErrorListener {

                                println("error12"+it)
                                Toast.makeText(
                                    contextParam,
                                    "mSome Error occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                erpNo.text.clear()

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
                            contextParam,
                            "Some unexpected error occured!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    
                }

            }

        })




    }
}
