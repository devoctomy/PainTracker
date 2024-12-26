import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paintracker.R
import com.example.paintracker.data.PainEntry

class PainEntryAdapter(
    private val painEntries: List<PainEntry>,
    private val onItemSelected: (PainEntry) -> Unit
) : RecyclerView.Adapter<PainEntryAdapter.PainEntryViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class PainEntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        private val painCategoriesTextView: TextView = view.findViewById(R.id.painCategoriesTextView)
        private val hasNotesTextView: TextView = view.findViewById(R.id.hasNotesTextView)

        fun bind(painEntry: PainEntry, isSelected: Boolean) {
            dateTextView.text = painEntry.date.toString()
            painCategoriesTextView.text = "Pain Categories: ${painEntry.painCategories.size}"
            hasNotesTextView.text = "Has Notes: ${if (painEntry.hasNotes) "Yes" else "No"}"

            itemView.setBackgroundColor(
                if (isSelected) itemView.context.getColor(com.google.android.material.R.color.design_default_color_primary)
                else itemView.context.getColor(android.R.color.transparent)
            )

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemSelected(painEntry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_pain_entry, parent, false)
        return PainEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PainEntryViewHolder, position: Int) {
        holder.bind(painEntries[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = painEntries.size
}

