package de.uniba.dsg.wss.jpa.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A product of the wholesale supplier.
 *
 * @author Benedikt Full
 */
@Entity(name = "Product")
@Table(name = "products")
public class ProductEntity extends BaseEntity {

  @Column(nullable = false)
  private String imagePath;

  @Column(nullable = false)
  private String name;

  private double price;

  @Column(nullable = false)
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
