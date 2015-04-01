package edu.team7_18842cmu.model;

import java.io.ObjectStreamException;
import java.util.Objects;

/**
 * Created by Michael-Gao on 2015/4/1.
 */
public class ItemInfo {
    String itemName = "";
    String itemPrice = "";
    String store = "";
    String purchaseDate = "";
    String quantity = "";

    public ItemInfo(String itemName, String itemPrice, String store, String purchaseDate, String quantity){
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.store = store;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
    }

    public boolean checkForm(){
        if(this.itemName.trim().length() == 0)
            return true;
        if(this.quantity.trim().length() == 0)
            return true;
        if(this.store.trim().length() == 0)
            return true;
        if(itemPrice.trim().length() == 0)
            return true;
        if(this.purchaseDate.trim().length() == 0)
            return true;
        return false;
    }

    public String getItemName(){
        return this.itemName;
    }

    public String getItemPrice(){
        return this.itemPrice;
    }

    public String getStore(){
        return this.store;
    }

    public String getPurchaseDate(){
        return this.purchaseDate;
    }

    public String getQuantity(){
        return this.quantity;
    }

    public Object[] getAttributes(){
        Object[] objects = new Object[5];
        objects[0] = this.itemName;
        objects[1] = this.itemPrice;
        objects[2] = this.quantity;
        objects[3] = this.store;
        objects[4] = this.purchaseDate;
        return objects;
    }
}
