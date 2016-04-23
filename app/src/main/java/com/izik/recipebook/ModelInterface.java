package com.izik.recipebook;

import java.util.ArrayList;

interface ModelInterface{
    public void addRecipe(Recipe recipe);
    public void deleteRecipe(Recipe recipe);
    public Recipe getRecipe(String id);
    public ArrayList<Recipe> getRecipes();
    public Integer[] GetAllRecipesImagesOfCurrentUser();
}
