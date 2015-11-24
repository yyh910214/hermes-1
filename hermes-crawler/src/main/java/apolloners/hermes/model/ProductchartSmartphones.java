package apolloners.hermes.model;

import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created by lukas on 15. 11. 24..
 */
public class ProductchartSmartphones {
    protected String price;
    protected String screen;
    protected String pixels;
    protected String storage;
    protected String ram;
    protected String weight;
    protected String sdCard;
    protected String battery;
    protected String os;

    public ProductchartSmartphones() {
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSdCard() {
        return sdCard;
    }

    public void setSdCard(String sdCard) {
        this.sdCard = sdCard;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Override
    public int hashCode() {
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putString(price)
                .putString(screen)
                .putString(pixels)
                .putString(storage)
                .putString(ram)
                .putString(weight)
                .putString(sdCard)
                .putString(battery)
                .putString(os)
                .hash();
        return hc.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(ProductchartSmartphones.class)
                .add("price", price)
                .add("screen", screen)
                .add("pixels", pixels)
                .add("storage", storage)
                .add("ram", ram)
                .add("weight", weight)
                .add("sdCard", sdCard)
                .add("battery", battery)
                .add("os", os)
                .toString();
    }
}
