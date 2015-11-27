package apolloners.hermes;

import java.util.List;
import java.util.UUID;

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

import apolloners.hermes.crawler.CetizenSmartphoneCrawler;
import apolloners.hermes.crawler.ProductchartSmartphonesCrawler;
import apolloners.hermes.model.CetizenSmartphone;
import apolloners.hermes.model.ProductchartSmartphones;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

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
        crawlConfig.setCrawlStorageFolder(dir + "/first");
        crawlConfig.setPolitenessDelay(1000);
        crawlConfig.setUserAgentString("apolloners");
        crawlConfig.setMaxDepthOfCrawling(2);
        crawlConfig.setMaxConnectionsPerHost(5);
        crawlConfig.setMaxTotalConnections(15);
        crawlConfig.setSocketTimeout(10000);
        crawlConfig.setConnectionTimeout(1000);
        
        CrawlConfig crawlConfig2 = new CrawlConfig();
        crawlConfig2.setCrawlStorageFolder(dir + "/second");
        crawlConfig2.setPolitenessDelay(1000);
        crawlConfig2.setUserAgentString("apolloners");
        crawlConfig2.setMaxDepthOfCrawling(3);
        crawlConfig2.setMaxConnectionsPerHost(5);
        crawlConfig2.setMaxTotalConnections(15);
        crawlConfig2.setSocketTimeout(10000);
        crawlConfig2.setConnectionTimeout(500);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setUserAgentName("apolloners");
        robotstxtConfig.setCacheSize(1000);

        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
        PageFetcher pageFetcher2 = new PageFetcher(crawlConfig2);
        RobotstxtServer robotstxtServer2 = new RobotstxtServer(robotstxtConfig, pageFetcher2);

        // 크롤링 컨트롤러 및 시드를 생성한다.
        CrawlController crawlController = null;
        CrawlController crawlController2 = null;

        // productchart.smartphones
        crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        crawlController2 = new CrawlController(crawlConfig2, pageFetcher2, robotstxtServer2);
        crawlController.addSeed("http://www.productchart.com/smartphones/");
        
        // 페이지 어떻게 타는지 몰라서 임시로 이렇게 해놨어요, 150페이지 정도까지 해야하는데 우선 10페이지까지만.
        for(int i = 1; i < 10; ++i)	{
        	crawlController2.addSeed("http://review.cetizen.com/review.php?q=phone&just_one=&just_one_name=&keyword_p=&p_data=3&p_split=&recnum=10&p=" + i);        	
        }
//        crawlController.addSeed("http://review.cetizen.com/review.php?qs=2&p_data=3");
        crawlController.startNonBlocking(ProductchartSmartphonesCrawler.class, 3);
        crawlController2.startNonBlocking(CetizenSmartphoneCrawler.class, 3);
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
        
        // 컨트롤러2의 완료를 기다린다.
        crawlController2.waitUntilFinish();
        
        for (Object localData : crawlController2.getCrawlersLocalData()) {
            List<CetizenSmartphone> cetizenSmartphones = (List<CetizenSmartphone>) localData;
            for (CetizenSmartphone cetizenSmartphone : cetizenSmartphones) {
                out.writeUTF(cetizenSmartphone.toString() + "\r\n");
            }
        }
        
        out.close();
        hfs.close();

        elapsedTime = System.currentTimeMillis() - elapsedTime;

        LOGGER.info("ELAPSED TIME: {}", elapsedTime);
    }
    
}
