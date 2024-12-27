import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paintracker.R
import com.example.paintracker.data.PainEntry
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PainEntryAdapter(
    private val painEntries: List<PainEntry>,
    private val onItemSelected: (PainEntry) -> Unit
) : RecyclerView.Adapter<PainEntryAdapter.PainEntryViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class PainEntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        private val painCategoriesRecyclerView: RecyclerView = view.findViewById(R.id.painCategoriesRecyclerView)
        private val hasNotesTextView: TextView = view.findViewById(R.id.hasNotesTextView)

        fun bind(painEntry: PainEntry, isSelected: Boolean) {
            dateTextView.text = formatPainEntryDate(painEntry.date)
            hasNotesTextView.text = "Has Notes: ${if (painEntry.hasNotes) "Yes" else "No"}"

            painCategoriesRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            painCategoriesRecyclerView.adapter = PainCategoryAdapter(painEntry.painCategories)

            val materialCardView = itemView as MaterialCardView
            val typedValue = TypedValue()
            val theme = itemView.context.theme

            val backgroundColor = if (isSelected) {
                theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
                typedValue.data
            } else {
                theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
                typedValue.data
            }
            materialCardView.setCardBackgroundColor(backgroundColor)

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemSelected(painEntry)
            }
        }
    }

    fun selectItem(position: Int) {
        if (position in painEntries.indices) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainEntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_pain_entry, parent, false)
        return PainEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PainEntryViewHolder, position: Int) {
        holder.bind(painEntries[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = painEntries.size

    private fun formatPainEntryDate(date: LocalDate): String {
        val day = date.dayOfMonth
        val ordinal = getOrdinal(day)
        val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        return "$day$ordinal $monthYear"
    }

    private fun getOrdinal(day: Int): String {
        return when {
            day % 100 in 11..13 -> "th" // Special case for 11th, 12th, 13th
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}

