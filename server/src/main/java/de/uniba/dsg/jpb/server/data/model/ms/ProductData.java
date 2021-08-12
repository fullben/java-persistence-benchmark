package de.uniba.dsg.jpb.server.data.model.ms;

import de.uniba.dsg.jpb.server.data.model.Identifiable;

public class ProductData implements Identifiable<Long> {

  private Long id;
  private Long imageId;
  private String name;
  private double price;
  private String data;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
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
