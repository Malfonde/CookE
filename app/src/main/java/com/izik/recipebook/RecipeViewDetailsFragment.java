package com.izik.recipebook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class RecipeViewDetailsFragment extends Fragment {



    private OnFragmentInteractionListener mListener;
    private Recipe recipe;
    //private TextView recipeName;
    //private ImageButton recipeImage;
    private ViewPager pager;
    private RecipeDetailsTabsPagerAdapter recipeDetailsTabsAdapter;
    private PagerTabStrip pager_header;
    private boolean withEditPermission;

    public RecipeViewDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Recipe recipe = getArguments().getParcelable("RecipeObject");
        this.recipe = recipe;
        this.withEditPermission = getArguments().getBoolean("withEditPermission");
        setHasOptionsMenu(true);
        getActivity().setTitle(recipe.getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.findItem(R.id.confirm_button).setVisible(false);
        menu.findItem(R.id.add_recipe_menu_button).setVisible(true);
        menu.findItem(R.id.edit_recipe_menu_button).setVisible(withEditPermission);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
      //  recipeName = (TextView) view.findViewById(R.id.viewRecipeName);
       // recipeImage = (ImageButton) view.findViewById(R.id.viewRecipeImage);
       // recipeImage.setImageResource(getResources().getIdentifier(recipe.getImage(), "drawable", "com.izik.recipebook"));
      //  recipeName.setText(recipe.getName());
        pager = (ViewPager) view.findViewById(R.id.recipeDetailsTabsPager);





        recipeDetailsTabsAdapter = new RecipeDetailsTabsPagerAdapter(getActivity().getSupportFragmentManager(),recipe);
        pager.setAdapter(recipeDetailsTabsAdapter);
        setBackgroundImagesOpacity(40);
    }

    private void setBackgroundImagesOpacity(int opacity)
    {
        int picId = getResources().getIdentifier(recipe.getImage(), "drawable", "com.izik.recipebook");
        Drawable recipePicture = getResources().getDrawable(picId);

        // setting the opacity (alpha)
        recipePicture.setAlpha(opacity);

        // setting the images on the ImageViews
        getView().findViewById(R.id.RecipeDetailsContainer).setBackground(recipePicture);
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
        recipeDetailsTabsAdapter.clearAll();
    }

    public interface OnFragmentInteractionListener {

        void onRecipeDetailsFragmentInteraction(Recipe recipe);
    }
}
