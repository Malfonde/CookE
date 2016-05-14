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

    private User AssignCurrentUser()
    {
        final User user = new User(mContext);
        final ParseQuery query = new ParseQuery(USERRECIPEBOOK);
        query.whereEqualTo(USER_ID, user.getId());
        ParseObject obj = null;

        try
        {
            obj = query.getFirst();
        }
        catch (ParseException e)
        {
            if(e.getCode() == 101)
            {
                SaveCurrentUser(user);
                return user;
            }

            WriteParseExceptionToLog(e);
        }

        if(obj != null)
        {
            ParseRelation<ParseObject> favoritesRelation = obj.getRelation(FAVORITES);

            favoritesRelation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null) {
                        Log.e("RecipeBook", e.getMessage());
                    } else {
                        ArrayList<Recipe> output = new ArrayList<Recipe>();

                        for (ParseObject obj : objects) {
                            ParseQuery favoritesQuery = new ParseQuery("Recipe");
                            favoritesQuery.whereEqualTo(OBJECT_ID, obj.getObjectId());
                            output.add(GetRecipesByQuery(query).get(0));
                        }

                        user.setFavoritRecipes(output);
                    }
                }
            });

            user.setObjectId(obj.getObjectId());
        }


        _user = user;
        return _user;
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

    public void AddRecipe(Recipe recipe)
    {
        NewModel newModel = NewModel.instance();
        newModel.AddRecipe(recipe);

        ParseObject myObj = new ParseObject("Recipe");
        myObj.put(USER_ID, recipe.getUserId());
        myObj.put(NAME, recipe.getName());
        myObj.put(DESC, recipe.getDescription());
        myObj.put(IMAGE, recipe.getImage());
        myObj.put(COOKING_INSTR, recipe.getCookingInstructions());
        myObj.put(SERVING_INSTR, recipe.getServingInstructions());

        ParseRelation<ParseObject> ingredientsRelation = myObj.getRelation(INGREDIENTS);
        //sacing each ingredient in cloud db
        SaveIngridientsListOfRecipe(recipe, ingredientsRelation);

        try
        {
            myObj.save();
            mListener.onAddRecipeComplete(true);
            recipe.setObjectID(myObj.getObjectId());
            allCurrentUserRecipes.add(recipe);
        }
        catch (ParseException e)
        {
            mListener.onAddRecipeComplete(false);
        }
    }

    private void SaveIngridientsListOfRecipe(Recipe recipe, ParseRelation<ParseObject> ingredientsRelation)
    {
        for (Ingredient ingredient : recipe.getIngrediants()) {
            ParseObject ingredientObj = new ParseObject("Ingredient");
            ingredientObj.put(NAME, ingredient.getName());
            ingredientObj.put(QUANTITY, ingredient.getQuantity());
            ingredientsRelation.add(ingredientObj);

            try
            {
                ingredientObj.save();
                mListener.onAddIngredientToRecipeComplete(true, "");
            }
            catch (ParseException e)
            {
                mListener.onAddIngredientToRecipeComplete(false, e.getMessage());
            }
        }
    }

    public void AddOrRemoveRecipeToFavorites(Recipe recipe, boolean isAddOperation)
    {
        ParseQuery query = new ParseQuery(USERRECIPEBOOK);
        query.whereEqualTo(OBJECT_ID, GetCurrentUser().getObjectId());

        try
        {
            ParseObject userObj = query.getFirst();
            ParseRelation<ParseObject> relation = userObj.getRelation(FAVORITES);
            ParseQuery recipeQuery = new ParseQuery("Recipe");
            recipeQuery.whereEqualTo(OBJECT_ID, recipe.getObjectID());

            ParseObject recipeObj = recipeQuery.getFirst();

            if(isAddOperation) {
                relation.add(recipeObj);
            }
            else
            {
                relation.remove(recipeObj);
            }

            userObj.save();

        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
            mListener.onaddOrRemoveFavoritesFail(recipe, isAddOperation);
        }

        mListener.onaddOrRemoveFavoritesSuccess(recipe, isAddOperation);

    }

    private void WriteParseExceptionToLog(ParseException e)
    {
        Log.e("RecipeBook", e.getMessage());
    }


    public ArrayList<Recipe> GetAllUserRecipesByID(String id)
    {
//        ParseQuery query = new ParseQuery("Recipe");
//        query.whereEqualTo(USER_ID, id);
//
//        allCurrentUserRecipes = GetRecipesByQuery(query);
//
//        return allCurrentUserRecipes;

        NewModel newModel = NewModel.instance();
        return newModel.GetAllUserRecipesByID(id);
    }

    public ArrayList<Recipe>  GetAllUsersRecipes()
    {
        ParseQuery query = new ParseQuery("Recipe");
        if(allCurrentUserRecipes.size() != 0)
        {
            query.whereNotEqualTo(USER_ID, allCurrentUserRecipes.get(0).getUserId());
        }
        else
        {
            User user = new User(mContext);
            query.whereNotEqualTo(USER_ID, user.getId());
        }

        ArrayList<Recipe> output = GetRecipesByQuery(query);
        allRecipes = output;

        return allRecipes;
    }

    private ArrayList<Recipe> GetRecipesByQuery(ParseQuery query)
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

    private ArrayList<Ingredient> GetIngredientsByIngredientsRelation(ParseRelation<ParseObject> relation)
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

    public void EditRecipe(Recipe recipe)
    {
        ParseQuery query = new ParseQuery("Recipe");
        query.whereEqualTo(OBJECT_ID,recipe.getObjectID());

        try
        {
            ParseObject obj = query.getFirst();

            if(obj != null) {

                obj.put(NAME, recipe.getName());
                obj.put(DESC, recipe.getDescription());
                obj.put(IMAGE, recipe.getImage());
                obj.put(COOKING_INSTR, recipe.getCookingInstructions());
                obj.put(SERVING_INSTR, recipe.getServingInstructions());

                ParseRelation<ParseObject> relation = obj.getRelation(INGREDIENTS);

                //removing previous ingredients list
                DeleteIngredientsInIngredientsRelation(relation);

                //saving the new ingredients list
                SaveIngridientsListOfRecipe(recipe, relation);

                obj.saveInBackground();
                mListener.onEditRecipeComplete(true);
            }
            else
            {
                Log.e("RecipeBook","The recipe with object id : " +
                        recipe.getObjectID() + " was not found in cloud db");
            }
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
        }
    }

    private void DeleteIngredientsInIngredientsRelation(ParseRelation<ParseObject> relation)
    {
        //get all ingredients of a recipe and delte them
        try
        {
            List<ParseObject> ingredientObjects = relation.getQuery().find();

            for (ParseObject obj : ingredientObjects)
            {
                relation.remove(obj);
            }
        }
        catch (ParseException e)
        {
            WriteParseExceptionToLog(e);
        }
    }

    public ArrayList<Recipe> GetAllUsersRecipesByLikeExp(String likeExpression, boolean findThisUserRecipes)
    {
        if(!findThisUserRecipes) {
            return GetSubListByExpression(likeExpression, allRecipes);
        }
        else
        {
            return GetSubListByExpression(likeExpression, allCurrentUserRecipes);
        }
    }

    public ArrayList<Recipe> GetUserFavoriteRecipesById(String user_Id)
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

    public void DeleteRecipe(Recipe recipe)
    {
        final Recipe thisRecipe = recipe;
        ParseQuery query = new ParseQuery("Recipe");
        query.whereEqualTo(OBJECT_ID, recipe.getObjectID());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    if (objects.size() > 1) {
                        Log.e("RecipieBook",
                                "There are more then one object with the same object id : "
                                        + thisRecipe.getObjectID());
                    }

                    objects.get(0).deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mListener.onDeleteRecipeComplete(true);
                            } else {
                                mListener.onDeleteRecipeComplete(false);
                            }
                        }
                    });
                }
            }
        });

        allCurrentUserRecipes.remove(recipe);
    }

    public void RemoveRecipeFromUserFavorites(Recipe recipe)
    {
        AddOrRemoveRecipeToFavorites(recipe, false);
    }

    public User GetCurrentUser()
    {
        if(_user == null)
            _user = Model.instance(mContext).AssignCurrentUser();
        return _user;
    }

    private void SaveCurrentUser(User user)
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

    private void SaveUserFavorites(User user, ParseRelation<ParseObject> favoritesRelation)
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

    public void AddRecipeToFavorites(Recipe recipe) {
        AddOrRemoveRecipeToFavorites(recipe,true);
    }

    public ArrayList<Recipe> GetUserFavoriteRecipesByLikeExp(String userId, String expression)
    {

        ArrayList<Recipe> favorites = GetUserFavoriteRecipesById(userId);
        // if expression is empty, return all recipes
        return GetSubListByExpression(expression, favorites);
    }

    public ArrayList<Recipe> GetSubListByExpression( String expression, ArrayList<Recipe> list)
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
