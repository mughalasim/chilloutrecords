package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_PROFILE_EDIT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_SHARE;
import static com.chilloutrecords.activities.ParentActivity.user_model;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class ProfileFragment extends Fragment {
    private View root_view;

    private String STR_ID = "";
    private final String TAG_LOG = "PROFILE";
    private DialogMethods dialogs;

    private String IMG_PROFILE_URL = "";
    private TextView
            txt_name,
            txt_stage_name,
            txt_info,
            txt_profile_visits,
            txt_member_since;
    private MaterialButton
            btn_singles,
            btn_collections;

    private FloatingActionButton
            btn_share,
            btn_edit_picture,
            btn_edit_profile;

    private RoundedImageView
            img_profile;

    private DatabaseReference
            reference;


    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.frag_profile, container, false);

                reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);
                dialogs = new DialogMethods(getActivity());

                // FIND ALL VIEWS
                txt_name = root_view.findViewById(R.id.txt_name);
                txt_stage_name = root_view.findViewById(R.id.txt_stage_name);
                txt_info = root_view.findViewById(R.id.txt_info);
                txt_profile_visits = root_view.findViewById(R.id.txt_profile_visits);
                txt_member_since = root_view.findViewById(R.id.txt_member_since);

                img_profile = root_view.findViewById(R.id.img_profile);
                btn_edit_picture = root_view.findViewById(R.id.btn_edit_picture);
                btn_edit_profile = root_view.findViewById(R.id.btn_edit_profile);
                btn_share = root_view.findViewById(R.id.btn_share);

                btn_singles = root_view.findViewById(R.id.btn_singles);
                btn_collections = root_view.findViewById(R.id.btn_collections);

                btn_singles.setVisibility(View.GONE);
                btn_collections.setVisibility(View.GONE);

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_ID = bundle.getString(EXTRA_STRING);
                    btn_edit_picture.hide();
                    btn_edit_profile.hide();
                } else {
                    STR_ID = FIREBASE_USER.getUid();
                    btn_edit_picture.show();
                    btn_edit_profile.show();
                }
                btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ShareFragment(), PAGE_TITLE_SHARE, "", null, true));
                    }
                });
                btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new RegisterFragment(), PAGE_TITLE_PROFILE_EDIT, "", null, true));
                    }
                });

                btn_edit_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                img_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogs.setDialogImagePreview(IMG_PROFILE_URL);
                    }
                });

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            container.removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onResume() {
        loadProfile();
        super.onResume();
    }

    // CLASS METHODS ===============================================================================
    private void loadProfile() {
        reference.child(STR_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel model = dataSnapshot.getValue(UserModel.class);
                if (model != null) {
                    user_model = model;
                    // Image
                    Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.p_pic, new UrlInterface() {
                        @Override
                        public void completed(Boolean success, String url) {
                            if (success) {
                                Glide.with(getActivity()).load(url).into(img_profile);
                                IMG_PROFILE_URL = url;
                            }
                        }
                    });
                    // General info
                    txt_name.setText(model.name);
                    txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
                    txt_info.setText(model.info);
                    // stats
                    txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
                    txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
                    // Music content
                    if (model.is_artist && model.music != null) {

                        if (model.music.singles.size() > 0) {
                            btn_singles.setVisibility(View.VISIBLE);
                            btn_singles.setText(String.valueOf(model.music.singles.size()).concat(" Single(s)"));
                            btn_singles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((ParentActivity) Objects.requireNonNull(getActivity()))
                                            .loadFragment(
                                                    new NavigationModel(new TrackFragment(), "Singles", BuildConfig.DB_REF_SINGLES, model.music.singles,
                                                            true
                                                    ));
                                }
                            });
                        } else {
                            btn_singles.setVisibility(View.GONE);
                        }

                        if (model.music.collections.size() > 0) {
                            btn_collections.setVisibility(View.VISIBLE);
                            btn_collections.setText(String.valueOf(model.music.collections.size()).concat(" Album(s) / Mixtape(s)"));
                            btn_collections.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((ParentActivity) Objects.requireNonNull(getActivity()))
                                            .loadFragment(
                                                    new NavigationModel(new TrackFragment(), "Albums / Mixtapes", BuildConfig.DB_REF_COLLECTIONS, model.music.collections,
                                                            true
                                                    ));
                                }
                            });
                        } else {
                            btn_collections.setVisibility(View.GONE);
                        }
                    } else {
                        btn_singles.setVisibility(View.GONE);
                        btn_collections.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });
    }
}