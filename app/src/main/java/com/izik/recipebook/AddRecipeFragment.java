package com.izik.recipebook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class AddRecipeFragment extends Fragment
{

    private static final String ADD_TITLE = "Add a recipe";
    private static final String EDIT_TITLE = "Editing";
    private static int RESULT_LOAD_IMAGE = 1;
    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private OnFragmentInteractionListener mListener;
    private ImageButton imageInput;
    private EditText nameInput;
    private EditText descriptionInput;
    private EditText cookingInstructionInput;
    private EditText servingInstructionsInput;
    private String picturePathTag = "@drawable/defauld_recipe_pic";
    private USER_ACTION userAction;


    public AddRecipeFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Recipe recipe = getArguments().getParcelable("RecipeObject");
        ArrayList<Ingredient> ingredients = getArguments().getParcelableArrayList("IngredientsListObject");
        userAction = (USER_ACTION) getArguments().getSerializable("USER_ACTION");
        this.ingredients = ingredients;
        this.recipe = recipe;
        setHasOptionsMenu(true);
        SetTitle();
    }

    private void SetTitle() {
        switch (userAction)
        {
            case ADD:
            {
                getActivity().setTitle(ADD_TITLE);
                break;
            }
            case EDIT:
            {
                getActivity().setTitle(EDIT_TITLE);
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        imageInput = (ImageButton) view.findViewById(R.id.PickRecipePicture);
        nameInput = (EditText) view.findViewById(R.id.input_name);
        descriptionInput = (EditText) view.findViewById(R.id.input_description);
        cookingInstructionInput = (EditText) view.findViewById(R.id.input_CookingInstructions);
        servingInstructionsInput = (EditText) view.findViewById(R.id.input_ServingInstructions);
        imageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoseImage();
            }
        });

        int resourceId = getActivity().getResources().getIdentifier(recipe.getImage(), "drawable", "com.izik.recipebook");
        imageInput.setImageResource(resourceId);
        imageInput.setMaxWidth(80);
        imageInput.setMaxHeight(100);
        nameInput.setText(recipe.getName());
        descriptionInput.setText(recipe.getDescription());
        cookingInstructionInput.setText(recipe.getCookingInstructions());
        servingInstructionsInput.setText(recipe.getServingInstructions());

       if (view.findViewById(R.id.ingredientsFragmentFrameLayer) != null)
        {
            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            final Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("IngredientsListObject", ingredients);
            ingredientsFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ingredientsFragmentFrameLayer, ingredientsFragment).commit();
        }

        setBackgroundImagesOpacity(view);
    }

    private void setBackgroundImagesOpacity(View view)
    {
        Drawable bowl_spoon_bg = getResources().getDrawable(R.drawable.bake_bg);

        // setting the opacity (alpha)
        bowl_spoon_bg.setAlpha(40);

        // setting the images on the ImageViews
        view.findViewById(R.id.bowl_spoon_bg_container).setBackground(bowl_spoon_bg);
    }

    private void ChoseImage()
    {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageInput.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            picturePathTag = picturePath;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.findItem(R.id.confirm_button).setVisible(true);
        menu.findItem(R.id.add_recipe_menu_button).setVisible(false);
        menu.findItem(R.id.delete_recipe_menu_button).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.confirm_button:
            {
                SaveRecipe();
                break;
            }
            case R.id.delete_recipe_menu_button: {
                if (userAction == USER_ACTION.EDIT)
                {
                    mListener.onFragmentInteraction(recipe, USER_ACTION.DELETE);
                }
                else
                {
                    //TODO: cancel add operation - back to main menu
                }
                break;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void SaveRecipe()
    {
        recipe.setName(this.nameInput.getText().toString());
        recipe.setDescription(this.descriptionInput.getText().toString());

        recipe.setImage(picturePathTag);
        recipe.setCookingInstructions(this.cookingInstructionInput.getText().toString());
        recipe.setServingInstructions(this.servingInstructionsInput.getText().toString());
        ArrayList<Ingredient> newIngredients = ((IngredientsFragment)  getFragmentManager().findFragmentById(R.id.ingredientsFragmentFrameLayer)).getTemporaryIngredients();
        recipe.setIngredients(newIngredients);

        switch (userAction) {
            case ADD:
            {
                mListener.onFragmentInteraction(recipe, USER_ACTION.ADD);
                break;
            }
            case EDIT:
            {
                mListener.onFragmentInteraction(recipe, USER_ACTION.EDIT);
                break;
            }
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
    }



    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Recipe recipe, USER_ACTION action);
    }
}