/**
 * 2015. 11. 25.
 * Copyright by joyhan
 * CetizenSmartphoneCrawler.java
 */
package apolloners.hermes.crawler;

import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import apolloners.hermes.model.CetizenSmartphone;

import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CetizenSmartphoneCrawler extends WebCrawler{
	protected static final Logger LOGGER = LoggerFactory.getLogger(CetizenSmartphoneCrawler.class);
	private static final Pattern SPEC_DOCUMENT_FILTER = Pattern.compile("http://review.cetizen.com/review.php\\?maxcount=1&prdstring=[0-9]+&q=spac_get");
	private static final Pattern PHONE_INFO_FILTER = Pattern.compile("http://review.cetizen.com/[a-zA-Z0-9/]*/review");
	
	private List<CetizenSmartphone> cetizenSmartphones = Lists.newArrayList();
	
	@Override
    public boolean shouldVisit(Page page, WebURL url) {
		String href = url.getURL().toLowerCase();
        return (SPEC_DOCUMENT_FILTER.matcher(href).matches()
        		|| PHONE_INFO_FILTER.matcher(href).matches());
    }
	
	@Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        
        if(!SPEC_DOCUMENT_FILTER.matcher(url.toLowerCase()).matches())	{
        	LOGGER.debug("NO SPEC DATA IN {}", url);
        } else	{
        	LOGGER.info("VISITED URL: {}", url);
        ParseData parseData = page.getParseData();
        if (parseData instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) parseData;
            String html = htmlParseData.getHtml();
            try {
                Document document = Jsoup.parse(html);
                Elements tdElems = document.body().select("td[class^=data-meta]");	
                
                CetizenSmartphone cetizenSmartphone = extractCetizenSmartphone(tdElems);
                
                this.cetizenSmartphones.add(cetizenSmartphone);
            } catch (Exception ex) {
                LOGGER.error("PAGE PARSE EXCEPTION: {}", url);
            }
        }
        }
    }

    @Override
    public Object getMyLocalData() {
        return this.cetizenSmartphones;
    }
    
    /**
     * 나중에 바꿔볼게요... 이건 아닌듯하오.... 이렇게 많을 거라곤 생각 못햇어요 
     */
    protected CetizenSmartphone extractCetizenSmartphone(Elements elements)	{
    	CetizenSmartphone cetizenSmartphone = new CetizenSmartphone();
    	
    	Element nameElem = elements.get(3);
        String name = nameElem.text().trim();
        
        Element modelElem = elements.get(5);
        String model = modelElem.text().trim();
        
        Element makerElem = elements.get(7);
        String maker = makerElem.text().trim();
        
        Element releaseDateElem = elements.get(9);
        String releaseDate = releaseDateElem.text().trim();
        
        Element priceElem = elements.get(11);
        String price = priceElem.text().trim();
        
        Element wipiElem = elements.get(13);
        String wipi = wipiElem.text().trim();
        
        Element screenElem = elements.get(15);
        String screen = screenElem.text().trim();
        
        Element pixelsElem = elements.get(17);
        String pixels = pixelsElem.text().trim();
        
        Element ppiElem = elements.get(21);
        String ppi = ppiElem.text().trim();
        
        Element lcdElem = elements.get(23);
        String lcd = lcdElem.text().trim();
        
        Element cpuElem = elements.get(25);
        String cpu = cpuElem.text().trim();
        
        Element chipsetElem = elements.get(27);
        String chipset = chipsetElem.text().trim();
        
        Element memoryElem = elements.get(29);
        String memory = memoryElem.text().trim();
        
        Element osElem = elements.get(31);
        String os = osElem.text().trim();
        
        Element sizeElem = elements.get(33);
        String size = sizeElem.text().trim();
        
        Element weightElem = elements.get(35);
        String weight = weightElem.text().trim();
        
        Element batteryElem = elements.get(37);
        String battery = batteryElem.text().trim();
        
        Element touchTypeElem = elements.get(39);
        String touchType = touchTypeElem.text().trim();
        
        Element backCameraElem = elements.get(43);
        String backCamera = backCameraElem.text().trim();
        
        Element flashTypeElem = elements.get(47);
        String flashType = flashTypeElem.text().trim();
        
        Element videoTypeElem = elements.get(49);
        String videoType = videoTypeElem.text().trim();
        
        Element dmbElem = elements.get(51);
        String dmb = dmbElem.text().trim();
        
        Element bluetoothElem = elements.get(55);
        String bluetooth = bluetoothElem.text().trim();
        
        Element sdCardElem = elements.get(57);
        String sdCard = sdCardElem.text().trim();
        
        Element connectSocketElem = elements.get(59);
        String connectSocket = connectSocketElem.text().trim();
        
        Element wifiElem = elements.get(61);
        String wifi = wifiElem.text().trim();
        
        Element gpsElem = elements.get(63);
        String gps = gpsElem.text().trim();
        
        cetizenSmartphone.setWipi(wipi);
        cetizenSmartphone.setBackCamera(backCamera);
        cetizenSmartphone.setBattery(battery);
        cetizenSmartphone.setBluetooth(bluetooth);
        cetizenSmartphone.setChipset(chipset);
        cetizenSmartphone.setConnectSocket(connectSocket);
        cetizenSmartphone.setCpu(cpu);
        cetizenSmartphone.setDmb(dmb);
        cetizenSmartphone.setFlashType(flashType);
        cetizenSmartphone.setGps(gps);
        cetizenSmartphone.setLcd(lcd);
        cetizenSmartphone.setMaker(maker);
        cetizenSmartphone.setMemory(memory);
        cetizenSmartphone.setModel(model);
        cetizenSmartphone.setName(name);
        cetizenSmartphone.setOs(os);
        cetizenSmartphone.setPixels(pixels);
        cetizenSmartphone.setPpi(ppi);
        cetizenSmartphone.setPrice(price);
        cetizenSmartphone.setReleaseDate(releaseDate);
        cetizenSmartphone.setScreen(screen);
        cetizenSmartphone.setSdCard(sdCard);
        cetizenSmartphone.setSize(size);
        cetizenSmartphone.setTouchType(touchType);
        cetizenSmartphone.setVideoType(videoType);
        cetizenSmartphone.setWeight(weight);
        cetizenSmartphone.setWifi(wifi);
        
        return cetizenSmartphone;
    }
}
