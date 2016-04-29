package com.izik.recipebook;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.Parse;

import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends FragmentActivity implements AddRecipeFragment.OnFragmentInteractionListener,
        IngredientsFragment.OnFragmentInteractionListener, RecipeViewDetailsFragment.OnFragmentInteractionListener, RecipeDescriptionTabFragment.OnFragmentInteractionListener,
        RecipeIngredientsTabFragment.OnFragmentInteractionListener, RecipeCookingInstructionsTabFragment.OnFragmentInteractionListener,
        RecipeServingInstructionsTabFragment.OnFragmentInteractionListener, BrowseRecipesFragment.OnFragmentInteractionListener, Model.OnModelCompletedOperationListener
{

    private static final int UNIQUE_MAIN_ACTIVITY_GROUP_ID = 1;
    private static final String TITLE = "RecipeBook";
    ImageAdapter imageAdapter;
    private Menu optionsMenu;
    private GridView gridview;
    private User User;
    private ProgressDialog AddOrRemoveFavoritesDialog;
    private Recipe viewedRecipe;
    private EditText input_searchMyRecipes;

    public void setViewedRecipe(Recipe viewedRecipe) {
        this.viewedRecipe = viewedRecipe;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.confirm_button).setVisible(false);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add_recipe_menu_button:
            {
               addRecipe();

                break;
            }
            case R.id.homeRecipeBook:
            {
                ReturnToMainPage();
                break;
            }
            case R.id.browseForRecipes:
            {
                BrowseRecipes();
                break;
            }
            case R.id.Favorites_menu_button:
            {
                ShowFavorites();
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        if(viewedRecipe != null)
        {
            SetViewdRecipeOpacityBack();
        }

        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Parse settings
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        AddOrRemoveFavoritesDialog = new ProgressDialog(this);
        AddOrRemoveFavoritesDialog.setCancelable(true);

        //Should happen once
        this.User = Model.instance(this).GetCurrentUser();

        PropertiesReader propertyReader = new PropertiesReader(this);
        Properties properties = propertyReader.getProperties("UserInfo.Properties");
        properties.setProperty("Phone_Number", String.valueOf(User.getId()));

        imageAdapter = new ImageAdapter(getApplication().getResources(), this);
        this.User.setUserRecipes(imageAdapter.SetRecipesImagesArrayForUser(User.getId()));

        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewedRecipe = User.getUserRecipes().get(position);
                ShowRecipeDetails(viewedRecipe);
            }
        });
        registerForContextMenu(gridview);

        input_searchMyRecipes = (EditText) findViewById(R.id.input_searchMyRecipes);

        input_searchMyRecipes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().compareTo("") == 0) {
                    SetGridView(s.toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetGridView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                SetGridView(s.toString());
            }
        });

        setBackgroundImagesOpacity();
    }

    private void SetGridView(String expression)
    {
        this.User.setUserRecipes(imageAdapter.setRecipesBy(expression, true));
        imageAdapter.notifyDataSetChanged();
    }


    private void setBackgroundImagesOpacity()
    {
        Drawable bowl_spoon_bg = getResources().getDrawable(R.drawable.bake_bg);

        // setting the opacity (alpha)
        bowl_spoon_bg.setAlpha(40);

        // setting the images on the ImageViews
        findViewById(R.id.searchMainContainer).setBackground(bowl_spoon_bg);
    }

    private void ShowRecipeDetails(Recipe recipe)
    {
        RecipeViewDetailsFragment fragment = new RecipeViewDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("IngredientsListObject", recipe.getIngrediants());
        bundle.putParcelable("RecipeObject", recipe);
        fragment.setArguments(bundle);

        ShowFragment(fragment);
        optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        optionsMenu.findItem(R.id.edit_recipe_menu_button).setVisible(true);
        optionsMenu.findItem(R.id.confirm_button).setVisible(false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.gridview)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle( User.getUserRecipes().get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.longClickRecipeMenu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(UNIQUE_MAIN_ACTIVITY_GROUP_ID, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.longClickRecipeMenu);
        String menuItemName = menuItems[menuItemIndex];

        if (item.getGroupId() == UNIQUE_MAIN_ACTIVITY_GROUP_ID) {
            if (menuItemName.compareTo("Delete") == 0) {
                DeleteRcipe(User.getUserRecipes().get(info.position), info.position);
            } else if (menuItemName.compareTo("Edit") == 0) {
                EditRecipe(User.getUserRecipes().get(info.position));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private void EditRecipe(Recipe recipe)
    {
        if (findViewById(R.id.FragmentsFrameLayer) != null)
        {
            InitializeAddRecipeFragment(recipe,recipe.getIngrediants(), USER_ACTION.EDIT);
        }
    }

    private void DeleteRcipe(Recipe recipe, int position)
    {
        User.getUserRecipes().remove(position);
        Model.instance(this).DeleteRecipe(recipe);
        imageAdapter.RefreshUserRecipesImagesList(User.getId());
        imageAdapter.notifyDataSetChanged();
    }

    private void addRecipe()
    {
        if (findViewById(R.id.FragmentsFrameLayer) != null)
        {
            Recipe mockRecipe = new Recipe("",User.getId(),"","",new ArrayList<Ingredient>(),"","","");
            ArrayList<Ingredient> mockIngredients = new ArrayList<>();
            InitializeAddRecipeFragment(mockRecipe,mockIngredients, USER_ACTION.ADD );
        }
    }

    private void InitializeAddRecipeFragment(Recipe recipe, ArrayList<Ingredient> ingredients, USER_ACTION userAction)
    {
        // write parcelable array
        AddRecipeFragment fragment = new AddRecipeFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("IngredientsListObject", ingredients);
        bundle.putParcelable("RecipeObject", recipe);
        bundle.putSerializable("USER_ACTION", userAction);
        fragment.setArguments(bundle);

        ShowFragment(fragment);

    }

    private void ShowFragment(android.support.v4.app.Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentsFrameLayer, fragment).commit();

        findViewById(R.id.searchMainContainer).setVisibility(View.INVISIBLE);
        findViewById(R.id.FragmentsFrameLayer).setVisibility(View.VISIBLE);
    }

    private void ReturnToMainPage()
    {
        findViewById(R.id.FragmentsFrameLayer).setVisibility(View.INVISIBLE);
        findViewById(R.id.searchMainContainer).setVisibility(View.VISIBLE);
        optionsMenu.findItem(R.id.confirm_button).setVisible(false);
        optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        SetTitleBack();
    }

    private void SetViewdRecipeOpacityBack()
    {
        int picId = getResources().getIdentifier(viewedRecipe.getImage(), "drawable", "com.izik.recipebook");
        Drawable recipePicture = getResources().getDrawable(picId);
        recipePicture.setAlpha(255);
        viewedRecipe = null;
    }

    private void ShowFavorites()
    {
        FavoritesFragment fragment = new FavoritesFragment(User.getId());
        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private void BrowseRecipes()
    {
        BrowseRecipesFragment fragment = new BrowseRecipesFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("User_ID",User.getObjectId());
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Recipe recipe, USER_ACTION action)
    {
        if(action == USER_ACTION.ADD)
        {
            User.getUserRecipes().add(recipe);
            Model.instance(this).AddRecipe(recipe);

        }
        if(action == USER_ACTION.EDIT)
        {
            Model.instance(this).EditRecipe(recipe);
        }

        imageAdapter.RefreshUserRecipesImagesList(User.getId());
        imageAdapter.notifyDataSetChanged();
        optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        optionsMenu.findItem(R.id.confirm_button).setVisible(false);
        findViewById(R.id.FragmentsFrameLayer).setVisibility(View.INVISIBLE);
        findViewById(R.id.gridview).setVisibility(View.VISIBLE);
        SetTitleBack();

    }

    private void SetTitleBack() {
        setTitle(TITLE);
    }

    @Override
    public void onIngredientFragmentInteraction(Ingredient ingredient)
    {
        if( ingredient == null) {
            Toast.makeText(MainActivity.this, "" + "ingredient is null",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "" + "ingredient is not null",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecipeDetailsFragmentInteraction(Recipe recipe)
    {

    }

    @Override
    public void onRecipeDescriptionFragmentInteraction(Recipe recipe)
    {

    }

    @Override
    public void onRecipeIngredientsTabFragmentInteraction(Recipe recipe) {

    }

    @Override
    public void onRecipeCookingInstructionsTabFragmentInteraction(Recipe recipe) {

    }

    @Override
    public void onRecipeServingInstructionsTabFragmentInteraction(Recipe recipe) {

    }

    @Override
    public void onBrowseRecipeFragmentInteraction(Recipe recipe)
    {

    }

    @Override
    public void onAddRecipeComplete(boolean success)
    {

    }

    @Override
    public void onAddIngredientToRecipeComplete(boolean b, String s) {

    }

    @Override
    public void onDeleteRecipeComplete(boolean b) {

    }

    @Override
    public void onEditRecipeComplete(boolean b) {

    }

    @Override
    public void onaddOrRemoveFavoritesFail(Recipe recipe, boolean isAddOperation)
    {
        if(isAddOperation)
        {
            AddOrRemoveFavoritesDialog.setMessage("Adding to favorites failed, try again.");
        }
        else
        {
            AddOrRemoveFavoritesDialog.setMessage("Removing from favorites failed, try again.");
        }

        AddOrRemoveFavoritesDialog.show();
    }

    @Override
    public void onaddOrRemoveFavoritesSuccess(Recipe recipe, boolean isAddOperation)
    {
        if(isAddOperation)
        {
            AddOrRemoveFavoritesDialog.setMessage("Adding to favorites completed.");
        }
        else
        {
            AddOrRemoveFavoritesDialog.setMessage("Removing from favorites completed.");
        }

        AddOrRemoveFavoritesDialog.show();
    }
}
