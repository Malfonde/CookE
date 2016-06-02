package com.izik.recipebook;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.izik.recipebook.DAL.NewModel;
import com.izik.recipebook.ServerSideHandlers.BROWSE_RECIPE_SPESIFY;

import java.util.ArrayList;

;

public class BrowseRecipesFragment extends Fragment
{
    private static final int UNIQUE_BROWSE_FRAGMENT_GROUP_ID = 2;
    private static final String TITLE = "Browsing";
    private OnFragmentInteractionListener mListener;
    private ArrayList<Recipe> allRecipes;
    private GridView gridview;
    private ImageAdapter imageAdapter;
    private RecipeViewDetailsFragment fragment;
    private EditText input_browseRecipes;
    ProgressDialog AddOrRemoveFavoritesDialog;
    private String UserID;
    private BROWSE_RECIPE_SPESIFY BrowseSpesification;

    public BrowseRecipesFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(TITLE);
        AddOrRemoveFavoritesDialog = new ProgressDialog(getContext());
        AddOrRemoveFavoritesDialog.setCancelable(true);
        UserID = getArguments().getString("User_ID");
        BrowseSpesification =  BROWSE_RECIPE_SPESIFY.valueOf(getArguments().getString("BrowseRecipesSpecify"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_recipes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        imageAdapter = new ImageAdapter(getActivity().getResources(), getContext());
        input_browseRecipes = (EditText) view.findViewById(R.id.input_browseRecipes);

        //setting the recipes to show
        switch (BrowseSpesification)
        {
            case SUGGESTED_RECIPES:
                allRecipes = imageAdapter.setRecommandedRecipes();
                break;
            case ALL_BUT_GIVEN_USER:
                allRecipes = imageAdapter.setAllRecipesWhoDoesntBelongToThisUser(UserID);
                break;
            default:
                allRecipes = imageAdapter.setRecipesFromAllUsers();
                break;
        }

        gridview = (GridView) view.findViewById(R.id.AllBrowsedRecipesGridView);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowRecipeDetails(allRecipes.get(position), false);
            }
        });
      //  registerForContextMenu(gridview);

        input_browseRecipes.addTextChangedListener(new TextWatcher() {
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

        registerForContextMenu(gridview);
        setBackgroundImagesOpacity();
    }

    private void setBackgroundImagesOpacity()
    {
        Drawable golden_hearts_bg = getResources().getDrawable(R.drawable.search_food_bg);

        // setting the opacity (alpha)
        golden_hearts_bg.setAlpha(40);

        // setting the images on the ImageViews
        getView().findViewById(R.id.BrowseGridViewContainer).setBackground(golden_hearts_bg);
    }

    private void SetGridView(String expression)
    {
        allRecipes = imageAdapter.setRecipesBy(expression, false,"");
        imageAdapter.notifyDataSetChanged();
    }


    private void ShowRecipeDetails(Recipe recipe, boolean withEditPermission)
    {
        fragment = new RecipeViewDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("IngredientsListObject", recipe.getIngrediants());
        bundle.putParcelable("RecipeObject", recipe);
        bundle.putBoolean("withEditPermission", withEditPermission);
        fragment.setArguments(bundle);

        ShowFragment(fragment);

        ((MainActivity)getActivity()).setViewedRecipe(recipe);
    }

    private void ShowFragment(android.support.v4.app.Fragment fragment)
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.BrowseRecipesFragmentsFrameLayer, fragment).addToBackStack("nestedFragmentInBrowseFragment").commit();

        getView().findViewById(R.id.BrowseGridViewContainer).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.BrowseRecipesFragmentsFrameLayer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.AllBrowsedRecipesGridView)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(allRecipes.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.longClickBrowseRecipesMenu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(UNIQUE_BROWSE_FRAGMENT_GROUP_ID, i, i, menuItems[i]);
            }     
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.longClickBrowseRecipesMenu);
        String menuItemName = menuItems[menuItemIndex];

        if (item.getGroupId() == UNIQUE_BROWSE_FRAGMENT_GROUP_ID) {
            if (menuItemName.compareTo("Add/Remove to favorites") == 0) {
                AddOrRemoveFromFavorites(allRecipes.get(info.position), info.position);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private void AddOrRemoveFromFavorites(Recipe recipe, int position)
    {
        //ArrayList<Recipe> favorites = Model.instance(getContext()).GetUserFavoriteRecipesById(UserID);
        ArrayList<Recipe> favorites = NewModel.instance().GetUserFavoriteRecipes(UserID);


        ArrayList objIds = new ArrayList();
        for (Recipe favorite : favorites)
        {
            objIds.add(favorite.getObjectID());
        }

        if(objIds.contains(recipe.getObjectID()))
        {
            NewModel.instance().AddOrRemoveToFavorites(recipe,getContext() , false);
           // Model.instance(getContext()).RemoveRecipeFromUserFavorites(recipe);
        }
        else
        {
            NewModel.instance().AddOrRemoveToFavorites(recipe,getContext(), true);
            //Model.instance(getContext()).AddRecipeToFavorites(recipe);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if(fragment != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    public interface OnFragmentInteractionListener {

        void onBrowseRecipeFragmentInteraction(Recipe recipe);
    }
}
