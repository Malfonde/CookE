package com.izik.recipebook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class RecipeDetailsTabsPagerAdapter extends FragmentPagerAdapter
{
    private static final int NUM_OF_TABS = 4;
    private final Recipe recipe;
    private final FragmentManager fragMan;
    private ArrayList<Fragment> fragments =new ArrayList<Fragment>();

    public RecipeDetailsTabsPagerAdapter(FragmentManager fragmentManager, Recipe recipe)
    {
        super(fragmentManager);
        this.recipe = recipe;
        fragMan = fragmentManager;
    }
    public void clearAll() //Clear all page
    {
        for (int i = 0; i < fragments.size(); i++) {
            fragMan.beginTransaction().remove(fragments.get(i)).commit();
        }
            fragments.clear();
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0: {

                RecipeDescriptionTabFragment fragment = new RecipeDescriptionTabFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelable("RecipeObject", recipe);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                return fragment;
            }
            case 1:
            {
                RecipeIngredientsTabFragment fragment = new RecipeIngredientsTabFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("IngredientsListObject", recipe.getIngrediants());
                fragment.setArguments(bundle);
                fragments.add(fragment);
                return fragment;
            }
            case 2:
            {
                RecipeCookingInstructionsTabFragment fragment = new RecipeCookingInstructionsTabFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelable("RecipeObject", recipe);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                return fragment;
            }
            case 3:
            {
                RecipeServingInstructionsTabFragment fragment = new RecipeServingInstructionsTabFragment();
                final Bundle bundle = new Bundle();
                bundle.putParcelable("RecipeObject", recipe);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                return fragment;
            }
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object)
    {

        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return NUM_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
            {
               return "Description";
            }
            case 1:
            {
                return "Ingredients";
            }
            case 2:
            {
                return "Cooking Instructions";
            }
            case 3:
            {
                return "How to serve?";
            }
            default:
                return null;
        }
    }
}
