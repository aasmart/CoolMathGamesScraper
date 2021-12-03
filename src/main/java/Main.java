import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String spreadsheetId = "1kinvjJ9KYp_h4ohH-iVW2rLpIASlAfd3VNoUJtJCI0g";

    public static void main(String[] args) throws IOException {
        String url = "https://www.coolmathgames.com/1-complete-game-list/view-all";
        Document doc = Jsoup.connect(url).get();

        Elements gameRows = doc.body().getElementsByClass("views-row");

        List<Game> games = new ArrayList<>();
        for(Element e : gameRows) {
            if(e.getElementsByClass("game-title").size() > 0) {
                // Get the game title element
                Element gameTitleElement = e.getElementsByClass("game-title").get(0).getAllElements().get(1);
                games.add(new Game(
                        // Get the name
                        gameTitleElement.text(),
                        // Create the hyperlink
                        "https://www.coolmathgames.com" + gameTitleElement.attributes().get("href"),
                        // Detect if it has the flash icon
                        e.getElementsByClass("icon-gamethumbnail-all-game-pg test").size() > 0
                ));
            }
        }

        try {
            GoogleSheets.writeToSheet(spreadsheetId, games);
        } catch (Exception e) {
            System.out.println("Failed to write games to spreadsheet: " + e.getMessage());
        }
    }
}
