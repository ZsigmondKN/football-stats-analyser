package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.League;
import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerProperty;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChart;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChartPanel;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractPlayerDashboardPanel;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.RadarAxisValues;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for drawing all the visible elements of the radar chart displayed on the GUI.
 *
 * @version 1.0 01/04/2023
 *
 * @author Zsigmond Kovacs-Nagy
 */

public class RadarChartPanel extends AbstractRadarChartPanel
{

    public RadarChartPanel(AbstractPlayerDashboardPanel parentPanel, AbstractRadarChart radarPlot) {
        super(parentPanel, radarPlot);
    }

    /**
     * Get the location of each point for positions. length amount of pentagons with all different sizes and shapes
     * depending on the position
     * @param positions the amount each corner is away from the centre
     * @param centerXAxis the distance to the centre of the panel on the x-axis
     * @param centerYAxis the distance to the centre of the panel on the y-axis
     */
    public int[][][] plotAxes(double[][] positions, int centerXAxis, int centerYAxis) {
        int[][][] coordinates = new int[positions.length][2][5];

        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < 5; j++) {
                double p = positions[i][j] * 3;
                int[] xAxes = {(int)(centerXAxis - 8 * p), centerXAxis, (int)(centerXAxis + 8 * p),
                        (int)(centerXAxis + 5 * p), (int)(centerXAxis - 5 * p)};
                int[] yAxes = {centerYAxis, (int)(centerYAxis - 8 * p), centerYAxis, (int)(centerYAxis + 8 * p),
                        (int)(centerYAxis + 8 * p)};
                coordinates[i][0][j] = xAxes[j];
                coordinates[i][1][j] = yAxes[j];
            }
        }

        return coordinates;
    }

    /**
     * Creates ten pentagons shapes for the background of the chart
     *
     * @param centerXAxis the distance to the centre of the panel on the x-axis
     * @param centerYAxis the distance to the centre of the panel on the y-axis
     */
    public Shape[] setBackgroundShapes (int centerXAxis, int centerYAxis) {
        // Set up coordinates
        Shape[] backgroundShape = new Shape[10];
        double[][] positions = new double[10][5];
        for (int i = 1; i < 11; i++){
            for (int j = 0; j < 5; j++){
                positions[i-1][j] = i;
            }
        }
        // Creat 10 pentagons with increasing size
        int i = 0;
        int[][][] coordinates = plotAxes(positions,centerXAxis, centerYAxis);
        for (int[][] coordinate : coordinates) {
            backgroundShape[i] = new Polygon(coordinate[0], coordinate[1], 5);
            i++;
        }

        return backgroundShape;
    }

    /**
     * Sets the labels used on the diagram, depending on the users selection
     *
     * @param playerProperty the Player properties being used to for the labels
     * @param centerXAxis the distance to the centre of the panel on the x-axis
     * @param centerYAxis the distance to the centre of the panel on the y-axis
     */
    public Map<String, int[]> setPropertyLabelsCoordinates
            (List<PlayerProperty> playerProperty, int centerXAxis, int centerYAxis)
    {
        Map<String, int[]> coordinates = new HashMap<>();
        // Define all five coordinates
        int[][] axes = {{centerXAxis - 370, centerYAxis -90},{centerXAxis - 60, centerYAxis - 260},
                     {centerXAxis + 160, centerYAxis - 90},{centerXAxis + 60, centerYAxis + 265},
                     {centerXAxis - 300, centerYAxis + 265}};
        // Add coordinates to map
        for (int i = 0; i < playerProperty.size(); i++) {
            coordinates.put(playerProperty.get(i).getName(), axes[i]);
        }
        return coordinates;
    }

    /**
     * Creates Creates the array of shapes used to plot on the chart.
     *
     * @param propertiesToDisplay the properties which correlate to the graph
     * @param centerXAxis the distance to the centre of the panel on the x-axis
     * @param centerYAxis the distance to the centre of the panel on the y-axis
     */
    public Shape[] setCharts (List<PlayerProperty> propertiesToDisplay, int centerXAxis, int centerYAxis) {
        AbstractRadarChart radarChart = getRadarChart();
        Map<PlayerProperty, RadarAxisValues> radarPlotAxesValues = radarChart.getRadarPlotAxesValues();

        Map<PlayerProperty, Double> highestValues = new HashMap<>();
        for (PlayerProperty property : propertiesToDisplay) {
            double highestValueOfTheProperty = getRadarChart().getPlayerCatalog().getMaximumValue(property, League.ALL);
            highestValues.put(property, highestValueOfTheProperty);
        }

        double[][] positions = new double[3][5];
        Shape[] Shapes = new Shape[3];

        for (int i = 0; i < propertiesToDisplay.size(); i++) {
            PlayerProperty property = propertiesToDisplay.get(i);
            double rationOfMaxToLargestValue =
                     radarPlotAxesValues.get(property).getMax() / highestValues.get(property);
            double rationOfAverageToLargestValue =
                    radarPlotAxesValues.get(property).getAverage() / highestValues.get(property);
            double rationOfMinToLargestValue =
                    radarPlotAxesValues.get(property).getMin() / highestValues.get(property);
            positions[0][i] = rationOfMaxToLargestValue * 10;
            positions[1][i] = rationOfAverageToLargestValue * 10;
            positions[2][i] = rationOfMinToLargestValue * 10;
        }

        int[][][] charts = plotAxes(positions, centerXAxis, centerYAxis);
        int i = 0;
        for (int[][] chart : charts) {
            Shapes[i] = new Polygon(chart[0], chart[1], 5);
            i++;
        }
        return Shapes;
    }

    /**
     * The method responsible for actually drawing each element of the radar chart
     *
     * @param g the graphics being used to create the chart
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Setup of variables
        Graphics2D g2 = (Graphics2D) g;
        AbstractRadarChart radarChart = getRadarChart();
        List<PlayerProperty> propertiesToDisplay = radarChart.getPlayerRadarChartProperties();

        // Setup for scaling info
        Dimension panel = getSize();
        int centerXAxis = panel.width / 2;
        int centerYAxis = panel.height / 2;

        // Draw background
        Shape[] backgroundShapes = setBackgroundShapes(centerXAxis, centerYAxis);
        for (Shape backgroundShape : backgroundShapes) {
            g2.draw(backgroundShape);
        }
        // Add stat labels
        g2.setFont(new Font("default", Font.BOLD, 13));
        Map<String, int[]> coordinates = setPropertyLabelsCoordinates(propertiesToDisplay, centerXAxis, centerYAxis);
        for (PlayerProperty playerProperty : propertiesToDisplay) {
            String propertyName = playerProperty.getName();
            g2.drawString(propertyName, coordinates.get(propertyName)[0], coordinates.get(propertyName)[1]);
        }

        // Draw graphs
        Shape[] charts = setCharts(propertiesToDisplay, centerXAxis, centerYAxis);

        if (getParentPanel().isMaxCheckBoxSelected()) {
            g2.setColor(Color.red);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.fill(charts[0]);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.draw(charts[0]);
        }
        if (getParentPanel().isAverageCheckBoxSelected()) {
            g2.setColor(Color.blue);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.fill(charts[1]);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.draw(charts[1]);
        }
        if (getParentPanel().isMinCheckBoxSelected()) {
            g2.setColor(Color.green);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.fill(charts[2]);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.draw(charts[2]);
        }
    }
}

