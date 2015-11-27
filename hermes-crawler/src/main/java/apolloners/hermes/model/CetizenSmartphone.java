/**
 * 2015. 11. 25.
 * Copyright by joyhan
 * CetizenSmartphone.java
 */
package apolloners.hermes.model;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class CetizenSmartphone {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(CetizenSmartphone.class);
	
	protected String name; 
	protected String model;
	protected String maker;
	protected String releaseDate;	// 출시일
	protected String price;
	protected String wipi;	// 통신 규격
	protected String screen;	// 액정 크기
	protected String pixels; // 해상도
	protected String ppi;
	protected String lcd;
	protected String cpu;
	protected String chipset;
	protected String memory;
	protected String os;
	protected String size;
	protected String weight;
	protected String battery;
	protected String touchType;
	protected String backCamera;
	protected String frontCamera;
	protected String flashType;
	protected String videoType;
	protected String bluetooth;
	protected String dmb;
	protected String sdCard;
	protected String connectSocket;	// 연결 단자
	protected String wifi;
	protected String gps;
	
	public CetizenSmartphone()	{
		// Empty Constructor
	}
	
	public String getDmb() {
		return dmb;
	}
	public void setDmb(String dmb) {
		this.dmb = dmb;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getWipi() {
		return wipi;
	}
	public void setWipi(String wipi) {
		this.wipi = wipi;
	}
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	public String getPixels() {
		return pixels;
	}
	public void setPixels(String pixels) {
		this.pixels = pixels;
	}
	public String getPpi() {
		return ppi;
	}
	public void setPpi(String ppi) {
		this.ppi = ppi;
	}
	public String getLcd() {
		return lcd;
	}
	public void setLcd(String lcd) {
		this.lcd = lcd;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getChipset() {
		return chipset;
	}
	public void setChipset(String chipset) {
		this.chipset = chipset;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getTouchType() {
		return touchType;
	}
	public void setTouchType(String touchType) {
		this.touchType = touchType;
	}
	public String getBackCamera() {
		return backCamera;
	}
	public void setBackCamera(String backCamera) {
		this.backCamera = backCamera;
	}
	public String getFrontCamera() {
		return frontCamera;
	}
	public void setFrontCamera(String frontCamera) {
		this.frontCamera = frontCamera;
	}
	public String getFlashType() {
		return flashType;
	}
	public void setFlashType(String flashType) {
		this.flashType = flashType;
	}
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	public String getBluetooth() {
		return bluetooth;
	}
	public void setBluetooth(String bluetooth) {
		this.bluetooth = bluetooth;
	}
	public String getSdCard() {
		return sdCard;
	}
	public void setSdCard(String sdCard) {
		this.sdCard = sdCard;
	}
	public String getConnectSocket() {
		return connectSocket;
	}
	public void setConnectSocket(String connectSocket) {
		this.connectSocket = connectSocket;
	}
	public String getWifi() {
		return wifi;
	}
	public void setWifi(String wifi) {
		this.wifi = wifi;
	}
	public String getGps() {
		return gps;
	}
	public void setGps(String gps) {
		this.gps = gps;
	}

	@Override
	public String toString()	{
		ToStringHelper toStringHelper = Objects.toStringHelper(CetizenSmartphone.class);
		Field[] fields = CetizenSmartphone.class.getDeclaredFields();
		for(Field field : fields)	{
			field.setAccessible(true);
			try {
				if(!field.getName().equals("LOGGER"))	{
					toStringHelper.add(field.getName(), field.get(this));
				}
			} catch (Exception e) {
				LOGGER.error("CETIZEN TOSTRING ERROR");
				LOGGER.error(e.getMessage());
			}
		}
		return toStringHelper.toString();
	}
}
