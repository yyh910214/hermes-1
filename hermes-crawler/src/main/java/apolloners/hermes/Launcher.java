package apolloners.hermes;

import apolloners.hermes.crawler.ProductchartCrawler;
import com.google.common.collect.Lists;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lukas on 15. 11. 24..
 */
public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);
    private static final List<String> CRAWLED_HTML_LIST = Lists.newArrayList();

    public static void main(String[] args) throws Exception {
        // 시드 환경설정 파일을 로딩한다.
        Properties seedProperties = new Properties();
        try {
            seedProperties.load(Launcher.class.getResourceAsStream("/seed.properties"));
        } catch (IOException ex) {
            LOGGER.error("'seed.properties' file is not found");
            System.exit(1);
        }

        // 런처 파라미터를 파싱한다.
        ArgumentParser argumentParser = ArgumentParsers.newArgumentParser("Crawler").defaultHelp(true).description("Web crawler for electronics data");
        argumentParser.addArgument("-n", "--number").dest("number").setDefault(1).help("Number of crawler");
        argumentParser.addArgument("-d", "--dir").dest("dir").setDefault(".").help("Directory for crawled data");

        Namespace namespace = null;
        try {
            namespace = argumentParser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            argumentParser.handleError(ex);
            System.exit(1);
        }

        // 파라미터로 가져온 값을 할당한다.
        Integer number = Integer.parseInt(namespace.getString("number").trim());
        String directory = namespace.getString("dir");

        // 크롤링을 위한 환경을 세팅한다.
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(directory);
        crawlConfig.setPolitenessDelay(1000);
        crawlConfig.setUserAgentString("apolloners");
        crawlConfig.setMaxDepthOfCrawling(2);
        crawlConfig.setMaxConnectionsPerHost(5);
        crawlConfig.setMaxTotalConnections(15);
        crawlConfig.setSocketTimeout(10000);
        crawlConfig.setConnectionTimeout(1000);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setUserAgentName("apolloners");
        robotstxtConfig.setCacheSize(1000);

        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        // 크롤링 컨트롤러를 생성한다. (개별 호스트 단위로 컨트롤러를 추가한다.)
        // #1, productchart 호스트를 추가한다.
        CrawlController productchartCrawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        // TODO, 추가되는 크롤링 대상 호스트가 생기면 추가한다.

        // 크롤링 컨트롤러 리스트를 관리한다.
        List<CrawlController> crawlControllers = Lists.newArrayList();
        crawlControllers.add(productchartCrawlController);
        // TODO, 추가되는 크롤링 대상 호스트가 생기면 컨트롤러 리스트에 add 한다

        // 시드 키에 따라 해당하는 컨트롤러의 시드로 추가한다.
        for (Map.Entry<Object, Object> entry : seedProperties.entrySet()) {
            String key = (String) entry.getKey();
            String seed = (String) entry.getValue();

            if (key.startsWith("productchart")) {
                LOGGER.info("PRODUCTCHART SEED ADDED: {}", key);
                productchartCrawlController.addSeed(seed);
            }
        }

        // 개별 컨트롤러를 Non-Blocking 방식으로 실행한다.
        productchartCrawlController.startNonBlocking(ProductchartCrawler.class, number);
        // TODO, 추가된 크롤링 컨트롤러의 startNonBlocking 메소드를 호출한다.

        // 모튼 크롤링이 완료될 때지 기다린다.
        for (CrawlController crawlController : crawlControllers) {
            crawlController.waitUntilFinish();
            List<Object> crawledLocalData = crawlController.getCrawlersLocalData();
            for (Object localData : crawledLocalData) {
                CRAWLED_HTML_LIST.add((String) localData);
            }
        }

        LOGGER.info("Crawled html size: {}", CRAWLED_HTML_LIST.size());
        for (String html : CRAWLED_HTML_LIST) {
            LOGGER.info(html.substring(0, 100));
        }
    }
}
