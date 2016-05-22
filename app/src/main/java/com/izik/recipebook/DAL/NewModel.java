package com.izik.recipebook.DAL;

import android.content.Context;

import com.izik.recipebook.Ingredient;
import com.izik.recipebook.Recipe;
import com.izik.recipebook.ServerSideHandlers.ServerRequest;
import com.izik.recipebook.User;
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
    //private User _user;

    private NewModel()
    {

    }

    public static NewModel instance() {
        return instance;
    }

    //region Public Methods

    public Recipe AddRecipe(Recipe recipe)
    {
        JSONObject jsonRecipe = ConvertRecipeToJSON(recipe);
        ServerRequest serverRequest = new ServerRequest();

        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/saveRecipe",jsonRecipe);

        Recipe result = ConvertJSONToRecipe(json);
        return result;
    }

    public void EditRecipe(Recipe recipe)
    {
        //please edit the recipe only with the userID ! not objectID !
        JSONObject jsonRecipe = ConvertRecipeToJSON(recipe);
        ServerRequest serverRequest = new ServerRequest();

        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/editRecipe", jsonRecipe);
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
        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/GetAllUserRecipes",jsonId);

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

    public ArrayList<Recipe> GetAllUsersRecipes()
    {
        ServerRequest serverRequest = new ServerRequest();
        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/getAllUsersRecipes", new JSONObject());
        ArrayList<Recipe> Results = new ArrayList();
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

    public ArrayList<Recipe> GetRecommandedRecipes(Context mContext)
    {
        User user = new User(mContext);
        JSONObject jsonUser = ConvertUserToJSON(user);

        ServerRequest serverRequest = new ServerRequest();
        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/getRecommandedRecipesByUser",jsonUser);
        ArrayList<Recipe> Results = new ArrayList();
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

    public User GetCurrentUser(Context mContext)
    {
        User user = new User(mContext);
        JSONObject jsonUser = ConvertUserToJSON(user);
        ServerRequest serverRequest = new ServerRequest();
        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/addUser", jsonUser);

        User userToReturn = ConvertJSONToUser(json, mContext);

        return userToReturn;
    }

    public ArrayList<Recipe> GetUserFavoriteRecipes(Context mContext)
    {
        User user = new User(mContext);
        JSONObject jsonUser = ConvertUserToJSON(user);
        ServerRequest serverRequest = new ServerRequest();
        JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/getUserFavoriteRecipes", jsonUser);

        ArrayList<Recipe> Results = new ArrayList();
        try {
            JSONArray jar = json.getJSONArray("Recipes");
            for (int i = 0; i < jar.length(); i++) {
                Results.add(ConvertJSONToFavoriteRecipe(jar.getJSONObject(i))); //Getting current json object and converting to recipe

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

     return Results;
    }



    public void AddOrRemoveToFavorites(Recipe recipe, Context mContext,boolean isAddOperation)
    {
        JSONObject jsonRecipe = ConvertFavoriteRecipeToJSON(recipe);
        ServerRequest serverRequest = new ServerRequest();
        User user = new User(mContext);

        try {
            jsonRecipe.put("requestedUserID",user.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(isAddOperation)
        {
            JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/addRecipeToUserFavorites",jsonRecipe);
        }
        else
        {
            JSONObject json = serverRequest.getJSON("http://192.168.1.101:8080/removeRecipeFromUserFavorites",jsonRecipe);
        }
    }

    private User ConvertJSONToUser(JSONObject json, Context mContext)
    {
        User result = new User(mContext);



        try
        {
            JSONObject userJson = (JSONObject) json.get("user");
            result.setObjectId(userJson.getString("_id"));

            //setting user favorites
            ArrayList<Recipe> favorites = new ArrayList<Recipe>();
            JSONArray jar = new JSONArray(userJson.get("Favorites").toString());

            for (int i=0; i<jar.length();i++)
            {
                JSONObject jobj = jar.getJSONObject(i);
                Recipe favRecipe =  ConvertJSONToRecipe(jobj);
                favorites.add(favRecipe);
            }

            result.setFavoritRecipes(favorites);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    //endregion

    //region Private

    private Recipe ConvertJSONToFavoriteRecipe(JSONObject jsonRecipe)
    {
        Recipe ret = new Recipe("","","","",new ArrayList<Ingredient>(),"","","");
        try {
            ret.setName(jsonRecipe.get("name").toString());
            ret.setDescription(jsonRecipe.get("description").toString());
            ret.setCookingInstructions(jsonRecipe.get("cookingInstructions").toString());
            ret.setServingInstructions(jsonRecipe.get("servingInstructions").toString());
            ret.setUserId(jsonRecipe.get("userID").toString());
            ret.setImage(jsonRecipe.get("image").toString());
            ret.setObjectID(jsonRecipe.get("objectID").toString());
            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
            JSONArray jar = new JSONArray(jsonRecipe.get("ingredients").toString());

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

    private JSONObject ConvertFavoriteRecipeToJSON(Recipe recipe)
    {
        JSONObject jsonRecipe = new JSONObject();
        try {

            jsonRecipe.put("name", recipe.getName());
            jsonRecipe.put("description", recipe.getDescription());
            jsonRecipe.put("cookingInstructions", recipe.getCookingInstructions());
            jsonRecipe.put("servingInstructions", recipe.getServingInstructions());
            jsonRecipe.put("userID", recipe.getUserId());
            jsonRecipe.put("image", recipe.getImage());
            jsonRecipe.put("objectID", recipe.getObjectID());

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
            jsonRecipe.put("_id", recipe.getObjectID());


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

    private JSONObject ConvertUserToJSON(User user)
    {
        JSONObject userJson = new JSONObject();

        try {
            userJson.put("userID", user.getId());

            JSONArray favorites = new JSONArray();

            for (Recipe recipe : user.getFavoritRecipes()) {

                JSONObject favoritejson = ConvertRecipeToJSON(recipe);
                favorites.put(favoritejson);
            }

            userJson.put("favorites", favorites.toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userJson;
    }

    //endregion

}
