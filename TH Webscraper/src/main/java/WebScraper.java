import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WebScraper {

    public static void main(String[] args) throws IOException {

        Document doc = Jsoup.connect("https://www.teamhitless.com/project/Squillakilla/").get();
        Element title = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1 > span").first();
        Element title2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1").first();
        Elements body = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div.et_pb_column.et_pb_column_2_3.et_pb_column_2.et_pb_css_mix_blend_mode_passthrough");
        Elements body2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div > div");
        System.out.println(body.select("p").size());
        System.out.println(body2.select("p").size());


        String runner = title != null ? title.ownText() : title2 != null ? title2.ownText() : "Runner not found";
        System.out.println(runner);
        search(body);
        if (body.select("p").isEmpty()) {
            search(body2);
        }



        Writer writer = Files.newBufferedWriter(Paths.get("links.csv"));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

    }

    private static void search(Elements body) {
        for (Element e : body.select("p").select("a:first-child")) {


            String url = e.attr("href");


            String runName = e.ownText();
            if (runName.isEmpty()) {
                runName = e.select("span").text();
            }
            System.out.println(url);
            System.out.println(runName);


        }
    }
}
