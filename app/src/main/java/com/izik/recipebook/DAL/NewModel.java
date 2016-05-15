package com.izik.recipebook.DAL;

import android.content.Context;

import com.izik.recipebook.Ingredient;
import com.izik.recipebook.Recipe;
import com.izik.recipebook.ServerSideHandlers.ServerRequest;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        JSONObject json = serverRequest.getJSON("http://192.168.95.1:8080/saveRecipe",jsonRecipe);
    }

    public void EditRecipe(Recipe recipe)
    {
        JSONObject jsonRecipe = ConvertRecipeToJSON(recipe);
        ServerRequest serverRequest = new ServerRequest();

        JSONObject json = serverRequest.getJSON("http://192.168.95.1:8080/saveRecipe",jsonRecipe);
    }

    public ArrayList<Recipe> GetAllUserRecipesByID(String id)
    {
        JSONObject jsonId = new JSONObject();

        try {
            jsonId.put("userID", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerRequest serverRequest = new ServerRequest();
        JSONObject json = serverRequest.getJSON("http://192.168.95.1:8080/GetAllUserRecipes",jsonId);

        //String Object_id,String User_id, String name, String description,ArrayList<Ingredient> ingrediants ,
        //String image, String cookingInstructions, String servingInstructions
        ArrayList<Recipe> Results = new ArrayList<Recipe>();
        try {
            JSONArray jar = json.getJSONArray("Recipes");
            for (int i = 0; i < jar.length(); i++) {
                Results.add(ConvertJSONToRecipe(jar.getJSONObject(i))); //Getting current json object and converting to recipe

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Results;
    }

    //endregion

    //region Private

    private Recipe ConvertJSONToRecipe(JSONObject jsonRecipe)
    {
        Recipe ret = new Recipe("","","","",new ArrayList<Ingredient>(),"","","");
        try {
            ret.setName(jsonRecipe.get("Name").toString());
            ret.setDescription(jsonRecipe.get("Description").toString());
            ret.setCookingInstructions(jsonRecipe.get("CookingInstructions").toString());
            ret.setServingInstructions(jsonRecipe.get("ServingInstructions").toString());
            ret.setUserId(jsonRecipe.get("UserID").toString());
            ret.setImage(jsonRecipe.get("ImagePath").toString());
            ret.setObjectID(jsonRecipe.get("_id").toString());



            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
            JSONArray jar = new JSONArray(jsonRecipe.get("Ingredients").toString());

            for (int i=0; i<jar.length();i++)
            {
                JSONObject jobj = jar.getJSONObject(i);
                Ingredient ing = new Ingredient(jobj.get("name").toString(), Double.valueOf(jobj.get("quantity").toString()));
                ingredients.add(ing);
            }

            ret.setIngredients(ingredients);

        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ret;
    }

    private JSONObject ConvertRecipeToJSON(Recipe recipe)
    {
        JSONObject jsonRecipe = new JSONObject();
        try {

            jsonRecipe.put("name", recipe.getName());
            jsonRecipe.put("description", recipe.getDescription());
            jsonRecipe.put("cookingInstructions", recipe.getCookingInstructions());
            jsonRecipe.put("servingInstructions", recipe.getServingInstructions());
            jsonRecipe.put("userID", recipe.getUserId());
            jsonRecipe.put("image", recipe.getImage());

            JSONArray ingredients = new JSONArray();

            for (Ingredient ingredient : recipe.getIngrediants()) {

                JSONObject ingredientjson = new JSONObject();

                ingredientjson.put("name", ingredient.getName());
                ingredientjson.put("quantity", ingredient.getQuantity());
                ingredients.put(ingredientjson);
            }

            jsonRecipe.put("ingredients", ingredients.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonRecipe;
    }


    //endregion

}
