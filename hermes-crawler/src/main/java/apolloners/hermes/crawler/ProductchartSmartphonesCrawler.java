package apolloners.hermes.crawler;

import apolloners.hermes.model.ProductchartSmartphones;
import com.google.common.collect.Lists;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lukas on 15. 11. 24..
 */
public final class ProductchartSmartphonesCrawler extends WebCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductchartSmartphonesCrawler.class);
    private static final Pattern DOCUMENT_FILTER = Pattern.compile("http://www.productchart.com/smartphones/[0-9]+");

    private List<ProductchartSmartphones> productchartSmartphonesList = Lists.newArrayList();

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
        String href = url.getURL().toLowerCase();
        return DOCUMENT_FILTER.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        LOGGER.info("VISITED URL: {}", url);

        ParseData parseData = page.getParseData();
        if (parseData instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) parseData;
            String html = htmlParseData.getHtml();
            try {
                Document document = Jsoup.parse(html);
                Element tbodyElem = document.body()
                        .getElementsByClass("content").get(0)
                        .getElementsByClass("item").get(0)
                        .getElementsByTag("table").get(0)
                        .getElementsByTag("tbody").get(0);
                Elements trElems = tbodyElem.getElementsByTag("tr");

                Element priceElem = trElems.get(1);
                String price = priceElem.getElementsByClass("data").get(0).text().trim();

                Element screenElem = trElems.get(2);
                String screen = screenElem.getElementsByClass("data").get(0).text().trim();

                Element pixelsElem = trElems.get(3);
                String pixels = pixelsElem.getElementsByClass("data").get(0).text().trim();

                Element storageElem = trElems.get(4);
                String storage = storageElem.getElementsByClass("data").get(0).text().trim();

                Element ramElem = trElems.get(5);
                String ram = ramElem.getElementsByClass("data").get(0).text().trim();

                Element weightElem = trElems.get(6);
                String weight = weightElem.getElementsByClass("data").get(0).text().trim();

                Element sdCardElem = trElems.get(8);
                String sdCard = sdCardElem.getElementsByClass("data").get(0).text().trim();

                Element batteryElem = trElems.get(9);
                String battery = batteryElem.getElementsByClass("data").get(0).text().trim();

                Element osElem = trElems.get(10);
                String os = osElem.getElementsByClass("data").get(0).text().trim();

                ProductchartSmartphones productchartSmartphones = new ProductchartSmartphones();
                productchartSmartphones.setPrice(price);
                productchartSmartphones.setScreen(screen);
                productchartSmartphones.setPixels(pixels);
                productchartSmartphones.setStorage(storage);
                productchartSmartphones.setRam(ram);
                productchartSmartphones.setWeight(weight);
                productchartSmartphones.setSdCard(sdCard);
                productchartSmartphones.setBattery(battery);
                productchartSmartphones.setOs(os);

                this.productchartSmartphonesList.add(productchartSmartphones);
            } catch (Exception ex) {
                LOGGER.error("PAGE PARSE EXCEPTION: {}", url);
            }
        }
    }

    @Override
    public Object getMyLocalData() {
        return this.productchartSmartphonesList;
    }

}
