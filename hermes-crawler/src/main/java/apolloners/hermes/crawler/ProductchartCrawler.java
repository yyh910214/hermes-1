package apolloners.hermes.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by lukas on 15. 11. 24..
 */
public class ProductchartCrawler extends WebCrawler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductchartCrawler.class);
    private static final Pattern COLLECTION_FILTER = Pattern.compile("http://www.productchart.com/[a-z]+.*");
    private static final Pattern DOCUMENT_FILTER = Pattern.compile("http://www.productchart.com/[a-z]+/[0-9]+");

    protected String html = "";

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
        String href = url.getURL().toLowerCase();
        return COLLECTION_FILTER.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        LOGGER.info("VISITED URL: {}", url);

        if (!DOCUMENT_FILTER.matcher(url).matches()) {
            return ;
        }

        ParseData parseData = page.getParseData();
        if (parseData instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) parseData;
            this.html = htmlParseData.getHtml();
        }
    }

    @Override
    public Object getMyLocalData() {
        return this.html;
    }
}
