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
            Element title7 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_0 > div > div > div > h1 > span:nth-child(2)").first();
            Element title8 = doc.select("#message-username-1080719105251876974 > span").first();
            Elements body = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div.et_pb_column.et_pb_column_2_3.et_pb_column_2.et_pb_css_mix_blend_mode_passthrough");
            Elements body2 = doc.select("#et-boc > div > div > div > div.et_pb_row.et_pb_row_2.et_pb_equal_columns.et_pb_gutters4 > div > div");

            String runner = title != null && !title.ownText().isEmpty() ? title.ownText() :
                    title2 != null && !title2.ownText().isEmpty() ? title2.ownText() :
                            title3 != null && !title3.ownText().isEmpty() ? title3.ownText() :
                                    title4 != null && !title4.ownText().isEmpty() ? title4.ownText() :
                                            title5 != null && !title5.ownText().isEmpty() ? title5.ownText() :
                                                    title6 != null && !title6.ownText().isEmpty() ? title6.ownText() :
                                                            title7 != null && !title7.ownText().isEmpty() ? title7.ownText() :
                                                                    title8 != null && !title8.ownText().isEmpty() ? title8.ownText() :
                                                            "Runner not found";

            if (body.select("p").isEmpty()) {
                saveRecord(csvPrinter, runner, body2);
            }

            saveRecord(csvPrinter, runner, body);

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
            String query;

            if (body.hasClass("et_pb_css_mix_blend_mode_passthrough")) {
                query = "> div.et_pb_module.et_pb_toggle";
            } else {
                query = "div.et_pb_module.et_pb_toggle.et_pb_toggle";
            }

            for (Element el : body.select(query)) {

                String game = el.select("h5").text();
                StringBuilder category = new StringBuilder();

                for (Element e : el.select("p")) {

                    Elements categoryStrong = e.select("strong");
                    if (categoryStrong.size() > 0 && categoryStrong.hasText()) {
                        category.replace(0, category.length(), categoryStrong.text());
                        continue;
                    }
                    Elements categoryBold = e.select("b");
                    if (categoryBold.size() > 0 && categoryBold.hasText()) {
                        category.replace(0, category.length(), categoryBold.text());
                        continue;
                    }
                    Elements categorySpan = e.select("span");
                    if (categorySpan.size() > 0 && categorySpan.hasText()) {
                        category.replace(0, category.length(), categorySpan.text());
                        continue;
                    }

                    if (e.select("a").text().isEmpty() && e.select("a").select("span").text().isEmpty()) {
                        continue;
                    }
                    if (e.select("a").text().matches("\\(World's First\\)|\\(World’s First\\)")) {
                        continue;
                    }

                    String url = e.select("a").attr("href");
                    String runName;
                    if (e.select("a").text().isEmpty()) {
                        runName = e.select("a").select("span").text();
                    } else if (e.select("a").hasText() && e.select("a").select("span").hasText()) {
                        runName = e.select("a").text() + e.select("a").select("span").text();
                    } else {
                        runName = e.select("a").text();
                    }

                    if (runName.matches("2\\)|3\\)")) {
                        continue;
                    }



                    System.out.println(game);
                    System.out.println(category);
                    System.out.println(runner);
                    System.out.println(runName);
                    System.out.println(url);


                    csvPrinter.printRecord(runner,game, category, runName, url);
                    csvPrinter.flush();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
