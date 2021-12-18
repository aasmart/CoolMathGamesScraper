import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {
    private static final String SCRAPE_URL = "https://www.coolmathgames.com/1-complete-game-list/view-all";
    private static final String spreadsheetId = "1kinvjJ9KYp_h4ohH-iVW2rLpIASlAfd3VNoUJtJCI0g";
    public static final String WRITE_SHEET_NAME = "Games";

    public static void main(String[] args) throws IOException {
        /* Create a map with the games and if they've been played. This is so when new games are added, they don't
        offset the table.
         */
        LinkedHashMap<String, String> gamePlayedMap = new LinkedHashMap<>();
        try {
            List<List<Object>> values = Sheets.readSheet(spreadsheetId, WRITE_SHEET_NAME + "!A2:F");

            for(List<Object> row : values) {
                gamePlayedMap.put(row.get(0).toString(), row.get(row.size() - 1).toString());
            }
        } catch (Exception e) {
            System.out.println("Failed to read spreadsheet values:" + e.getMessage());
        }

        // Connect to the website and scrape data
        Document doc = Jsoup.connect(SCRAPE_URL).get();

        Elements gameRows = doc.body().getElementsByClass("views-row");

        List<Game> games = new ArrayList<>();
        int gameNum = 0;
        for(Element e : gameRows) {
            if(e.getElementsByClass("game-title").size() > 0) {
                // Get the game title element
                Element gameTitleElement = e.getElementsByClass("game-title").get(0).getAllElements().get(1);
                String hyperlink = "https://www.coolmathgames.com" + gameTitleElement.attributes().get("href");

                // Get the game's page
                Document gamePage = Jsoup.connect(hyperlink).get();

                // Get Ratings and Votes
                Elements ratings = gamePage.getElementsByClass("rating-val");
                Elements votes = gamePage.getElementsByClass("cmg_game_tot_cnt");
                games.add(new Game(
                        // Get the name
                        gameTitleElement.text(),
                        hyperlink,
                        /* Check if the game is new by checking if there are at least some values in the spreadsheet
                        and the game name doesn't already exist*/
                        gamePlayedMap.size() > 0 && !gamePlayedMap.containsKey(gameTitleElement.text()),
                        // Detect if it has the flash icon
                        e.getElementsByClass("icon-gamethumbnail-all-game-pg test").size() > 0,
                        // Get the rating
                        ratings.size() > 0 ? ratings.get(0).text() : "0.0",
                        votes.size() > 0 ? votes.get(0).text().replaceAll("(,|Votes)", "") : "0",
                        gamePlayedMap.getOrDefault(gameTitleElement.text(), "FALSE")
                ));

                if(++gameNum % 50 == 0)
                    System.out.println("Game #" + gameNum + " read...");
            }
        }

        // Write the data to the spreadsheet
        try {
            Sheets.writeToSheet(spreadsheetId, games, WRITE_SHEET_NAME + "!A2:F");
        } catch (Exception e) {
            System.out.println("Failed to write games to spreadsheet: " + e.getMessage());
        }
    }
}
