package com.izik.recipebook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RecipeCookingInstructionsTabFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    private Recipe recipe;
    private TextView cookingInstructions;

    public RecipeCookingInstructionsTabFragment()
    {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Recipe recipe = getArguments().getParcelable("RecipeObject");
        this.recipe = recipe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_recipe_description, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        cookingInstructions = (TextView) view.findViewById(R.id.recipeTabTextView);
        cookingInstructions.setText(recipe.getCookingInstructions());
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

    public interface OnFragmentInteractionListener
    {
        void onRecipeCookingInstructionsTabFragmentInteraction(Recipe recipe);
    }
}
