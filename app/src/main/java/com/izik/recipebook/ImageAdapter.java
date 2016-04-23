package com.izik.recipebook;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter
{
    private Resources resources;
    private Context mContext;
    private Integer[] mThumbIds;
    private String favoriteRecipesByUserId;

    public ImageAdapter(Resources resources, Context c)
    {
        mContext = c;
        this.resources = resources;

    }

    public ImageAdapter()
    {
    }

    public int getCount()
    {
        return mThumbIds.length;
    }

    public Object getItem(int position)
    {
        if(mThumbIds.length > position)
        {
            return mThumbIds[position];
        }
        return null;
    }

    public long getItemId(int position) {
       return 0;
    }

    public Integer[] getmThumbIds() {
        return mThumbIds;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setMaxHeight(100);
            imageView.setMaxWidth(80);
            imageView.setPadding(0,0,0,0);

        }
        else
        {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public void RefreshUserRecipesImagesList(String user_id)
    {
        SetRecipesImagesArrayForUser(user_id);
    }

    public  ArrayList<Recipe> SetRecipesImagesArrayForUser(String id)
    {
        ArrayList<Recipe> userRecipes = Model.instance(resources,mContext).GetAllUserRecipesByID(id);
        mThumbIds = GetAllImagesFromArrayList(userRecipes);
        return userRecipes;
    }

    public  ArrayList<Recipe> setRecipesFromAllUsers()
    {
        ArrayList<Recipe> allRecipes = Model.instance(resources,mContext).GetAllUsersRecipes();
        mThumbIds = GetAllImagesFromArrayList(allRecipes);
        return  allRecipes;
    }

    public ArrayList<Recipe> setRecipesBy(String likeExpression, boolean findThisUserRecipes) {

        ArrayList<Recipe> likeExpressionRecipes = Model.instance(resources, mContext).GetAllUsersRecipesByLikeExp(likeExpression, findThisUserRecipes);
        mThumbIds = GetAllImagesFromArrayList(likeExpressionRecipes);
        return likeExpressionRecipes;
    }


    private Integer[] GetAllImagesFromArrayList(ArrayList<Recipe> recipes)
    {
        Integer[] output = new Integer[recipes.size()];

        if(resources != null) {

            for (int i = 0; i < output.length; i++) {
                int resourceId = resources.getIdentifier(recipes.get(i).getImage(), "drawable", "com.izik.recipebook");

                output[i] = resourceId;
            }
        }
        else
        {
            for (int i = 0; i < output.length; i++) {
                int resourceId = R.drawable.pie;
                output[i] = resourceId;
            }
        }

        return output;
    }

    public ArrayList<Recipe> setFavoriteRecipesByUserId(String UserId)
    {
        ArrayList<Recipe> favorites = Model.instance(resources,mContext).GetUserFavoriteRecipesById(UserId);
        mThumbIds = GetAllImagesFromArrayList(favorites);

        return favorites;
    }

    public ArrayList<Recipe> setUserFavoriteRecipesByExpression(String userId, String expression)
    {
        ArrayList<Recipe> likeExpressionRecipes = Model.instance(resources, mContext).GetUserFavoriteRecipesByLikeExp(userId, expression);
        mThumbIds = GetAllImagesFromArrayList(likeExpressionRecipes);
        return likeExpressionRecipes;
    }
}