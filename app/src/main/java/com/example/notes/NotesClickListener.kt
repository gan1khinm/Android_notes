package com.example.notes

import androidx.cardview.widget.CardView
import com.example.notes.Models.Notes

interface NotesClickListener {
    fun onClick(notes: Notes)
    fun onLongClick(notes: Notes, cardView: CardView)
}