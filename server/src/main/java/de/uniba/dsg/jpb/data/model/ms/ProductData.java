package de.uniba.dsg.jpb.data.model.ms;

/**
 * A product of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class ProductData extends BaseData {

  private String imagePath;
  private String name;
  private double price;
  private String data;

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
