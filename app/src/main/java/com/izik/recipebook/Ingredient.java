package com.izik.recipebook;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable

{
    public String Name;
    public double Quantity;

    public Ingredient(String name, double quantity )
    {
        Name = name;
        Quantity = quantity;
    }

    public Ingredient()
    {
        Name = "";
        Quantity = 0;
    }

    protected Ingredient(Parcel in)
    {
        Name = in.readString();
        Quantity = in.readDouble();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>()
    {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public double getQuantity() {
        return Quantity;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(new String[]{this.Name, String.valueOf(this.Quantity)});
    }
}
