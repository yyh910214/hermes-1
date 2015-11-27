/**
 * 2015. 11. 27.
 * Copyright by yyh / Hubigo AIAL
 * CetizenLauncher.java
 */
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
import apolloners.hermes.model.CetizenSmartphone;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CetizenLauncher {
	private static final Logger LOGGER = LoggerFactory.getLogger(CetizenLauncher.class);

    public static void main(String[] args) throws Exception {
        Long elapsedTime = System.currentTimeMillis();

        // ��ó �Ķ���͸� �Ľ��Ѵ�.
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

        // �Ķ���ͷ� ������ ���� �Ҵ��Ѵ�.
        String dir = namespace.getString("dir");
        String remote = namespace.getString("remote");
        String output = namespace.getString("output");

        // �ϵ� ���� �� ���Ͻý����� �ҷ��´�.
        Configuration hdfsConf = new Configuration();
        hdfsConf.set("fs.default.name", remote);
        FileSystem hfs = FileSystem.get(hdfsConf);

        // ��� ������ �����ϸ� �����Ѵ�.
        Path hfile = new Path(output + "/" + UUID.randomUUID());
        if (hfs.exists(hfile)) {
            LOGGER.info("HDFS Output File will be deleted.");
            hfs.delete(hfile, true);
        }

        // ũ�Ѹ��� ���� ȯ���� �����Ѵ�.
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(dir);
        crawlConfig.setPolitenessDelay(500);
        crawlConfig.setUserAgentString("apolloners");
        crawlConfig.setMaxDepthOfCrawling(3);
        crawlConfig.setMaxConnectionsPerHost(5);
        crawlConfig.setMaxTotalConnections(15);
        crawlConfig.setSocketTimeout(10000);
        crawlConfig.setConnectionTimeout(1000);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setUserAgentName("apolloners");
        robotstxtConfig.setCacheSize(1000);

        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        // ũ�Ѹ� ��Ʈ�ѷ� �� �õ带 �����Ѵ�.
        CrawlController crawlController = null;

        // cetizen.smartphones
        crawlController = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        
        // ������ ��� Ÿ���� ���� �ӽ÷� �̷��� �س����.
        for(int i = 1; i < 40; ++i)	{
        	crawlController.addSeed("http://review.cetizen.com/review.php?q=phone&just_one=&just_one_name=&keyword_p=&p_data=3&p_split=&recnum=50&p=" + i);        	
        }

        crawlController.startNonBlocking(CetizenSmartphoneCrawler.class, 1);
        // TODO, ũ�Ѹ� ��� ȣ��Ʈ�� ����� �߰��Ѵ�.

        // ��Ʈ�ѷ��� �Ϸ�Ǳ⸦ ��ٸ���.
        crawlController.waitUntilFinish();

        // ũ�Ѹ��� �����͸� �ϵ� ���Ͻý��ۿ� ����Ѵ�.
        FSDataOutputStream out = hfs.create(hfile);

        for (Object localData : crawlController.getCrawlersLocalData()) {
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
