package de.uniba.dsg.wss.data.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A product of the wholesale supplier.
 *
 * @author Benedikt Full
 */
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

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
