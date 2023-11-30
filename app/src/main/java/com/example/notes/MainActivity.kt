package com.example.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.Adapter.NotesListAdapter
import com.example.notes.DateBase.RoomDB
import com.example.notes.Models.Notes
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var fab_add: FloatingActionButton
    lateinit var garbage: FloatingActionButton
    lateinit var back: FloatingActionButton
    lateinit var notesListAdapter: NotesListAdapter
    lateinit var database: RoomDB
    var notes: MutableList<Notes> = ArrayList()
    lateinit var selectedNote: Notes

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_home)
        fab_add = findViewById(R.id.fab_add)
        garbage = findViewById(R.id.garbage)
        back = findViewById(R.id.back)
        database = RoomDB.getInstance(this)


        notes = database.mainDao().getAll().toMutableList()
        updateRecyclerNotes(notes);


        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NotesTakerActivity::class.java)
            startActivityForResult(intent, 101)
            garbage.visibility = View.VISIBLE
            back.visibility = View.INVISIBLE
        }
        garbage.setOnClickListener {
            val deletedNotes = database.mainDao().getDeletedNotes()
            notes.clear()
            notes.addAll(deletedNotes)
            notesListAdapter.notifyDataSetChanged()
            back.visibility = View.VISIBLE
            garbage.visibility = View.INVISIBLE
        }
        back.setOnClickListener{
            val allNotes = database.mainDao().getAll()
            notes.clear()
            notes.addAll(allNotes)
            notesListAdapter.notifyDataSetChanged()
            back.visibility = View.INVISIBLE
            garbage.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101){
            if (resultCode == Activity.RESULT_OK) {
                val newNotes = data?.getSerializableExtra("note") as Notes
                database.mainDao().insert(newNotes)
                notes.clear()
                notes.addAll(database.mainDao().getAll())
                notesListAdapter.notifyDataSetChanged()
            }
        }

        if (requestCode == 102){
            if (resultCode == Activity.RESULT_OK) {
                val newNotes = data?.getSerializableExtra("note") as Notes
                database.mainDao().update(newNotes.id, newNotes.title, newNotes.notes)
                notes.clear()
                notes.addAll(database.mainDao().getAll())
                notesListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateRecyclerNotes(notes: List<Notes>) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        notesListAdapter = NotesListAdapter(this@MainActivity, notes, notesClickListener)
        recyclerView.adapter = notesListAdapter
    }

    private val notesClickListener = object : NotesClickListener {
        override fun onClick(notes: Notes){
            val intent = Intent(this@MainActivity, NotesTakerActivity::class.java)
            intent.putExtra("old_note", notes)
            startActivityForResult(intent, 102)
        }

        override fun onLongClick(notes: Notes, cardView: CardView){
            selectedNote = Notes()
            selectedNote = notes
            showPopUp (cardView, notes.isDeleted)
        }
    }

    private fun showPopUp(cardView: CardView, isDeleted: Boolean) {
        val popupMenu: PopupMenu = PopupMenu(this, cardView)
        popupMenu.setOnMenuItemClickListener(this)
        if (!isDeleted){
            popupMenu.inflate(R.menu.popup_menu)
        } else {
            popupMenu.inflate(R.menu.popup_delete_menu)
        }

        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete -> {
                database.mainDao().deleteNote(selectedNote.id)
                notes.remove(selectedNote)
                notesListAdapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Note removed", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.delete_forever -> {
                database.mainDao().delete(selectedNote)
                notes.remove(selectedNote)
                notesListAdapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Note removed forever", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.restore -> {
                database.mainDao().restoreNote(selectedNote.id)
                notes.remove(selectedNote)
                notesListAdapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Note restored", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return false
        }
    }
}