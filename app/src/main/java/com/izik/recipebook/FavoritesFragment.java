package com.izik.recipebook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements BackButton
{

    private static final String TITLE = "Favorties";
    private ImageAdapter imageAdapter;
    private EditText input_browseFavortieRecipes;
    private GridView gridview;
    private String userId;
    private ArrayList<Recipe> favorites;
    private RecipeViewDetailsFragment fragment;


    public FavoritesFragment(String UserId)
    {
        userId = UserId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(TITLE);
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
        input_browseFavortieRecipes = (EditText) view.findViewById(R.id.input_browseRecipes);
        favorites = imageAdapter.setFavoriteRecipesByUserId(userId);
        gridview = (GridView) view.findViewById(R.id.AllBrowsedRecipesGridView);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowRecipeDetails(favorites.get(position));
            }
        });
        registerForContextMenu(gridview);

        input_browseFavortieRecipes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetGridView(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        registerForContextMenu(gridview);

    }

    private void SetGridView(String expression)
    {
        favorites = imageAdapter.setUserFavoriteRecipesByExpression(userId, expression);
        imageAdapter.notifyDataSetChanged();
    }

    private void ShowRecipeDetails(Recipe recipe)
    {
        fragment = new RecipeViewDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("IngredientsListObject", recipe.getIngrediants());
        bundle.putParcelable("RecipeObject", recipe);
        bundle.putBoolean("withEditPermission", false);
        fragment.setArguments(bundle);

        ShowFragment(fragment);
        ((MainActivity)getActivity()).setViewedRecipe(recipe);
    }

    private void ShowFragment(android.support.v4.app.Fragment fragment)
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.BrowseRecipesFragmentsFrameLayer, fragment).addToBackStack("nestedFragmentInFavoritesFragment").commit();

        getView().findViewById(R.id.BrowseGridViewContainer).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.BrowseRecipesFragmentsFrameLayer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(fragment != null)
        {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }


    @Override
    public void onFragmentResume()
    {

        getView().findViewById(R.id.BrowseGridViewContainer).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.BrowseRecipesFragmentsFrameLayer).setVisibility(View.INVISIBLE);
        getActivity().setTitle(TITLE);
        ((MainActivity)getActivity()).SetViewdRecipeOpacityBack();

    }
}
