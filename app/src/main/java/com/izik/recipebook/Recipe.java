package com.izik.recipebook;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Properties;


public class Recipe implements Parcelable
{

    private String userId;
    private String name;
    private String description;
    private ArrayList<Ingredient> ingrediants;
    private String image;
    private String cookingInstructions;
    private String servingInstructions;
    private String objectID;


    public Recipe(String Object_id,String User_id, String name, String description,ArrayList<Ingredient> ingrediants ,
                  String image, String cookingInstructions, String servingInstructions)
    {
        this.objectID = Object_id;
        this.name = name;
        this.userId = User_id;
        this.description = description;
        this.image = image;
        this.ingrediants = ingrediants;
        this.servingInstructions = servingInstructions;
        this.cookingInstructions = cookingInstructions;
    }


    public Recipe(Parcel in) {
        String[] data = new String[7];

        in.readStringArray(data);
        this.objectID = data[0];
        this.userId = data[1];
        this.name = data[2];
        this.description = data[3];
        in.readTypedList(ingrediants, Ingredient.CREATOR);
        this.image = data[4];
        this.cookingInstructions = data[5];
        this.servingInstructions = data[6];
    }


    public String getUserId()
    {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<Ingredient> getIngrediants() {
        return ingrediants;
    }

    public String getCookingInstructions() {
        return cookingInstructions;
    }

    public String getServingInstructions() {
        return servingInstructions;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCookingInstructions(String cookingInstructions) {
        this.cookingInstructions = cookingInstructions;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setServingInstructions(String servingInstructions) {
        this.servingInstructions = servingInstructions;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(new String[] {this.objectID, this.userId,this.name,this.description,
                this.image, this.cookingInstructions, this.servingInstructions});
        dest.writeTypedList(ingrediants);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingrediants = ingredients;
    }

    public Object getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
}
