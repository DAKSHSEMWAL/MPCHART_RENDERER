package com.google.research.health.features.utils;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class MyCombinedChartRenderer extends CombinedChartRenderer {


    public MyCombinedChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
        mChart = chart;
        createRenderers();
    }

    /**
     * all rederers for the different kinds of data this combined-renderer can draw
     */
    private List<DataRenderer> mRenderers = new ArrayList<DataRenderer>(5);

    private CombinedChart mChart;

    /*public CombinedChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = new WeakReference<Chart>(chart);
        createRenderers();
    }*/

    /**
     * Creates the renderers needed for this combined-renderer in the required order. Also takes the DrawOrder into
     * consideration.
     */
    public void createRenderers() {

        if (mRenderers != null)
            mRenderers.clear();

        //CombinedChart chart = (CombinedChart)mChart.get();
        if (mChart == null)
            return;

        CombinedChart.DrawOrder[] orders = mChart.getDrawOrder();

        for (CombinedChart.DrawOrder order : orders) {

            switch (order) {
                case LINE:
                    if (mChart.getLineData() != null)
                        mRenderers.add(new MyLineChartRenderer(mChart, mAnimator, mViewPortHandler));
                    break;
                case CANDLE:
                    if (mChart.getCandleData() != null)
                        mRenderers.add(new MyCandleStickRenderer(mChart, mAnimator, mViewPortHandler));
                    break;
            }
        }
    }

    @Override
    public void initBuffers() {

        for (DataRenderer renderer : mRenderers)
            renderer.initBuffers();
    }

    @Override
    public void drawData(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawData(c);
    }

    @Override
    public void drawValues(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawValues(c);
    }

    @Override
    public void drawExtras(Canvas c) {

        for (DataRenderer renderer : mRenderers)
            renderer.drawExtras(c);
    }

    protected List<Highlight> mHighlightBuffer = new ArrayList<Highlight>();

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        //Chart chart = mChart.get();
        if (mChart == null && mRenderers.size() == 0) return;

        for (DataRenderer renderer : mRenderers) {
            ChartData data = null;

            if (renderer instanceof LineChartRenderer)
                data = ((MyLineChartRenderer) renderer).mChart.getLineData();
            else if (renderer instanceof CandleStickChartRenderer)
                data = ((MyCandleStickRenderer) renderer).mChart.getCandleData();

            int dataIndex = data == null ? -1
                    : (mChart.getData()).getAllData().indexOf(data);

            mHighlightBuffer.clear();

            for (Highlight h : indices) {
                if (h.getDataIndex() == dataIndex || h.getDataIndex() == -1)
                    mHighlightBuffer.add(h);
            }

            renderer.drawHighlighted(c, mHighlightBuffer.toArray(new Highlight[mHighlightBuffer.size()]));
        }
    }

    /**
     * Returns the sub-renderer object at the specified index.
     *
     * @param index
     * @return
     */
    public DataRenderer getSubRenderer(int index) {
        if (index >= mRenderers.size() || index < 0)
            return null;
        else
            return mRenderers.get(index);
    }

    /**
     * Returns all sub-renderers.
     *
     * @return
     */
    public List<DataRenderer> getSubRenderers() {
        return mRenderers;
    }

    public void setSubRenderers(List<DataRenderer> renderers) {
        this.mRenderers = renderers;
    }
}
