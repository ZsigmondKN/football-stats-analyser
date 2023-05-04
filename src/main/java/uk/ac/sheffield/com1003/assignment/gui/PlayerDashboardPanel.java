package uk.ac.sheffield.com1003.assignment.gui;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;
import uk.ac.sheffield.com1003.assignment.codeprovided.gui.AbstractPlayerDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.ac.sheffield.com1003.assignment.codeprovided.PlayerProperty.fromPropertyName;

/**
 * This class provides implementation for most of the GUI elements extended from the
 * AbstractPlayerDashboardPanel class. As a result this class is responsible for search, filtering and
 * displaying text based information.
 *
 * @version 1.0 27/04/2023
 *
 * @author Zsigmond Kovacs-Nagy
 */
public class PlayerDashboardPanel extends AbstractPlayerDashboardPanel
{

    /**
     * Constructor
     *
     * @param playerCatalog the collection of the data used to populate the dashboard
     */
    public PlayerDashboardPanel(AbstractPlayerCatalog playerCatalog) {
        super(playerCatalog);
    }

    public void populateNationComboBox() {
        DefaultComboBoxModel<String> nationBoxModel = (DefaultComboBoxModel<String>) comboNations.getModel();

        nationList.add(0,"");
        for (PlayerEntry player : playerCatalog.getPlayerEntriesList(League.ALL)) {
            if (!nationList.contains(player.getNation()))
                nationList.add(player.getNation());
        }
        nationBoxModel.addAll(nationList);
    }

    public void populatePositionComboBox() {
        DefaultComboBoxModel<String> positionBoxModel = (DefaultComboBoxModel<String>) comboPositions.getModel();

        positionList.add(0,"");
        for (PlayerEntry player : playerCatalog.getPlayerEntriesList(League.ALL)) {
            if (!positionList.contains(player.getPosition()))
                positionList.add(player.getPosition());
        }
        positionBoxModel.addAll(positionList);
    }

    public void populateTeamComboBox() {
        DefaultComboBoxModel<String> teamBoxModel = (DefaultComboBoxModel<String>) comboTeams.getModel();

        teamList.add(0,"");
        for (PlayerEntry player : playerCatalog.getPlayerEntriesList(League.ALL)) {
            if (!teamList.contains(player.getTeam()))
                teamList.add(player.getTeam());
        }
        teamBoxModel.addAll(teamList);
    }

    public void resetFilteredPlayerEntriesList() {
        filteredPlayerEntriesList = new ArrayList<>(playerCatalog.getPlayerEntriesList(League.ALL));
    }

    public void filterByLeagueType() {
        if (selectedLeagueType == League.ALL)
            return;
        // Remove from list if player not in selected league
        filteredPlayerEntriesList.removeIf(playerEntry -> playerEntry.getLeagueType() != selectedLeagueType);
    }

