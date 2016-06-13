package com.izik.recipebook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.izik.recipebook.ServerSideHandlers.ABOUT_HELP;
import com.izik.recipebook.ServerSideHandlers.BROWSE_RECIPE_SPESIFY;

public class AboutHelpFragment extends Fragment {

    private static String TITLE = "";
    private ABOUT_HELP AboutHelp;

    public AboutHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AboutHelp =  ABOUT_HELP.valueOf(getArguments().getString("AboutHelp"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        switch (AboutHelp)
        {
            case ABOUT:
                getActivity().setTitle("About");
                aboutData();
                break;
            case HELP:
                getActivity().setTitle("Help");
                helpData();
                break;
            default:
                break;
        }
    }

    private void aboutData(){
        getView().findViewById(R.id.about_text).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.help_text).setVisibility(View.INVISIBLE);
    }

    private void helpData(){
        getView().findViewById(R.id.about_text).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.help_text).setVisibility(View.VISIBLE);
    }
}
