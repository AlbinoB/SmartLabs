package com.albino.smartlabs.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.albino.smartlabs.R
import com.albino.smartlabs.model.LabSystem
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class LabSystemAdapter (val contextParam : Context, var listOfSystems:ArrayList<LabSystem>,var listOfActiveSystems:ArrayList<LabSystem>,var listOfInactiveSystems:ArrayList<LabSystem>,val progressBarActiveSystems:ProgressBar,var textViewActiveSystems:TextView): RecyclerView.Adapter<LabSystemAdapter.ViewHolderLabSystems>() {

    class ViewHolderLabSystems(view: View) : RecyclerView.ViewHolder(view) {
        val computerCode: TextView = view.findViewById(R.id.computerCode)
        val currentUser: TextView = view.findViewById(R.id.currentUser)
        val currentUserCourse:TextView=view.findViewById(R.id.currentUserCourse)
        val toggleSwitch:Switch=view.findViewById(R.id.toggleSwitch)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLabSystems {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_all_systems_single_row, parent, false)

        progressBarActiveSystems.progress=((listOfActiveSystems.size.toFloat()/(listOfInactiveSystems.size.toFloat()+listOfActiveSystems.size.toFloat()))*100).toInt()

        textViewActiveSystems.text=listOfActiveSystems.size.toString()+"/"+(listOfInactiveSystems.size.toInt()+listOfActiveSystems.size.toInt())


        return ViewHolderLabSystems(view)
    }

    override fun getItemCount(): Int {
        return listOfSystems.size
    }

    override fun onBindViewHolder(holder: ViewHolderLabSystems, position: Int) {

        val system=listOfSystems.get(position)

        holder.computerCode.text=system.computerCode
        holder.currentUser.text=system.currentUser
        holder.currentUserCourse.text=system.currentUserCourse

        holder.toggleSwitch.isChecked = system.systemStatus.toInt()==1

        holder.toggleSwitch.setOnClickListener {

            if(!holder.toggleSwitch.isChecked){
                val alterDialog=androidx.appcompat.app.AlertDialog.Builder(contextParam)
                alterDialog.setTitle("Confirmation")
                alterDialog.setMessage("Do you want to shut down "+holder.computerCode.text.toString()+"?")
                alterDialog.setPositiveButton("Shut Down"){text,listener->
                    system.currentUser="Not in use"
                    system.currentUserCourse="---"
                    system.systemStatus="0"
                    listOfInactiveSystems.add(system)
                    listOfActiveSystems.remove(system)
                    listOfSystems.remove(system)
                    notifyDataSetChanged()
                    progressBarActiveSystems.progress=((listOfActiveSystems.size.toFloat()/(listOfInactiveSystems.size.toFloat()+listOfActiveSystems.size.toFloat()))*100).toInt()

                    textViewActiveSystems.text=listOfActiveSystems.size.toString()+"/"+(listOfInactiveSystems.size.toInt()+listOfActiveSystems.size.toInt())

                    if (system.currentUserId.contains("1012180001")){
                        turnOffSystemLabAssistant(system.currentUserId,system.computerId)
                    }else{
                        turnOffSystemStudent(system.currentUserId)
                    }
                }

                alterDialog.setNegativeButton("No"){ text,listener->
                    holder.toggleSwitch.isChecked = !holder.toggleSwitch.isChecked

                }
                alterDialog.setCancelable(false)
                alterDialog.create()
                alterDialog.show()
            }else
            {
                val alterDialog=androidx.appcompat.app.AlertDialog.Builder(contextParam)
                alterDialog.setTitle("Confirmation")
                alterDialog.setMessage("Do you want to turn on "+holder.computerCode.text.toString()+"?")
                alterDialog.setPositiveButton("Turn on"){text,listener->
                    system.currentUser="Lab Asst Name"
                    system.currentUserId="1012180001"
                    system.currentUserCourse="Lab dept"
                    system.systemStatus="1"
                    listOfActiveSystems.add(system)
                    listOfInactiveSystems.remove(system)
                    listOfSystems.remove(system)
                    notifyDataSetChanged()

                    turnOnSystemLabAssistant("1012180001",system.computerId)

                    progressBarActiveSystems.progress=((listOfActiveSystems.size.toFloat()/(listOfInactiveSystems.size.toFloat()+listOfActiveSystems.size.toFloat()))*100).toInt()

                    textViewActiveSystems.text=listOfActiveSystems.size.toString()+"/"+(listOfInactiveSystems.size.toInt()+listOfActiveSystems.size.toInt())

                    Toast.makeText(contextParam,"Turning on "+system.computerCode,Toast.LENGTH_SHORT).show()
                }

                alterDialog.setNegativeButton("No"){ text,listener->
                    holder.toggleSwitch.isChecked = !holder.toggleSwitch.isChecked

                }
                alterDialog.setCancelable(false)
                alterDialog.create()
                alterDialog.show()

            }


        }




    }

    fun returnListOfActiveSystems():ArrayList<LabSystem>{
        return listOfActiveSystems
    }

    fun returnListOfInactiveSystems():ArrayList<LabSystem>{
        return listOfInactiveSystems
    }


    fun turnOffSystemStudent(currentUserId:String){

        try {


            val queue = Volley.newRequestQueue(contextParam)


            val url = "http://"+contextParam.resources.getString(R.string.ip_address)+"/hackathon/v1/allocate_system_to_user.php?userId="+currentUserId

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

                },
                Response.ErrorListener {

                    println("error12"+it)
                    Toast.makeText(
                        contextParam,
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
                contextParam,
                "Some unexpected error occured!!!",
                Toast.LENGTH_SHORT
            ).show()

        }



    }





    fun turnOffSystemLabAssistant(currentUserId:String,computerId:String){

        try {


            val queue = Volley.newRequestQueue(contextParam)


            val url = "http://"+contextParam.resources.getString(R.string.ip_address)+"/hackathon/v1/deallocate_system_to_lab_assistant.php?labAssistantId="+currentUserId+"&computerId="+computerId

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

                },
                Response.ErrorListener {

                    println("error12"+it)
                    Toast.makeText(
                        contextParam,
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
                contextParam,
                "Some unexpected error occured!!!",
                Toast.LENGTH_SHORT
            ).show()

        }



    }







    fun turnOnSystemLabAssistant(currentUserId:String,computerId: String){

        try {


            val queue = Volley.newRequestQueue(contextParam)


            val url = "http://"+contextParam.resources.getString(R.string.ip_address)+"/hackathon/v1/allocate_system_to_lab_assistant.php?labAssistantId="+currentUserId+"&computerId="+computerId

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

                },
                Response.ErrorListener {

                    println("error12"+it)
                    Toast.makeText(
                        contextParam,
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
                contextParam,
                "Some unexpected error occured!!!",
                Toast.LENGTH_SHORT
            ).show()

        }

        


    }

}