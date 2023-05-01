package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChart;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChartPanel;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractPlayerDashboardPanel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

/**
 * SKELETON IMPLEMENTATION
 */

public class RadarChartPanel extends AbstractRadarChartPanel
{
    public RadarChartPanel(AbstractPlayerDashboardPanel parentPanel, AbstractRadarChart radarPlot) {
        super(parentPanel, radarPlot);
    }

    public Shape[] setBackgroundShapes (int centerXAxis, int centerYAxis) {
        Shape[] backgroundShape = new Shape[10];
        for (int i = 1; i < 11; i++) {
            int j = i * 3;
            int[] xAxes = {centerXAxis - 10*j, centerXAxis, centerXAxis + 10*j, centerXAxis + 5*j, centerXAxis - 5*j};
            int[] yAxes = {centerYAxis, centerYAxis - 9*j, centerYAxis, centerYAxis + 9*j, centerYAxis + 9*j};
            backgroundShape[i - 1] = new Polygon(xAxes, yAxes, 5);
        }
        return backgroundShape;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Set up scaling info
        Dimension panel = getSize();
        int centerXAxis = panel.width/2;
        int centerYAxis = panel.height/2;

        // Draw background
        Shape[] backgroundShapes = setBackgroundShapes(centerXAxis, centerYAxis);
        for (Shape backgroundShape : backgroundShapes) {
            g2.draw(backgroundShape);
        }
//        radarPlot
//        RadarChart.getPlayerRadarChartProperties
//        // Add stat labels
//        for (playerRadarChartProperties)
//        g2.drawString("test", centerXAxis, centerYAxis);
    }



}

