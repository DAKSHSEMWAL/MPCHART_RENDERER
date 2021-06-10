import android.graphics.Typeface
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.research.health.core.Common.getWeekNumber
import kotlin.math.roundToInt


class MyXAxisFormatter : ValueFormatter() {
    private val days = arrayOf("M", "T", "W", "T", "F", "S", "S")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return days.getOrNull(value.toInt()) ?: value.toString()
    }
}