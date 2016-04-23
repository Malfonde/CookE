package com.izik.recipebook;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class Ingrediants_Adapter extends  BaseAdapter
{
    private final Context context;
    private ArrayList<Ingredient> Ingredients;

    public Ingrediants_Adapter(Context context,ArrayList<Ingredient> ingredientsquantities)
    {
        this.context = context;
        this.Ingredients = ingredientsquantities;
    }

    @Override
    public int getCount()
    {
        return Ingredients.size();
    }

    @Override
    public Object getItem(int position)
    {
        return Ingredients.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.edit_quantitiy_list_row, parent, false);
            EditText ingredient_input = (EditText) rowView.findViewById(R.id.ingerdient_input);
            EditText quantity_input = (EditText) rowView.findViewById(R.id.quantity_input);


        ingredient_input.setText(Ingredients.get(position).getName());
        quantity_input.setText(String.valueOf(Ingredients.get(position).getQuantity()));

        ingredient_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Ingredients.get(position).setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ingredients.get(position).setName(s.toString());
            }
        });

        quantity_input.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                try {

                    double quantity = Double.parseDouble(s.toString());
                    Ingredients.get(position).setQuantity(quantity);
                } catch (NumberFormatException e) {
                    Ingredients.get(position).setQuantity(0.0);
                }
            }
        });




        return rowView;
    }

}
