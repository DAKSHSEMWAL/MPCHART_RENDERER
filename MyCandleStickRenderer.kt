package com.google.research.health.features.utils

import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet
import com.github.mikephil.charting.renderer.CandleStickChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler

class MyCandleStickRenderer(
    chart: CandleDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) : CandleStickChartRenderer(chart, animator, viewPortHandler) {
    @JvmField
    var mChart: CandleDataProvider? = null

    //private ChartAnimator mChartAnimator;
    //private ViewPortHandler mViewPortHandler;
    private val mShadowBuffers = FloatArray(8)
    private val mBodyBuffers = FloatArray(4)
    private val mRangeBuffers = FloatArray(4)
    private val mOpenBuffers = FloatArray(4)
    private val mCloseBuffers = FloatArray(4)
    override fun drawDataSet(c: Canvas, dataSet: ICandleDataSet) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        val phaseY = mAnimator.phaseY
        val barSpace = dataSet.barSpace
        val showCandleBar = dataSet.showCandleBar
        if (dataSet.entryCount == 0) {
            return
        }
        mXBounds[mChart] = dataSet
        mRenderPaint.strokeWidth = dataSet.shadowWidth

        // draw the body
        for (j in mXBounds.min..mXBounds.range + mXBounds.min) {

            // get the entry
            val e = dataSet.getEntryForIndex(j) ?: continue
            val xPos = e.x
            val open = e.open
            val close = e.close
            val high = e.high
            val low = e.low
            if (showCandleBar) {
                // calculate the shadow
                mShadowBuffers[0] = xPos
                mShadowBuffers[2] = xPos
                mShadowBuffers[4] = xPos
                mShadowBuffers[6] = xPos
                if (open > close) {
                    mShadowBuffers[1] = high * phaseY
                    mShadowBuffers[3] = open * phaseY
                    mShadowBuffers[5] = low * phaseY
                    mShadowBuffers[7] = close * phaseY
                } else if (open < close) {
                    mShadowBuffers[1] = high * phaseY
                    mShadowBuffers[3] = close * phaseY
                    mShadowBuffers[5] = low * phaseY
                    mShadowBuffers[7] = open * phaseY
                } else {
                    mShadowBuffers[1] = high * phaseY
                    mShadowBuffers[3] = open * phaseY
                    mShadowBuffers[5] = low * phaseY
                    mShadowBuffers[7] = mShadowBuffers[3]
                }
                trans.pointValuesToPixel(mShadowBuffers)

                // draw the shadows
                if (dataSet.shadowColorSameAsCandle) {
                    if (open > close) mRenderPaint.color =
                        if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(j) else dataSet.decreasingColor else if (open < close) mRenderPaint.color =
                        if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(j) else dataSet.increasingColor else mRenderPaint.color =
                        if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(j) else dataSet.neutralColor
                } else {
                    mRenderPaint.color =
                        if (dataSet.shadowColor == ColorTemplate.COLOR_NONE) dataSet.getColor(j) else dataSet.shadowColor
                }
                mRenderPaint.style = Paint.Style.STROKE
                c.drawLines(mShadowBuffers, mRenderPaint)

                // calculate the body
                mBodyBuffers[0] = xPos - 0.5f + barSpace
                mBodyBuffers[1] = close * phaseY
                mBodyBuffers[2] = xPos + 0.5f - barSpace
                mBodyBuffers[3] = open * phaseY
                trans.pointValuesToPixel(mBodyBuffers)

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing
                    if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.decreasingColor
                    }
                    mRenderPaint.style = dataSet.decreasingPaintStyle
                    c.drawRoundRect(
                        mBodyBuffers[0], mBodyBuffers[3] - 15,
                        mBodyBuffers[2], mBodyBuffers[1] + 15, 40f, 40f,
                        mRenderPaint
                    )
                } else if (open < close) {
                    if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.increasingColor
                    }
                    mRenderPaint.style = dataSet.increasingPaintStyle
                    c.drawRoundRect(
                        mBodyBuffers[0], mBodyBuffers[1] - 15,
                        mBodyBuffers[2], mBodyBuffers[3] + 15, 40f, 40f,
                        mRenderPaint
                    )
                } else { // equal values
                    if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.color = dataSet.getColor(j)
                    } else {
                        mRenderPaint.color = dataSet.neutralColor
                    }
                    c.drawLine(
                        mBodyBuffers[0], mBodyBuffers[1],
                        mBodyBuffers[2], mBodyBuffers[3],
                        mRenderPaint
                    )
                }
            } else {
                mRangeBuffers[0] = xPos
                mRangeBuffers[1] = high * phaseY
                mRangeBuffers[2] = xPos
                mRangeBuffers[3] = low * phaseY
                mOpenBuffers[0] = xPos - 0.5f + barSpace
                mOpenBuffers[1] = open * phaseY
                mOpenBuffers[2] = xPos
                mOpenBuffers[3] = open * phaseY
                mCloseBuffers[0] = xPos + 0.5f - barSpace
                mCloseBuffers[1] = close * phaseY
                mCloseBuffers[2] = xPos
                mCloseBuffers[3] = close * phaseY
                trans.pointValuesToPixel(mRangeBuffers)
                trans.pointValuesToPixel(mOpenBuffers)
                trans.pointValuesToPixel(mCloseBuffers)

                // draw the ranges
                var barColor: Int =
                    if (open > close) if (dataSet.decreasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
                        j
                    ) else dataSet.decreasingColor else if (open < close) if (dataSet.increasingColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
                        j
                    ) else dataSet.increasingColor else if (dataSet.neutralColor == ColorTemplate.COLOR_NONE) dataSet.getColor(
                        j
                    ) else dataSet.neutralColor
                mRenderPaint.color = barColor
                c.drawLine(
                    mRangeBuffers[0], mRangeBuffers[1],
                    mRangeBuffers[2], mRangeBuffers[3],
                    mRenderPaint
                )
                c.drawLine(
                    mOpenBuffers[0], mOpenBuffers[1],
                    mOpenBuffers[2], mOpenBuffers[3],
                    mRenderPaint
                )
                c.drawLine(
                    mCloseBuffers[0], mCloseBuffers[1],
                    mCloseBuffers[2], mCloseBuffers[3],
                    mRenderPaint
                )
            }
        }
    }

    init {
        this.mChart = chart
        //this.mChartAnimator = animator;
        //this.mViewPortHandler = viewPortHandler;
    }
}