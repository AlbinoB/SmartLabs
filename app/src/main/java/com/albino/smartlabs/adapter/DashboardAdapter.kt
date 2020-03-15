package com.albino.smartlabs.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.albino.smartlabs.R
import com.albino.smartlabs.activity.*
import com.albino.smartlabs.model.Lab
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException



class DashboardAdapter (val contextParam :Context,val listOfLabs:ArrayList<Lab>): RecyclerView.Adapter<DashboardAdapter.ViewHolderDashboard>() {

    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {
        val labName: TextView = view.findViewById(R.id.labName)
        val labId: TextView = view.findViewById(R.id.labId)
        val textViewActiveSystems:TextView=view.findViewById(R.id.textViewActiveSystems)
        val progressBarActiveSystems:ProgressBar=view.findViewById(R.id.progressBarActiveSystems)
        val linearLayoutEachLab:LinearLayout=view.findViewById(R.id.linearLayoutEachLab)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_dashboard_single_row, parent, false)

        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return listOfLabs.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {

        val lab=listOfLabs.get(position)

        holder.labId.text=lab.labId
        holder.labName.text=lab.labName
        holder.textViewActiveSystems.text=lab.labCountActiveSystems+"/"+lab.labCountAllSystems
        holder.progressBarActiveSystems.progress=((lab.labCountActiveSystems.toFloat()/lab.labCountAllSystems.toFloat())*100).toInt()

        holder.linearLayoutEachLab.setOnClickListener(View.OnClickListener {
            val intent = Intent(contextParam, LabSystems::class.java)

            intent.putExtra("labId", lab.labId)

            intent.putExtra("labName", lab.labName)

            contextParam.startActivity(intent)


        })


    }

}