package com.izik.recipebook;


import android.content.Context;
import java.util.ArrayList;
import java.util.UUID;

public class User
{
    private String id;
    private ArrayList<Recipe> userRecipes = new ArrayList<>();
    private ArrayList<Recipe> favoritRecipes = new ArrayList<>();
    private Context context;
    private String objectId;

    public User (Context context)
    {
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), 0);
        this.context = context;

        id = deviceUuid.toString();
    }

    public String getId() {
        return id;
    }

    public ArrayList<Recipe> getFavoritRecipes() {
        return favoritRecipes;
    }

    public ArrayList<Recipe> getUserRecipes() {
        return userRecipes;
    }

    public void setFavoritRecipes(ArrayList<Recipe> favoritRecipes) {
        this.favoritRecipes = favoritRecipes;
    }

    public void setUserRecipes(ArrayList<Recipe> userRecipes) {
        this.userRecipes = userRecipes;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }
}
