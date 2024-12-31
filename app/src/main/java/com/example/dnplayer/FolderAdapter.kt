package com.example.dnplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FolderAdapter(
    private val folderPaths: List<String>,  // List of full folder paths
    private val folderClickListener: FolderClickListener  // Listener for folder clicks
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    interface FolderClickListener {
        fun onFolderClick(folderPath: String)  // Callback with full folder path
    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderIcon: ImageView = itemView.findViewById(R.id.folderIcon)
        val folderName: TextView = itemView.findViewById(R.id.folderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val fullPath = folderPaths[position]

        // Extract the last folder name from the path
        val folderName = File(fullPath).name

        holder.folderName.text = folderName
        holder.folderIcon.setImageResource(R.drawable.folder) // Optional: Set default icon

        holder.itemView.setOnClickListener {
            folderClickListener.onFolderClick(fullPath)  // Pass the full path on click
        }
    }

    override fun getItemCount(): Int = folderPaths.size
}
