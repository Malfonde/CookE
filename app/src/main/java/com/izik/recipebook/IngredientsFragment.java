package com.izik.recipebook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


public class IngredientsFragment extends Fragment
{
    private ImageButton addIngredient;
    private ListView ingredients_list;
    private OnFragmentInteractionListener mListener;
    private Ingrediants_Adapter ingrediants_adapter;
    private ArrayList<Ingredient> origionalIngredients;
    private ArrayList<Ingredient> tempIngredients = new ArrayList<>();
    private ImageButton removeIngredient;

    public ArrayList<Ingredient> getTemporaryIngredients()
    {
        return tempIngredients;
    }

    public IngredientsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        origionalIngredients = getArguments().getParcelableArrayList("IngredientsListObject");

        for (Ingredient ingredient : origionalIngredients)
        {
            tempIngredients.add(ingredient);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        addIngredient = (ImageButton) view.findViewById(R.id.addToIngrediantsList_Button);
        removeIngredient = (ImageButton) view.findViewById(R.id.removeFromIngrediantsList_Button);
        ingredients_list = (ListView) view.findViewById(R.id.ingredients_list);
        ingrediants_adapter = new Ingrediants_Adapter(this.getContext(), tempIngredients);
        ingredients_list.setAdapter(ingrediants_adapter);

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient(v);
            }
        });

        removeIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeIngredient(v);
            }
        });

        setListViewHeightBasedOnChildren(ingredients_list);
        setIngredientRemoveButtonVisibility();
    }

    private void addIngredient(View view)
    {
        Ingredient ingredient = new Ingredient();
        tempIngredients.add(ingredient);
        ingredients_list.refreshDrawableState();
        ingrediants_adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(ingredients_list);
        setIngredientRemoveButtonVisibility();
    }

    private void setIngredientRemoveButtonVisibility()
    {
        int size = tempIngredients.size();
        if(size > 0)
        {
            removeIngredient.setVisibility(View.VISIBLE);
        }
        else
            removeIngredient.setVisibility(View.INVISIBLE);

    }

    private void removeIngredient(View view)
    {
        int size = tempIngredients.size();
        if(size > 0) {
            tempIngredients.remove(size - 1);
            ingredients_list.refreshDrawableState();
            ingrediants_adapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(ingredients_list);
        }

        setIngredientRemoveButtonVisibility();
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        Ingrediants_Adapter listAdapter = (Ingrediants_Adapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onIngredientFragmentInteraction(Ingredient ingredient);
    }
}
