package com.izik.recipebook;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.izik.recipebook.DAL.NewModel;
import com.izik.recipebook.ServerSideHandlers.ABOUT_HELP;
import com.izik.recipebook.ServerSideHandlers.BROWSE_RECIPE_SPESIFY;
import java.util.ArrayList;
import java.util.Properties;



public class MainActivity extends AppCompatActivity implements AddRecipeFragment.OnFragmentInteractionListener,
        IngredientsFragment.OnFragmentInteractionListener, RecipeViewDetailsFragment.OnFragmentInteractionListener, RecipeDescriptionTabFragment.OnFragmentInteractionListener,
        RecipeIngredientsTabFragment.OnFragmentInteractionListener, RecipeCookingInstructionsTabFragment.OnFragmentInteractionListener,
        RecipeServingInstructionsTabFragment.OnFragmentInteractionListener, BrowseRecipesFragment.OnFragmentInteractionListener, Model.OnModelCompletedOperationListener
{

    private static final int UNIQUE_MAIN_ACTIVITY_GROUP_ID = 1;
    private static final String TITLE = "CookE";
    ImageAdapter imageAdapter;
    private Menu optionsMenu;
    private GridView gridview;
    private User User;
    private ProgressDialog AddOrRemoveFavoritesDialog;
    private Recipe viewedRecipe;
    private EditText input_searchMyRecipes;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

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
        //clear the backstack to avoid errors and correct back button functionality
        ClearBackStack();

        switch (item.getItemId())
        {
            case android.R.id.home: {
                mDrawer.openDrawer(GravityCompat.START);
                //return true;
                break;
            }
            case R.id.add_recipe_menu_button:
            {
               addRecipe();
                break;
            }
            case R.id.edit_recipe_menu_button:
            {
                EditRecipe(this.viewedRecipe);
                break;
            }
            /*case R.id.homeRecipeBook:
            {
                ReturnToMainPage();
                break;
            }*/
//            case R.id.suggestRecipes:
//            {
//                SuggestRecipes();
//                break;
//            }
            /*case R.id.Favorites_menu_button:
            {
                ShowFavorites();
                break;
            }
            case R.id.browseAllOthersRecipes:
            {
                BrowseOthersRecipes();
                break;
            }*/
            default:
                ReturnToMainPage();
                break;
        }

        // if we viewd a recipeDetails fragment, we need to change the picture's opacity back
        if(viewedRecipe != null)
        {
            SetViewdRecipeOpacityBack();

        }

        /*if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    private void ClearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Drawer settings
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Find our drawer view
//      mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.setDrawerListener(drawerToggle);

        AddOrRemoveFavoritesDialog = new ProgressDialog(this);
        AddOrRemoveFavoritesDialog.setCancelable(true);

        //Should happen once
        this.User = NewModel.instance().GetCurrentUser(this);

        //this.User = Model.instance(this).GetCurrentUser();

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

        //setBackgroundImagesOpacity();
    }

    // Drawer stuff
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        //Fragment fragment = null;
        //Class fragmentClass;
       switch(menuItem.getItemId())
       {
           case R.id.nav_login_button:
               LoginToFB();
               break;
           case R.id.nav_suggestions:
               SuggestRecipes();
               break;
           case R.id.nav_my_recipes:
               ReturnToMainPage();
               break;
            case R.id.nav_add_recipe:
                addRecipe();
                break;
            case R.id.nav_browse:
                BrowseOthersRecipes();
                break;
            case R.id.nav_favorites:
                ShowFavorites();
                break;
           case R.id.nav_help:
               ShowHelp();
               break;
           case R.id.nav_about:
               ShowAbout();
               break;
            default:
                ReturnToMainPage();
                break;
        }

        if (viewedRecipe != null)
        {
            SetViewdRecipeOpacityBack();
        }
        /*try {
            fragment = (Fragment)fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Insert the fragment by replacing any existing fragment
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();*/

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void LoginToFB()
    {
        facebook_login fragment = new facebook_login();
        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
    // end drawer stuff


    private void SetGridView(String expression)
    {
        this.User.setUserRecipes(imageAdapter.setRecipesBy(expression, true, this.User.getId()));
        imageAdapter.notifyDataSetChanged();
    }


    /*private void setBackgroundImagesOpacity()
    {
        Drawable bowl_spoon_bg = getResources().getDrawable(R.drawable.bake_bg);

        // setting the opacity (alpha)
        bowl_spoon_bg.setAlpha(40);

        // setting the images on the ImageViews
        //findViewById(R.id.searchMainContainer).setBackground(bowl_spoon_bg);
    }*/

    private void ShowRecipeDetails(Recipe recipe)
    {
        gridview.setVisibility(View.INVISIBLE);
        RecipeViewDetailsFragment fragment = new RecipeViewDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("IngredientsListObject", recipe.getIngrediants());
        bundle.putParcelable("RecipeObject", recipe);
        bundle.putBoolean("withEditPermission", true);
        fragment.setArguments(bundle);

        ShowFragment(fragment);
        //optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
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
                DeleteRecipe(User.getUserRecipes().get(info.position), info.position);
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

    private void DeleteRecipe(Recipe recipe, int position)
    {
        User.getUserRecipes().remove(position);
        //Model.instance(this).DeleteRecipe(recipe);
        NewModel.instance().DeleteRecipe(recipe);
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
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentsFrameLayer, fragment).addToBackStack("currentShowedFragment").commit();*/

        //findViewById(R.id.searchMainContainer).setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.FragmentsFrameLayer, fragment).commit();

        findViewById(R.id.FragmentsFrameLayer).setVisibility(View.VISIBLE);
    }

    private void ReturnToMainPage()
    {
        findViewById(R.id.FragmentsFrameLayer).setVisibility(View.INVISIBLE);
        //findViewById(R.id.searchMainContainer).setVisibility(View.VISIBLE);
        optionsMenu.findItem(R.id.confirm_button).setVisible(false);
        optionsMenu.findItem(R.id.edit_recipe_menu_button).setVisible(false);
        optionsMenu.findItem(R.id.delete_recipe_menu_button).setVisible(false);
        //optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        SetTitleBack();
    }

    public void SetViewdRecipeOpacityBack()
    {
        gridview.setVisibility(View.VISIBLE);
        int picId = getResources().getIdentifier(viewedRecipe.getImage(), "drawable", "com.izik.recipebook");
        Drawable recipePicture = getResources().getDrawable(picId);
        recipePicture.setAlpha(255);
        viewedRecipe = null;
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            //if showRecipeDetails
            if(viewedRecipe != null)
            {
                //if we showed recipe from myRecipes
                if(count == 1) {
                    SetViewdRecipeOpacityBack();
                    ReturnToMainPage();
                }
                //if we showed from Favorites Or Browse Or Suggest
                else
                {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.FragmentsFrameLayer);
                    // checking which fragment is it
                    try {
                        if (fragment instanceof BackButton) {
                            ((BackButton) fragment).onFragmentResume();
                        }
                    }
                    catch(Exception e)
                    {

                    }
                }
            }

            getSupportFragmentManager().popBackStackImmediate();
        }

    }

    private void ShowFavorites()
    {
        FavoritesFragment fragment = new FavoritesFragment(User.getId());
        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private void BrowseOthersRecipes()
    {
        BrowseRecipesFragment fragment = new BrowseRecipesFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("User_ID", User.getId());
        bundle.putString("BrowseRecipesSpecify", BROWSE_RECIPE_SPESIFY.ALL_BUT_GIVEN_USER.toString());
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private void ShowHelp()
    {
        AboutHelpFragment fragment = new AboutHelpFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("AboutHelp", ABOUT_HELP.HELP.toString());
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private void ShowAbout()
    {
        AboutHelpFragment fragment = new AboutHelpFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("AboutHelp", ABOUT_HELP.ABOUT.toString());
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    private void SuggestRecipes()
    {
        BrowseRecipesFragment fragment = new BrowseRecipesFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("User_ID", User.getId());
        bundle.putString("BrowseRecipesSpecify", BROWSE_RECIPE_SPESIFY.SUGGESTED_RECIPES.toString());
        fragment.setArguments(bundle);
        ShowFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Recipe recipe, USER_ACTION action)
    {
        //TODO: shouldn't this be a switch case?
        if(action == USER_ACTION.ADD)
        {
            //Model.instance(this).AddRecipe(recipe);
            User.getUserRecipes().add(NewModel.instance().AddRecipe(recipe));
        }
        if(action == USER_ACTION.EDIT)
        {
            NewModel.instance().EditRecipe(recipe);
        }

        if(action == USER_ACTION.DELETE)
        {
            User.getUserRecipes().remove(recipe);
           // Model.instance(this).DeleteRecipe(recipe); // we need to put newmodel here to delete from db
            NewModel.instance().DeleteRecipe(recipe);
        }

        if(action == USER_ACTION.CANCEL)
        {
            // DO NOTHING...
        }

        imageAdapter.RefreshUserRecipesImagesList(User.getId());
        imageAdapter.notifyDataSetChanged();
        //optionsMenu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        optionsMenu.findItem(R.id.confirm_button).setVisible(false);
        optionsMenu.findItem(R.id.edit_recipe_menu_button).setVisible(false);
        optionsMenu.findItem(R.id.delete_recipe_menu_button).setVisible(false);
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
        ReturnToMainPage();
    }

    @Override
    public void onAddIngredientToRecipeComplete(boolean b, String s) {

    }

    @Override
    public void onDeleteRecipeComplete(boolean b) {
        ReturnToMainPage();
    }

    @Override
    public void onEditRecipeComplete(boolean b) {
        ReturnToMainPage();
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
