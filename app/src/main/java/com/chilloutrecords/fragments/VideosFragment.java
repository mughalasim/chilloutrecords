package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.adapters.VideoListingAdapter;
import com.chilloutrecords.interfaces.VideoListingInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_POINTS;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_VIDEO;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;
import static com.chilloutrecords.utils.StaticVariables.VIDEO_MODEL;

public class VideosFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private VideoListingAdapter adapter;
    private ArrayList<VideoModel> models = new ArrayList<>();
    private TextView txt_no_results;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);
                recycler_view = root_view.findViewById(R.id.recycler_view);
                txt_no_results = root_view.findViewById(R.id.txt_no_results);

                fetchVideos();

                recycler_view.setTextView(txt_no_results, getString(R.string.progress_loading));
                recycler_view.setHasFixedSize(true);
                recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new VideoListingAdapter(getActivity(), models, new VideoListingInterface() {
                    @Override
                    public void clicked(VideoModel model, String page_title) {
                        if (USER_MODEL.points > BuildConfig.POINTS_FEE_PLAY) {
                            Database.updateProfilePoints(-BuildConfig.POINTS_FEE_PLAY);
                            VIDEO_MODEL = model;
                            ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new PlayerFragment(), page_title, EXTRA_VIDEO, null, true));
                        } else {
                            ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new PointsFragment(), PAGE_TITLE_POINTS, "", null, true));
                        }
                    }
                });

                recycler_view.setAdapter(adapter);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    // CLASS METHODS ===============================================================================
    private void fetchVideos() {
        FIREBASE_DB.getReference().child(BuildConfig.DB_REF_VIDEOS).orderByChild("is_live").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    VideoModel video = child.getValue(VideoModel.class);
                    assert video != null;
                    models.add(video);
                }

                if (models.size() < 1) {
                    recycler_view.setTextView(txt_no_results, getString(R.string.error_no_videos));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
                recycler_view.setTextView(txt_no_results, getString(R.string.error_500));
            }
        });
    }

}
