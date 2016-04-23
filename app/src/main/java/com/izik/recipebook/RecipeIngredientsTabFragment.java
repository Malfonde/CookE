package com.izik.recipebook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class RecipeIngredientsTabFragment extends Fragment
{

    private OnFragmentInteractionListener mListener;
    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private Ingrediants_View_Adapter ingrediants_adapter;
    private ListView ingredients_list;

    public RecipeIngredientsTabFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Recipe recipe = getArguments().getParcelable("RecipeObject");
        ArrayList<Ingredient> ingredients = getArguments().getParcelableArrayList("IngredientsListObject");
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_ingredients_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ingredients_list = (ListView) view.findViewById(R.id.ingredientsTabInDetails);
        ingrediants_adapter = new Ingrediants_View_Adapter(this.getContext(),ingredients);
        ingredients_list.setAdapter(ingrediants_adapter);
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
    }

    public interface OnFragmentInteractionListener {

        void onRecipeIngredientsTabFragmentInteraction(Recipe recipe);
    }
}
