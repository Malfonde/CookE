package com.izik.recipebook.DAL;

import android.content.Context;

import com.izik.recipebook.Ingredient;
import com.izik.recipebook.Recipe;
import com.izik.recipebook.ServerSideHandlers.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by izik on 4/30/2016.
 */
public class NewModel
{
    private static  NewModel instance = new NewModel();

    private NewModel()
    {

    }

    public static NewModel instance() {
        return instance;
    }

    //region Public Methods

    public void AddRecipe(Recipe recipe)
    {
        JSONObject jsonRecipe = ConvertRecipeToJSON(recipe);
        ServerRequest serverRequest = new ServerRequest();

        JSONObject json = serverRequest.getJSON("http://192.168.1.100:8080/saveRecipe",jsonRecipe);

    }

    //endregion

    //region Private

    private JSONObject ConvertRecipeToJSON(Recipe recipe)
    {
        JSONObject student1 = new JSONObject();
        try {

            student1.put("name", recipe.getName());
            student1.put("description", recipe.getDescription());
            student1.put("cookingInstructions", recipe.getCookingInstructions());
            student1.put("servingInstructions", recipe.getServingInstructions());
            student1.put("userID", recipe.getUserId());
            student1.put("image", recipe.getImage());

            JSONArray ingredients = new JSONArray();

            for (Ingredient ingredient : recipe.getIngrediants()) {

                JSONObject ingredientjson = new JSONObject();

                ingredientjson.put("name", ingredient.getName());
                ingredientjson.put("quantity", ingredient.getQuantity());
                ingredients.put(ingredientjson);
            }

            student1.put("ingredients", ingredients.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return student1;
    }


    //endregion

}
