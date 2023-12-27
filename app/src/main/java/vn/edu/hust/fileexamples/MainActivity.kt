package vn.edu.hust.fileexamples

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.edu.hust.fileexamples.databinding.ActivityMainBinding
import java.io.File
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var fileAdapter: FileAdapter
    private lateinit var currentDirectory: File
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        registerForContextMenu(recyclerView)



        if (Build.VERSION.SDK_INT < 30) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.v("TAG", "Permission Denied")
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
            } else
                Log.v("TAG", "Permission Granted")
        } else {
            if (!Environment.isExternalStorageManager()) {
                Log.v("TAG", "Permission Denied")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            } else {
                Log.v("TAG", "Permission Granted")
            }
        }

        currentDirectory = Environment.getExternalStorageDirectory()
        setupRecyclerView()

    }


    private fun setupRecyclerView() {
        val root = Environment.getExternalStorageDirectory()
        val files = getFiles(root)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FileAdapter(files, object : FileAdapter.OnItemClickListener {
            override fun onItemClick(file: File) {
                if (file.isDirectory) {
                    displayFolderContents(file)
                } else {
                }
            }

            override fun onItemLongClick(item: File, view: View): Boolean {
                // Xử lý sự kiện nhấn giữ
                return true
            }
        })
    }

    private fun displayFolderContents(folder: File) {
        val subItems = getFiles(folder)
        (recyclerView.adapter as? FileAdapter)?.updateData(subItems)
    }

    private fun getFiles(directory: File): List<File> {
        val fileList = ArrayList<File>()
        val files = directory.listFiles()

        if (files != null) {
            fileList.addAll(files)
        }

        return fileList
    }

//    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        menuInflater.inflate(R.menu.context_menu, menu)
//    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menuInflater.inflate(R.menu.context_menu, menu)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val file = (recyclerView.adapter as FileAdapter).getItem(info.position)

        when (item.itemId) {
            R.id.context_menu_rename -> {
                // Xử lý đổi tên
                showRenameDialog(file)
                return true
            }
            R.id.context_menu_delete -> {
                // Xử lý xóa thư mục với xác nhận AlertDialog
                showDeleteConfirmationDialog(file)
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }


    private fun showRenameDialog(file: File) {
        val editText = EditText(this)
        editText.setText(file.name)

        AlertDialog.Builder(this)
            .setTitle("Rename File")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    fileAdapter.renameFile(this, file, newName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    fun renameFile(oldFile: File, newName: String) {
        val newFile = File(oldFile.parent, newName)
        if (oldFile.renameTo(newFile)) {
//            notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Failed to rename file", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteConfirmationDialog(file: File) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(getString(R.string.confirm_delete_message, file.name))
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                deleteFolder(file)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun deleteFolder(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteFolder(it) }
        }
        file.delete()
        (recyclerView.adapter as? FileAdapter)?.updateData(getFiles(currentDirectory))
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission Granted")
        } else {
            Log.v("TAG", "Permission Denied")
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new_folder -> {
                // Xử lý khi chọn "Tạo thư mục mới"
                createNewFolder()
                return true
            }
            R.id.action_new_text_file -> {
                // Xử lý khi chọn "Tạo file văn bản mới"
                createNewTextFile()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun createNewFolder() {

    }

    private fun createNewTextFile() {

    }

}