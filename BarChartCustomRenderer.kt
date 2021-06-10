
import android.graphics.Bitmap
import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil


open class BarChartCustomRenderer(
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?,
    private val barImage: Bitmap
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    override fun drawData(c: Canvas?) {
        super.drawData(c)
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        super.drawDataSet(c, dataSet, index)
        drawBarImages(c, dataSet, index)
    }

    private fun drawBarImages(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val buffer = mBarBuffers[index]
        var left: Float //avoid allocation inside loop
        var right: Float
        var top: Float
        var bottom: Float
        val scaledBarImage = scaleBarImage(buffer)
        val starWidth = scaledBarImage.width
        val starOffset = starWidth / 2
        var j = 0
        while (j < buffer.buffer.size * mAnimator.phaseX) {
            left = buffer.buffer[j]
            right = buffer.buffer[j + 2]
            top = buffer.buffer[j + 1]
            bottom = buffer.buffer[j + 3]
            val x = (left + right) / 2f
            if (!mViewPortHandler.isInBoundsRight(x)) break
            if (!mViewPortHandler.isInBoundsY(top)
                || !mViewPortHandler.isInBoundsLeft(x)
            ) {
                j += 4
                continue
            }
            val entry = dataSet.getEntryForIndex(j / 4)
            val `val` = entry.y
            if (`val` > 50) {
                drawImage(c, scaledBarImage, x - starOffset, top)
            }
            j += 4
        }
    }

    private fun scaleBarImage(buffer: BarBuffer): Bitmap {
        val firstLeft = buffer.buffer[0]
        val firstRight = buffer.buffer[2]
        val firstWidth = ceil((firstRight - firstLeft).toDouble()).toInt()
        return Bitmap.createScaledBitmap(barImage, firstWidth, firstWidth, false)
    }

    protected fun drawImage(c: Canvas, image: Bitmap?, x: Float, y: Float) {
        if (image != null) {
            c.drawBitmap(image, x, y, null)
        }
    }
}