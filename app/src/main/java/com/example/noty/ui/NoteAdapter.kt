package com.example.noty.ui

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noty.data.Note
import com.example.noty.data.NoteType
import com.example.noty.databinding.ItemNoteBinding

class NoteAdapter(
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.textTitle.text = note.title
            val formattedTime = DATE_FORMAT.format(Date(note.timestamp))
            binding.textTimestamp.text = formattedTime

            if (!note.description.isNullOrEmpty()) {
                binding.textDescription.text = note.description
                binding.textDescription.visibility = android.view.View.VISIBLE
            } else {
                binding.textDescription.visibility = android.view.View.GONE
            }

            // Set content description for accessibility
            val contentDesc = "Note: ${note.title}. ${if (!note.description.isNullOrEmpty()) note.description + ". " else ""}Created $formattedTime"
            binding.root.contentDescription = contentDesc

            binding.buttonMore.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                val popup = android.widget.PopupMenu(it.context, it)
                popup.menuInflater.inflate(com.example.noty.R.menu.note_item_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        com.example.noty.R.id.action_edit -> {
                            it.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            onEditClick(note)
                            true
                        }
                        com.example.noty.R.id.action_delete -> {
                            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            onDeleteClick(note)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }

            binding.root.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                onEditClick(note)
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
