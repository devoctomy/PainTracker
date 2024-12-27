import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.paintracker.R
import com.example.paintracker.data.PainCategory

class PainCategoryAdapter(private val categories: List<PainCategory>) :
    RecyclerView.Adapter<PainCategoryAdapter.PainCategoryViewHolder>() {

    inner class PainCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val circleView: ImageView = view.findViewById(R.id.circle)

        fun bind(category: PainCategory) {
            circleView.setBackgroundColor(category.colour)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PainCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_circle, parent, false)
        return PainCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PainCategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
