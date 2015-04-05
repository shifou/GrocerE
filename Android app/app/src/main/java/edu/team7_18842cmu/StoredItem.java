package edu.team7_18842cmu;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Nick on 4/4/2015.
 */
public class StoredItem {
    public String itemName, itemStore, itemSize;
    public BigDecimal itemPrice;
    public Date purchaseDate;


    public StoredItem() {
        super();
    }

    public void setItemName(String name){
        this.itemName = name;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemStore(String store){
        this.itemStore = store;
    }

    public String getItemStore() {
        return itemStore;
    }

    public void setItemSize(String size){
        this.itemSize = size;
    }

    public String getItemSize() {
        return itemName;
    }

    public void setItemPrice(BigDecimal price){
        this.itemPrice = price;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setPurchaseDate(Date date){
        this.purchaseDate = date;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }
}


