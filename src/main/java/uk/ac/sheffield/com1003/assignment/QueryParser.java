package uk.ac.sheffield.com1003.assignment;

import uk.ac.sheffield.com1003.assignment.codeprovided.*;
import java.util.*;

/**
 * Class to parse Queries. Provides implementation for the readQueries method
 * which creates a list of queries using the queries provided.
 *
 * @version 1.0 21/04/2023
 *
 * @author Zsigmond Kovacs-Nagy
 */
public class QueryParser extends AbstractQueryParser
{

    /**
     * This method selects the league based on the token provided.
     *
     * @param iterator the iterator used to traverse the query tokens
     * @param token the token to check for league selection
     * @return the selected league selector
     */
    private String leagueSelection(Iterator<String> iterator, String token) {
        String league = "Not found";
        // Also test if both leagues are included, if so change league and iterate forward
        switch (token) {
            case "epl":
                String nextToken = iterator.next();
                if (nextToken.equals("or")) {
                    league = "ALL";
                    iterator.next();
                    iterator.next();
                } else {
                    league = "EPL";
                }
                break;
            case "liga":
                nextToken = iterator.next();
                if (nextToken.equals("or")) {
                    league = "ALL";
                    iterator.next();
                    iterator.next();
                } else {
                    league = "LIGA";
                }
                break;
        }
        return league;
    }

    @Override
    public List<Query> readQueries(List<String> queryTokens) {
        // Set up Lists and the iterator used to create the list of Queries
        List<Query> queryList = new ArrayList<>();
        List<SubQuery> subQueryList = new ArrayList<>();
        Iterator<String> iterator = queryTokens.iterator();
        String leagueString = "";

        try {
            iterator.next(); // Iterate over unused values
            while (iterator.hasNext()) {
                String token = iterator.next();
                if (token.equals("select")) {
                    League league = League.valueOf(leagueString);
                    queryList.add(new Query(new ArrayList<>(subQueryList), league));
                    subQueryList.clear();
                    token = iterator.next();
                }

                // Select the league the that will be queried
                if (!token.equals("and")) {
                    leagueString = leagueSelection(iterator, token);
                }

                // Formalise the sub query data and add it to the list
                PlayerProperty playerProperty = PlayerProperty.fromName(iterator.next());
                String operator = iterator.next();
                double value = Double.parseDouble(iterator.next());
                subQueryList.add(new SubQuery(playerProperty, operator, value));
            }
            // Add the last query to the list
            League league = League.valueOf(leagueString);
            queryList.add(new Query(new ArrayList<>(subQueryList), league));

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Argument does not fit");
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            System.err.println("Element not found");
            e.printStackTrace();
        }
        return queryList;
    }
}


