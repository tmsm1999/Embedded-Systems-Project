package com.cashierapp.Classes;

public class Cashier {
    String sName;
    String sWeight;

    public Cashier() {
    }

    public Cashier(String name, String weight) {
        this.sName = name;
        this.sWeight = weight;
    }

    public String getName(){
        return sName;
    }

    public String getWeight(){
        return sWeight;
    }

    public void setWeight(String weight){
        this.sWeight = weight;
    }

}
