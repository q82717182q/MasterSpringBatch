package com.yao.masterspringbatch.model;

import java.math.BigDecimal;

/**
 * Created by Jack Yao on 2021/12/4 3:54 下午
 */
public class Product {
    private Integer productID;
    private String productName;
    private BigDecimal price;
    private Integer unit;
    private String ProdDesc;

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProdDesc() {
        return ProdDesc;
    }

    public void setProdDesc(String prodDesc) {
        ProdDesc = prodDesc;
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
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", ProductDesc='" + ProdDesc + '\'' +
                ", price=" + price +
                ", unit=" + unit +
                '}';
    }
}
