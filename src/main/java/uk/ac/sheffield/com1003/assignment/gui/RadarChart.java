package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.AbstractPlayerCatalog;
import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerEntry;
import uk.ac.sheffield.com1003.assignment.codeprovided.PlayerProperty;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractRadarChart;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.RadarAxisValues;

import java.util.*;

/**
 * Class responsible for providing functionality to create a radar chart.
 *
 * @version 1.0 01/04/2023
 *
 * @author Zsigmond Kovacs-Nagy
 */

public class RadarChart extends AbstractRadarChart
{
    /**
     * Constructor
     * @param playerCatalog the collection of the data used to populate the dashboard
     * @param filteredPlayerEntriesList the player entries being used to assist the radar chart panel.
     */
    public RadarChart(AbstractPlayerCatalog playerCatalog, List<PlayerEntry> filteredPlayerEntriesList,
                      List<PlayerProperty> playerRadarChartProperties)
    {
        super(playerCatalog, filteredPlayerEntriesList, playerRadarChartProperties);
    }

    @Override
    public void updateRadarChartContents(List<PlayerProperty> radarChartPlayerProperties,
                                         List<PlayerEntry> filteredPlayerEntriesList) {
        radarAxesValues.clear();
        playerRadarChartProperties = new ArrayList<PlayerProperty>();
        // Reset and update values
        for (PlayerProperty playerProperty : radarChartPlayerProperties) {
            playerRadarChartProperties.add(playerProperty);
            double min = playerCatalog.getMinimumValue(playerProperty, filteredPlayerEntriesList);
            double max = playerCatalog.getMaximumValue(playerProperty, filteredPlayerEntriesList);
            double average = playerCatalog.getMeanAverageValue(playerProperty, filteredPlayerEntriesList);
            RadarAxisValues radarAxisValue = new RadarAxisValues(min, max, average);
            radarAxesValues.put(playerProperty, radarAxisValue);
        }
    }

    @Override
    public List<PlayerProperty> getPlayerRadarChartProperties() {
        try {
            return playerRadarChartProperties;
        } catch (NoSuchElementException e) {
            System.err.println("Element being requested does not exist");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<PlayerProperty, RadarAxisValues> getRadarPlotAxesValues() {
        try {
            return radarAxesValues;
        } catch (NoSuchElementException e) {
            System.err.println("Element being requested does not exist");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AbstractPlayerCatalog getPlayerCatalog() { return playerCatalog; }

    @Override
    public List<PlayerEntry> getFilteredPlayerEntries() { return filteredPlayerEntries; }

}

