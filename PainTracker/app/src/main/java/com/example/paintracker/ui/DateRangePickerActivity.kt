import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DateRangePickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show the custom dialog
        showDateRangePickerDialog()
    }

    private fun showDateRangePickerDialog() {
        val calendar = Calendar.getInstance()

        var fromDate: String?
        var toDate: String?

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Date Range")
            .setMessage("Pick a From Date and To Date.")
            .setPositiveButton("OK") { _, _ ->
                // Handle OK
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            // From Date Picker
            val fromButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            fromButton.text = "From Date"
            fromButton.setOnClickListener {
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        fromDate = "$year-${month + 1}-$dayOfMonth"
                        fromButton.text = "From: $fromDate"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // To Date Picker
            val toButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            toButton.text = "To Date"
            toButton.setOnClickListener {
                DatePickerDialog(
                    this,
                    { _, year, month, dayOfMonth ->
                        toDate = "$year-${month + 1}-$dayOfMonth"
                        toButton.text = "To: $toDate"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        dialog.show()
    }
}