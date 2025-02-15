package com.chilloutrecords.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.fragments.HomeFragment;
import com.chilloutrecords.fragments.ProfileFragment;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.services.LoginStateService;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_STORAGE;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
import static com.chilloutrecords.utils.StaticVariables.INT_PERMISSIONS_CAMERA;
import static com.chilloutrecords.utils.StaticVariables.INT_PERMISSIONS_STORAGE;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class ParentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_page_title;
    DialogMethods dialog;

    public ArrayList<NavigationModel> navigation_list = new ArrayList<>();

    public final static String
            PAGE_TITLE_HOME = "Home",
            PAGE_TITLE_ARTISTS = "Home / Artists",
            PAGE_TITLE_VIDEOS = "Home / Videos",
            PAGE_TITLE_PROFILE = "Home / Profile",
            PAGE_TITLE_PROFILE_EDIT = "Home / Profile Edit",
            PAGE_TITLE_ADD_TRACK = "Add / Edit track",
            PAGE_TITLE_SHARE = "Home / Share",
            PAGE_TITLE_ABOUT = "Home / About us",
            PAGE_TITLE_POINTS = "Home / Points",
            PAGE_TITLE_LOGOUT = "Home / Logout";

    public static String
            STR_FILE_NAME = "",
            STR_SAVE_TO_PATH = "",
            STR_DOWNLOAD_URL = "";

    final String[] permissions_camera = {
            Manifest.permission.CAMERA};

    final String[] permissions_storage = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        StaticMethods.startServiceIfNotRunning(this, LoginStateService.class);

        ShortcutBadger.applyCount(ParentActivity.this, 0);

        dialog = new DialogMethods(ParentActivity.this);

        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_navigation);

        // SETUP TOOLBAR
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadFragment(new NavigationModel(new HomeFragment(), PAGE_TITLE_HOME, "", null, true));

    }

    @Override
    public void onBackPressed() {
        int size = navigation_list.size();
        if (size > 1) {
            navigation_list.remove(size - 1);
            navigation_list.get(size - 2).add_to_back_stack = false;
            loadFragment(navigation_list.get(size - 2));
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        dialog.setDialogPermissions(grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            // RESULT FOR IMAGE UPLOAD =====================================================
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    final String file_name = "/users/" + FIREBASE_USER.getUid() + ".jpg";
                    FIREBASE_STORAGE.getReference()
                            .child(BuildConfig.STORAGE_IMAGES + file_name)
                            .putFile(resultUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StaticMethods.logg("FILE", "SUCCESSFULLY UPLOADED");
                                    USER_MODEL.p_pic = file_name;
                                    Database.setUser(USER_MODEL, new GeneralInterface() {
                                        @Override
                                        public void success() {
                                            try {
                                                ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.ll_fragment);
                                                assert fragment != null;
                                                fragment.updateArt(file_name);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                StaticMethods.showToast(2, getString(R.string.error_unknown));
                                            }
                                        }

                                        @Override
                                        public void failed() {
                                            StaticMethods.showToast (2, getString(R.string.error_unknown));
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    StaticMethods.logg("FILE", "FAILED TO UPLOAD");
                                    StaticMethods.showToast(2, getString(R.string.error_unknown));
                                }
                            });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    StaticMethods.showToast(2, getString(R.string.error_unknown));
                }
            }
        } else {
            StaticMethods.showToast(1, "Cancelled");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @AfterPermissionGranted(INT_PERMISSIONS_STORAGE)
    public void startFileDownload() {
        if (EasyPermissions.hasPermissions(ParentActivity.this, permissions_storage)) {
            StaticMethods.downloadFileFromUrl(STR_DOWNLOAD_URL, STR_FILE_NAME, STR_SAVE_TO_PATH);
        } else {
            EasyPermissions.requestPermissions(ParentActivity.this, getString(R.string.rationale_storage),
                    INT_PERMISSIONS_STORAGE, permissions_storage);
        }
    }

    @AfterPermissionGranted(INT_PERMISSIONS_CAMERA)
    public void updateProfileImage() {
        if (EasyPermissions.hasPermissions(ParentActivity.this, permissions_camera)) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(ParentActivity.this);
        } else {
            EasyPermissions.requestPermissions(ParentActivity.this, getString(R.string.rationale_image),
                    INT_PERMISSIONS_CAMERA, permissions_camera);
        }
    }

    // BASIC METHODS ===============================================================================
    public void loadFragment(NavigationModel navigation) {
        if (navigation.add_to_back_stack) {
            navigation_list.add(navigation);
        }

        if (navigation_list.size() < 2) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_back));
        }

        Bundle bundle = new Bundle();

        if (!navigation.extra_bundles.equals("")) {
            bundle.putString(EXTRA_STRING, navigation.extra_bundles);
            navigation.fragment.setArguments(bundle);
        }
        if (navigation.extra_string_array != null) {
            bundle.putStringArrayList(EXTRA_DATA, navigation.extra_string_array);
            navigation.fragment.setArguments(bundle);
        }

        txt_page_title.setText(navigation.page_title);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if (navigation.add_to_back_stack) {
            transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
        } else {
            transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        }
        transaction.replace(R.id.ll_fragment, navigation.fragment).addToBackStack(null).commit();
    }

}
