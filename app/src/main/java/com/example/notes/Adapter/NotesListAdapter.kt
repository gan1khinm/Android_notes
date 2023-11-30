package com.example.notes.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.example.notes.Models.Notes
import com.example.notes.NotesClickListener
import com.example.notes.R

class NotesListAdapter(private val context: Context, private val list: List<Notes>, private val listener: NotesClickListener) :
    RecyclerView.Adapter<NotesViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.notes_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(@NonNull holder: NotesViewHolder, position: Int) {
        holder.textView_title.text = list[position].title
        holder.textView_title.isSelected = true

        holder.textView_notes.text = list[position].notes

        holder.textView_date.text = list[position].date
        holder.textView_date.isSelected = true
        holder.notes_conteiner.setCardBackgroundColor(holder.itemView.resources.getColor(R.color.orange, null))

        holder.notes_conteiner.setOnClickListener {
            listener.onClick(list[holder.adapterPosition])
        }

        holder.notes_conteiner.setOnLongClickListener {
            listener.onLongClick(list[holder.adapterPosition], holder.notes_conteiner)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val notes_conteiner: CardView = itemView.findViewById(R.id.notes_conteiner)
    val textView_title: TextView = itemView.findViewById(R.id.textView_title)
    val textView_notes: TextView = itemView.findViewById(R.id.textView_notes)
    val textView_date: TextView = itemView.findViewById(R.id.textView_date)
}

