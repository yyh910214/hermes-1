package apolloners.hermes;

import apolloners.hermes.crawler.ProductchartSmartphonesCrawler;
import apolloners.hermes.model.ProductchartSmartphones;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Created by lukas on 15. 11. 24..
 */
public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        Long elapsedTime = System.currentTimeMillis();

        // 런처 파라미터를 파싱한다.
        ArgumentParser argumentParser = ArgumentParsers.newArgumentParser("Crawler").defaultHelp(true).description("Web crawler for electronics data");
        argumentParser.addArgument("-d", "--dir").dest("dir").setDefault(".").help("Directory for crawled data");
        argumentParser.addArgument("-r", "--remote").dest("remote").required(true).help("HDFS Remote Address");
        argumentParser.addArgument("-o", "--output").dest("output").required(true).help("HDFS Output Path");

        Namespace namespace = null;
        try {
            namespace = argumentParser.parseArgs(args);
        } catch (ArgumentParserException ex) {
            argumentParser.handleError(ex);
            System.exit(1);
        }

        // 파라미터로 가져온 값을 할당한다.
        String dir = namespace.getString("dir");
        String remote = namespace.getString("remote");
        String output = namespace.getString("output");

        // 하둡 설정 및 파일시스템을 불러온다.
        Configuration hdfsConf = new Configuration();
        hdfsConf.set("fs.default.name", remote);
        FileSystem hfs = FileSystem.get(hdfsConf);

        // 출력 파일이 존재하면 삭제한다.
        Path hfile = new Path(output + "/" + UUID.randomUUID());
        if (hfs.exists(hfile)) {
            LOGGER.info("HDFS Output File will be deleted.");
            hfs.delete(hfile, true);
        }

        // 크롤링을 위한 환경을 세팅한다.
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(dir);
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

        // 크롤링 컨트롤러 및 시드를 생성한다.
        CrawlController crawlController = null;

        // productchart.smartphones
        crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        crawlController.addSeed("http://www.productchart.com/smartphones/");
        crawlController.startNonBlocking(ProductchartSmartphonesCrawler.class, 1);
        // TODO, 크롤링 대상 호스트가 생기면 추가한다.

        // 컨트롤러가 완료되기를 기다린다.
        crawlController.waitUntilFinish();

        // 크롤링한 데이터를 하둡 파일시스템에 기록한다.
        FSDataOutputStream out = hfs.create(hfile);
        for (Object localData : crawlController.getCrawlersLocalData()) {
            List<ProductchartSmartphones> productchartSmartphonesList = (List<ProductchartSmartphones>) localData;
            for (ProductchartSmartphones productchartSmartphones : productchartSmartphonesList) {
                out.writeUTF(productchartSmartphones.toString() + "\r\n");
            }
        }
        out.close();
        hfs.close();

        elapsedTime = System.currentTimeMillis() - elapsedTime;

        LOGGER.info("ELAPSED TIME: {}", elapsedTime);
    }
}
