import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WebScraper {

    public static void main(String[] args) throws IOException {


        String filePath = "TH Webscraper/runs.csv";
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);


        Document document = Jsoup.connect("https://www.teamhitless.com/about/members/").timeout(0).get();
        Elements runnerBody = document.select("#et-boc > div > div > div.et_pb_section.et_pb_section_1.et_pb_with_background.et_section_regular > div.et_pb_row.et_pb_row_2 > div > div > div > p").select("a");
        for (int i = 0; i < runnerBody.size(); i++) {
            String runnerName;
            if (runnerBody.get(i).attr("href").startsWith("https://www.teamhitless.com")) {
                runnerName = StringUtils.remove(runnerBody.get(i).attr("href"), "https://www.teamhitless.com");
            } else if (runnerBody.get(i).attr("href").startsWith("http://www.teamhitless.com")) {
                runnerName = StringUtils.remove(runnerBody.get(i).attr("href"), "http://www.teamhitless.com");
            } else {
                runnerName = runnerBody.get(i).attr("href");
            }
            Document doc = Jsoup.connect("https://www.teamhitless.com" + runnerName).timeout(0).ignoreHttpErrors(true).get();
            Element title = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1 > span").first();
            Element title2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1 > a").first();
            Element title3 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1").first();
            Element title4 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > div > h1").first();
            Element title5 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > div > h1 > span").first();
            Element title6 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > div > div > h1").first();
            Elements body = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div.et_pb_column.et_pb_column_2_3.et_pb_column_2.et_pb_css_mix_blend_mode_passthrough");
            Elements body2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div > div");

            String runner = title != null && !title.ownText().isEmpty() ? title.ownText() :
                    title2 != null && !title2.ownText().isEmpty() ? title2.ownText() :
                            title3 != null && !title3.ownText().isEmpty() ? title3.ownText() :
                                    title4 != null && !title4.ownText().isEmpty() ? title4.ownText() :
                                            title5 != null && !title5.ownText().isEmpty() ? title5.ownText() :
                                                    title6 != null && !title6.ownText().isEmpty() ? title6.ownText() :
                                                            "Runner not found";

            if (body.select("p").isEmpty()) {
                saveRecord(csvPrinter, runner, body2);
            } else {
                saveRecord(csvPrinter, runner, body);
            }
            csvPrinter.flush();
        }

        setCurrentMemberIndex(3);


    }

    private static void setCurrentMemberIndex(int size) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("TH Webscraper/currentMemberIndex.txt"));
        writer.write(String.valueOf(size));
        writer.close();
    }


    private static void saveRecord(CSVPrinter csvPrinter, String runner, Elements body) {
        try {

            for (Element e : body.select("p").select("a")) {

                if (e.ownText().isEmpty() && e.select("span").text().isEmpty()) {
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

}
