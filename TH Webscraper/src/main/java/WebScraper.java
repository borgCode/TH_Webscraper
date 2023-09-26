import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WebScraper {

    public static void main(String[] args) throws IOException {

        String filePath = "runs.csv";
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        Document document = Jsoup.connect("https://www.teamhitless.com/about/members/").get();
        Elements runnerBody = document.select("#et-boc > div > div > div.et_pb_section.et_pb_section_1.et_pb_with_background.et_section_regular > div.et_pb_row.et_pb_row_2 > div > div > div > p").select("a");
        for (int i = 0; i < runnerBody.size(); i++) {
            String urlString = getURL(runnerBody.get(i).attr("href"));
            Document doc = Jsoup.parse(urlString);
            doc.connection().get();
            Element title = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1 > span").first();
            Element title2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1").first();
            Elements body = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div.et_pb_column.et_pb_column_2_3.et_pb_column_2.et_pb_css_mix_blend_mode_passthrough");
            Elements body2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div > div");

            String runner = title != null ? title.ownText() : title2 != null ? title2.ownText() : "Runner not found";

            if (body.select("p").isEmpty()) {
                saveRecord(csvPrinter, runner, body2);
            }
            saveRecord(csvPrinter, runner, body);
            csvPrinter.flush();
        }



    }

    private static void saveRecord(CSVPrinter csvPrinter,  String runner, Elements body) {
        try {

            for (Element e : body.select("p").select("a")) {

                if (e.ownText().isEmpty()) {
                    continue;
                }

                String url = e.attr("href");
                String runName = e.ownText();
                if (runName.isEmpty()) {
                    runName = e.select("span").text();
                }

                csvPrinter.printRecord(runner, runName, url);
                csvPrinter.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getURL(String href) {
        String url = "https://www.teamhitless.com" + href;
        return url;
    }
}
