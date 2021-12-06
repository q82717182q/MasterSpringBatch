package com.yao.masterspringbatch.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created by Jack Yao on 2021/12/4 3:54 下午
 */
@XmlRootElement(name = "product")
public class Product {

    private Integer productId;
//    @XmlElement(name = "productName")
    private String productName;
    private BigDecimal price;
    private Integer unit;
    private String productDesc;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productId +
                ", productName='" + productName + '\'' +
                ", ProductDesc='" + productDesc + '\'' +
                ", price=" + price +
                ", unit=" + unit +
                '}';
    }
}