    public void filterByNation() {
        if (selectedPlayerNation == null || selectedPlayerNation.isEmpty())
            return;
        // Remove from list if player not from selected nation
        filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getNation().equals(selectedPlayerNation));
    }

    public void filterByPosition() {
        if (selectedPlayerPosition == null || selectedPlayerPosition.isEmpty())
            return;
        // Remove from list if player not in selected position
        filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getPosition().equals
                (selectedPlayerPosition));
    }

    public void filterByTeam() {
        if (selectedTeam == null || selectedTeam.isEmpty())
            return;
        // Remove from list if not in the team
        filteredPlayerEntriesList.removeIf(playerEntry -> !playerEntry.getTeam().equals(selectedTeam));
    }

    /**
     * executeQuery method - applies chosen query to the relevant list
     */
    @Override
    public void executeQuery() {
        Query query = new Query(subQueryList, selectedLeagueType);
        filteredPlayerEntriesList = new ArrayList<>(query.executeQuery(playerCatalog));
    }

    /**
     * addFilters method - adds filters input into GUI to subQueryList ArrayList
     */
    @Override
    public void addFilter() {
        subQueriesTextArea.setText(null);
        for (SubQuery subQuery : subQueryList) {
            subQueriesTextArea.append( " " + subQuery.toString() + "; ");
        }
        subQueriesTextArea.repaint();
    }

    /**
     * getValue method - supporter method for updateStatistics providing the corresponding value
     * to the provided label
     */
    private String getValue(PlayerProperty property, String label) {
        double value;
        switch (label) {
            case "minimum:":
                value = playerCatalog.getMinimumValue(property, filteredPlayerEntriesList);
                break;
            case "maximum:":
                value = playerCatalog.getMaximumValue(property, filteredPlayerEntriesList);
                break;
            case "mean:":
                value = playerCatalog.getMeanAverageValue(property, filteredPlayerEntriesList);
                break;
            default:
                return "";
        }
        return String.format("%.2f", value);
    }

    /**
     * updateStatistics method - updates the table with statistics after any changes which may
     * affect the JTable which holds the statistics
     */
    @Override
    public void updateStatistics() {
        statisticsTextArea.removeAll();
        JPanel gridPanel = new JPanel(new GridLayout(0, PlayerProperty.values().length + 1));
        // Add the first row containing the property names
        gridPanel.add(new JLabel(""));
        for (PlayerProperty property : PlayerProperty.values()) {
            JLabel label = new JLabel(property.getName());
            gridPanel.add(label);
        }
        // Add the three categories and the corresponding values for them for each property
        String[] labels = {"minimum:", "maximum:", "mean:"};
        for (String label : labels) {
            gridPanel.add(new JLabel(label));
            for (PlayerProperty property : PlayerProperty.values()) {
                JLabel value = new JLabel(getValue(property, label));
                gridPanel.add(value);
            }
        }
        // Add the grid to the panel
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        statisticsTextArea.setLayout(new BorderLayout());
        statisticsTextArea.add(scrollPane);
    }

    /**
     * updatePlayerCatalogDetailsBox method - updates the list of players when changes are made
     */
    @Override
    public void updatePlayerCatalogDetailsBox() {
        filteredPlayerEntriesTextArea.removeAll();
        JPanel gridPanel = new JPanel(new GridLayout(0, PlayerDetail.values().length));

        for (PlayerDetail detail : PlayerDetail.values()) {
            gridPanel.add(new JLabel(detail.getName()));
        }

        for (PlayerEntry player : filteredPlayerEntriesList) {
            for (PlayerDetail detail : PlayerDetail.values()) {
                gridPanel.add(new JLabel(player.getDetail(detail)));
            }
        }

        // Add the grid to the panel
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        filteredPlayerEntriesTextArea.setLayout(new BorderLayout());
        filteredPlayerEntriesTextArea.add(scrollPane);
    }

    @Override
    public void updateRadarChart() {
        String categoryName = (String) comboRadarChartCategories.getSelectedItem();
        Category cat = Category.getCategoryFromName(categoryName);
        List<PlayerProperty> listProperties = Arrays.asList(cat.getProperties());
        radarChart.updateRadarChartContents(listProperties, filteredPlayerEntriesList);
        repaint();
    }

    @Override
    public boolean isMinCheckBoxSelected() {
        return minCheckBox.isSelected();
    }

    @Override
    public boolean isMaxCheckBoxSelected() {
        return maxCheckBox.isSelected();
    }

    @Override
    public boolean isAverageCheckBoxSelected() {
        return averageCheckBox.isSelected();
    }

    /**
     * clearFilters method - clears all filters from the subQueryList ArrayList and updates
     * the relevant GUI components
     */
    @Override
    public void clearFilters() {
        // Reset first line
        comboLeagueTypes.setSelectedIndex(0);
        comboPlayerNames.setSelectedIndex(0);
        comboNations.setSelectedIndex(0);
        comboPositions.setSelectedIndex(0);
        comboTeams.setSelectedIndex(0);
        // Reset query filters
        value.setText(null);
        subQueryList.clear();
        subQueriesTextArea.setText(null);
        // Repaint
        refreshEntriesListAndGUI();
    }

    /**
     * updatePlayerNamesComboBox method - responsively to filters update the Player name combo box
     */
    public void updatePlayerNamesComboBox() {
        DefaultComboBoxModel<String> nameBoxModel = (DefaultComboBoxModel<String>) comboPlayerNames.getModel();
        // Reset combo box
        nameBoxModel.removeAllElements();
        playerNamesList.clear();

        // Populate the combo box
        playerNamesList.add(0,"");
        for (PlayerEntry player : filteredPlayerEntriesList) {
            playerNamesList.add(player.getPlayerName());
        }
        nameBoxModel.addAll(playerNamesList);
    }

    /**
     * populatePlayerDetailsComboBoxes method - initialise all the combo boxes with all the initial values
     */
    @Override
    public void populatePlayerDetailsComboBoxes() {
        updatePlayerNamesComboBox();
        populateNationComboBox();
        populatePositionComboBox();
        populateTeamComboBox();
    }

    /**
     * refreshEntriesListAndGUI method - apply filters to the filteredPlayerEntriesList and refresh the GUI
     */
    public void refreshEntriesListAndGUI() {
        resetFilteredPlayerEntriesList();
        // Filter by query
        executeQuery();
        // Filter the PlayerEntriesList
        filterByLeagueType();
        filterByNation();
        filterByPosition();
        filterByTeam();

        updateGUI();
    }

    /**
     * updateGUI method - refresh GUI with new filters
     */
    public void updateGUI() {
        updatePlayerNamesComboBox();
        updateStatistics();
        updatePlayerCatalogDetailsBox();
        updateRadarChart();
    }

    /**
     * addListeners method - adds relevant actionListeners to the GUI components
     */
    @Override
    public void addListeners() {
        try {
            comboLeagueTypes.addActionListener(e -> {
                selectedLeagueType = League.fromName(comboLeagueTypes.getSelectedItem().toString());
                refreshEntriesListAndGUI();
            });

            comboNations.addActionListener(e -> {
                selectedPlayerNation = comboNations.getSelectedItem().toString();
                refreshEntriesListAndGUI();
            });

            comboPositions.addActionListener(e -> {
                selectedPlayerPosition = comboPositions.getSelectedItem().toString();
                refreshEntriesListAndGUI();
            });

            comboTeams.addActionListener(e -> {
                selectedTeam = comboTeams.getSelectedItem().toString();
                refreshEntriesListAndGUI();
            });

            buttonAddFilter.addActionListener(e -> {
                try {
                    // Select filter variables
                    PlayerProperty playerProperty = fromPropertyName(comboQueryProperties.getSelectedItem().toString());
                    String operator = comboOperators.getSelectedItem().toString();
                    double providedValue = Double.parseDouble(value.getText());
                    // Create new subQuery and paint it
                    subQueryList.add(new SubQuery(playerProperty, operator, providedValue));
                    addFilter();
                    refreshEntriesListAndGUI();
                } catch (NumberFormatException nfe) {
                    System.out.println("Not a valid filter value");
                }
            });

            comboRadarChartCategories.addActionListener(e -> updateRadarChart());

            minCheckBox.addActionListener(e -> updateRadarChart());

            maxCheckBox.addActionListener(e -> updateRadarChart());

            averageCheckBox.addActionListener(e -> updateRadarChart());

            buttonClearFilters.addActionListener(e -> clearFilters());

        } catch (NullPointerException e) {
            System.err.println("Null object provided");
            e.printStackTrace();
        }
    }
}
