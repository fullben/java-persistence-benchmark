package de.uniba.dsg.jpb.server.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Item {

  @Id private Long id;
  private Long imageId;
  private String name;
  private double price;
  private String data;

  public Long getId() {
    return id;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Item item = (Item) o;
    return id.equals(item.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
