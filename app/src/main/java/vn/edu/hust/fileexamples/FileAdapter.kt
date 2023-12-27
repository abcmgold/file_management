package vn.edu.hust.fileexamples

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(private var items: List<File>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: File)
        fun onItemLongClick(item: File, view: View): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener { onItemClickListener.onItemClick(item) }
        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemLongClick(item, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): File {
        return items[position]
    }

    fun updateData(newItems: List<File>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun renameFile(context: Context, oldFile: File, newName: String) {
        val newFile = File(oldFile.parent, newName)
        if (oldFile.renameTo(newFile)) {
            notifyDataSetChanged()
        } else {
            Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val textFileName: TextView = itemView.findViewById(R.id.textFileName)

        fun bind(item: File) {
            textFileName.text = item.name
            if (item.isDirectory) {
                icon.setImageResource(R.drawable.baseline_folder_24)
            } else {
                icon.setImageResource(R.drawable.baseline_folder_24)
            }
        }
    }
}
