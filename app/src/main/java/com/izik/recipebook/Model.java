package com.izik.recipebook;

import android.content.Context;
import android.util.Log;

import com.izik.recipebook.DAL.NewModel;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import java.util.ArrayList;
import java.util.List;


public class Model
{
    private static final String FAVORITES = "Favorites";
    private static final String USERRECIPEBOOK = "RecipeBookUser";
    private static  Model instance = new Model();
    private User _user;
    private ArrayList<Ingredient> ingrediants = new ArrayList<>();
    private ArrayList<Recipe> allRecipes = new ArrayList<>();
    private ArrayList<Recipe> allCurrentUserRecipes = new ArrayList<>();
    private static OnModelCompletedOperationListener mListener;
    private static Context mContext;
    private static final String USER_ID = "User_ID";
    private static final String INGREDIENTS = "Ingredients";
    private static final String DESC = "Description";
    private static final String NAME = "Name";
    private static final String QUANTITY = "Quantity";
    private static final String IMAGE = "Image";
    private static final String COOKING_INSTR = "Cooking_Instructions";
    private static final String SERVING_INSTR = "Serving_Instructions";
    private static final String OBJECT_ID = "objectId";

    private Model()
    {

    }

    public static Model instance(Context context)
    {
        if (context instanceof OnModelCompletedOperationListener)
        {
            mListener = (OnModelCompletedOperationListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mContext = context;
        return instance;
    }



 /*   private void WriteParseExceptionToLog(ParseException e)
    {
        Log.e("RecipeBook", e.getMessage());
    }


*/
   /* private ArrayList<Recipe> GetRecipesByQuery(ParseQuery query)
    {
        ArrayList<Recipe> output = new ArrayList<>();

        try
        {
            List<ParseObject> list = query.find();

            for (ParseObject object : list)
            {

                ParseRelation<ParseObject> relation = object.getRelation(INGREDIENTS);
                ArrayList<Ingredient> ingredients = GetIngredientsByIngredientsRelation(relation);

                String name = object.getString(NAME);
                String desc = object.getString(DESC);
                String img = object.getString(IMAGE);
                String cookInstr = object.getString(COOKING_INSTR);
                String servInstr = object.getString(SERVING_INSTR);
                String user_id = object.getString(USER_ID);
                String object_id = object.getObjectId();

                output.add(new Recipe(object_id,user_id, name, desc, ingredients, img, cookInstr, servInstr));
            }
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
        }

        return output;
    }
*/
  /*  private ArrayList<Ingredient> GetIngredientsByIngredientsRelation(ParseRelation<ParseObject> relation)
    {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        try
        {
            List<ParseObject> ingredientObjects = relation.getQuery().find();

            for (ParseObject obj : ingredientObjects)
            {
                double quant = Double.valueOf(obj.get(QUANTITY).toString());
                String name = obj.get(NAME).toString();
                ingredients.add(new Ingredient(name, quant));
            }
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
        }

        return ingredients;
    }
*/
    /*public ArrayList<Recipe> GetAllUsersRecipesByLikeExp(String likeExpression, boolean findThisUserRecipes)
    {
        if(!findThisUserRecipes) {
            return GetSubListByExpression(likeExpression, allRecipes);
        }
        else
        {
            return GetSubListByExpression(likeExpression, allCurrentUserRecipes);
        }
    }*/

 /*   public ArrayList<Recipe> GetUserFavoriteRecipesById(String user_Id)
    {       
        final ParseQuery query = new ParseQuery(USERRECIPEBOOK);
        query.whereEqualTo(USER_ID, user_Id);

        try {

            ParseObject userObj = query.getFirst();
            ParseRelation<ParseObject> favoritesRelation = userObj.getRelation(FAVORITES);

            List<ParseObject> objectsList = favoritesRelation.getQuery().find();
            ArrayList<Recipe> output = new ArrayList<Recipe>();

            for (ParseObject recipeObj : objectsList)
            {
                ParseQuery favoritesQuery = new ParseQuery("Recipe");
                favoritesQuery.whereEqualTo(OBJECT_ID, recipeObj.getObjectId());
                output.add(GetRecipesByQuery(favoritesQuery).get(0));
            }

           return output;
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
            return new ArrayList<>();
        }
    }
*/

/*    private void SaveCurrentUser(User user)
    {
        ParseObject UserObj = new ParseObject(USERRECIPEBOOK);
        UserObj.put(USER_ID, user.getId());

        ParseRelation<ParseObject> favoritesRelation = UserObj.getRelation(FAVORITES);

        SaveUserFavorites(user, favoritesRelation);

        try
        {
            UserObj.save();
            user.setObjectId(UserObj.getObjectId());
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
        }
    }
*/
/*    private void SaveUserFavorites(User user, ParseRelation<ParseObject> favoritesRelation)
    {
        for (Recipe recipe : user.getFavoritRecipes()) {
            ParseQuery query = new ParseQuery("Recipe");
            query.whereEqualTo(OBJECT_ID, recipe.getObjectID());

            try
            {
                ParseObject obj = query.getFirst();
                favoritesRelation.add(obj);
            }
            catch (ParseException e)
            {
                WriteParseExceptionToLog(e);
            }
        }
    }
*/
    /*public ArrayList<Recipe> GetUserFavoriteRecipesByLikeExp(String userId, String expression)
    {

        ArrayList<Recipe> favorites = GetUserFavoriteRecipesById(userId);
        // if expression is empty, return all recipes
        return GetSubListByExpression(expression, favorites);
    }*/

 /*   public ArrayList<Recipe> GetSubListByExpression( String expression, ArrayList<Recipe> list)
    {
        if(expression.compareTo("") == 0)
        {
            return list;
        }

        expression = expression.toLowerCase();
        ArrayList<Recipe> output = new ArrayList<>();

        for (Recipe recipe : list)
        {
            if(recipe.getName().toLowerCase().contains(expression))
            {
                output.add(recipe);
            }
            else if(recipe.getDescription().toLowerCase().contains(expression))
            {
                output.add(recipe);
            }
        }

        return output;
    }
*/
    public interface OnModelCompletedOperationListener
    {
        void onAddRecipeComplete(boolean success);
        void onAddIngredientToRecipeComplete(boolean b, String s);
        void onDeleteRecipeComplete(boolean b);
        void onEditRecipeComplete(boolean b);
        void onaddOrRemoveFavoritesFail(Recipe recipe, boolean isAddOperation);
        void onaddOrRemoveFavoritesSuccess(Recipe recipe, boolean isAddOperation);
    }
}
