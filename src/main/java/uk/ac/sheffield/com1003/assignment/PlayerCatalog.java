package uk.ac.sheffield.com1003.assignment;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;

import java.util.*;

/**
 * Class to parse Queries.
 * Provides method to create a list of queries.
 *
 * @version 1.0 21/04/2023
 *
 * @author Zsigmond Kovacs-Nagy
 */
public class PlayerCatalog extends AbstractPlayerCatalog
{
    /**
     * Constructor.
     *
     * @param eplFilename file mane of the epl dataset
     * @param ligaFilename file mane of the liga dataset
     */
    public PlayerCatalog(String eplFilename, String ligaFilename) {
        super(eplFilename, ligaFilename);
    }

    /**
     * Method to assign the player details and properties to a player property map
     *
     * @param playerDetails array of details used in the map
     * @param playerProperties array of properties used in the map
     */
    public PlayerPropertyMap pupulatePlayerPropertyMap(String[] playerDetails, double [] playerProperties)
    {
        // Add the properties and details to the playerPropertyMap
        PlayerPropertyMap playerPropertyMap = new PlayerPropertyMap();
        try {
            int detailCount = 0;
            for (PlayerDetail detail : PlayerDetail.values()) {
                playerPropertyMap.putDetail(detail, playerDetails[detailCount]);
                detailCount ++;
            }
            int propertyCount = 0;
            for (PlayerProperty property : PlayerProperty.values()) {
                playerPropertyMap.put(property, playerProperties[propertyCount]);
                propertyCount ++;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument during player property map population");
            e.printStackTrace();
        }
        return playerPropertyMap;
    }

    @Override
    public PlayerPropertyMap parsePlayerEntryLine(String line) throws IllegalArgumentException
    {
        final int NUM_PLAYER_DETAILS = 4;
        final int NUM_PLAYER_PROPERTIES = 25;

        // Create arrays used to populate the playerPropertyMap
        String[] playerInfo = line.split(",");
        String[] playerDetails = new String[NUM_PLAYER_DETAILS];
        String[] temporaryArray = new String[NUM_PLAYER_PROPERTIES];

        // Populate the arrays with the corresponding values
        System.arraycopy(playerInfo, 0, playerDetails, 0, NUM_PLAYER_DETAILS);
        System.arraycopy(playerInfo, NUM_PLAYER_DETAILS, temporaryArray, 0, NUM_PLAYER_PROPERTIES);
        double[] playerProperties = new double[NUM_PLAYER_PROPERTIES];
        for (int i = 0; i < NUM_PLAYER_PROPERTIES; i++) {
            try {
                playerProperties[i] = Double.parseDouble(temporaryArray[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Player properties provided out of bounds.");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Player property conversion incompatible.");
                e.printStackTrace();
            }
        }

        return pupulatePlayerPropertyMap(playerDetails, playerProperties);
    }

    @Override
    public void updatePlayerCatalog() {
        List<PlayerEntry> allPlayerEntries = new ArrayList<>();
        allPlayerEntries.addAll(this.playerEntriesMap.get(League.EPL));
        allPlayerEntries.addAll(this.playerEntriesMap.get(League.LIGA));
        // Add the newly created league to the playerEntriesMap
        playerEntriesMap.put(League.ALL, allPlayerEntries);
    }

    @Override
    public double getMinimumValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList) {
        double minValue = Double.MAX_VALUE;
        try {
            // find min value
            for (PlayerEntry player : playerEntryList) {
                double propertyValue = player.getProperty(playerProperty);
                if (minValue > propertyValue)
                    minValue = propertyValue;
            }
        } catch (NoSuchElementException e) {
            System.err.println("Property being requested does not exist.");
            e.printStackTrace();
        }
        return minValue;
    }

    @Override
    public double getMaximumValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList) {
        double maxValue = -1;
        try {
            // find max value
            for (PlayerEntry player : playerEntryList) {
                double propertyValue = player.getProperty(playerProperty);
                if (maxValue < propertyValue)
                    maxValue = propertyValue;
            }
        } catch (NoSuchElementException e) {
            System.err.println("Property being requested does not exist.");
            e.printStackTrace();
        }
        return maxValue;
    }

    @Override
    public double getMeanAverageValue(PlayerProperty playerProperty, List<PlayerEntry> playerEntryList) {
        int numberOfValues = playerEntryList.size();
        double totalValue = 0;
        try {
            // add all values together
            for (PlayerEntry player : playerEntryList)
                totalValue += player.getProperty(playerProperty);
        } catch (NoSuchElementException e) {
            System.err.println("Property being requested does not exist.");
            e.printStackTrace();
        }
        // return mean value
        return totalValue / numberOfValues;
    }

    @Override
    public List<PlayerEntry> getFirstFivePlayerEntries(League type)
    {
        List<PlayerEntry> wholeLeague = getPlayerEntriesList(type);
        return wholeLeague.subList(0,5);
    }

}
