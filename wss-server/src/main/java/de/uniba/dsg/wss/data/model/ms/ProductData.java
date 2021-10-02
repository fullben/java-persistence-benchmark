package de.uniba.dsg.wss.data.model.ms;

import org.jacis.plugin.objectadapter.cloning.JacisCloneable;

/**
 * A product of the wholesale supplier.
 *
 * @author Benedikt Full
 */
public class ProductData extends BaseData implements JacisCloneable<ProductData> {

  private String imagePath;
  private String name;
  private double price;
  private String data;

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    checkWritable();
    this.imagePath = imagePath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    checkWritable();
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    checkWritable();
    this.price = price;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    checkWritable();
    this.data = data;
  }

  @Override
  public ProductData clone() {
    return (ProductData) super.clone();
  }
}
