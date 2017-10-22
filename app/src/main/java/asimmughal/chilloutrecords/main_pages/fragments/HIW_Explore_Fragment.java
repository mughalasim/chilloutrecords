package asimmughal.chilloutrecords.main_pages.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;

public class HIW_Explore_Fragment extends Fragment {
    private static View rootview;
    Helpers helper;
    TextView description,
            button;
    ImageView image;

    public HIW_Explore_Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview != null) {
            ViewGroup parent = (ViewGroup) rootview.getParent();
            if (parent != null)
                parent.removeView(rootview);
        }
        try {
            rootview = inflater.inflate(R.layout.fragment_hiw, container, false);
            helper = new Helpers(getActivity());

            button = (TextView) rootview.findViewById(R.id.button);
            description = (TextView) rootview.findViewById(R.id.description);
            image = (ImageView) rootview.findViewById(R.id.image);

            button.setText("EXPLORE RESTAURANTS");
            description.setText("Explore Restaurants.\nIn the mood for Sushi?\nLooking for something romantic?\nWhatever you fancy, use the EatOut to find incredible restaurants in your area");
            image.setImageResource(R.drawable.hiw_explore);


        } catch (InflateException e) {
            e.printStackTrace();
        }
        return rootview;
    }



}