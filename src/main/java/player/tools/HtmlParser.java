package player.tools;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class HtmlParser {

    private HtmlParser() {
        // Tool class
    }

    public static void main(String args[]) throws IOException {
        Document dom = Jsoup.parse(Paths.get("/home/tmi/shared", "tmp.html").toFile(), "UTF-8");

        Elements elements = dom.select(".outputLine");

        List<String> playerInput = new ArrayList<>();
        List<String> opponentInput = new ArrayList<>();

        for(int i = 0; i < elements.size(); i++){
            if(i % 2 == 0){
                playerInput.add(elements.get(i).text());
            }else{
                opponentInput.add(elements.get(i).text());
            }
        }

        System.out.println(playerInput);
        System.out.println(opponentInput);
    }
}
