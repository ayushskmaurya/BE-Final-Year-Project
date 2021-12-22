package com.messengerhelloworld.helloworld.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.messengerhelloworld.helloworld.databinding.ActivityMainBinding;
import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.utils.Base;
import com.messengerhelloworld.helloworld.utils.DatabaseHandler;
import com.messengerhelloworld.helloworld.utils.DisplayProfileImage;
import com.messengerhelloworld.helloworld.utils.LastDeletedProfileImages;
import com.messengerhelloworld.helloworld.utils.ManageFolders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "hwmLogMain";
    private static final String PROFILE_IMAGE_NAME = "com.messengerhelloworld.helloworld.profileImageName";
    private final DatabaseHandler databaseHandler = new DatabaseHandler(this, "helloworld", null, 1);
    private final Handler handler = new Handler();
    private static boolean shouldUploadAttachments = true;
    private static boolean shouldUpdateProfilePhoto = false;
    private ImageView myProfilePhoto;

    public static void setShouldUpdateProfilePhoto(boolean shouldUpdateProfilePhoto) {
        MainActivity.shouldUpdateProfilePhoto = shouldUpdateProfilePhoto;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
        if(sp.getString("HelloWorldUserId", null) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_chats, R.id.nav_contacts, R.id.nav_about_helloworld)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            View headerView = navigationView.getHeaderView(0);
            TextView userName = headerView.findViewById(R.id.userName);
            TextView userMobileNo = headerView.findViewById(R.id.userMobileNo);
            userName.setText(sp.getString("HelloWorldUserName", null));
            userMobileNo.setText(sp.getString("HelloWorldUserMobileNo", null));

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.menu_logout).setOnMenuItemClickListener(item -> {
                sp.edit().clear().commit();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            });

            // Permissions
            String[] permissionsRequired = {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ArrayList<String> permissions = new ArrayList<>();
            for(String permission : permissionsRequired)
                if(ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED)
                    permissions.add(permission);
            if(permissions.size() > 0)
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[permissions.size()]), 1);

            // Creating all the required folders.
            ManageFolders.createFoldersForProfileImages();

            // Deleting stored profile images frequently.
            databaseHandler.insertLastDeletedProfileImagesDateTime(
                    new LastDeletedProfileImages(databaseHandler.getCurrentDateTime()));
            deleteProfileImages();

            // Starting to upload all the pending attachments to server.
            if(shouldUploadAttachments) {
                shouldUploadAttachments = false;
                uploadAttachmentsToServer();
            }

            // Displaying my Profile Photo.
            myProfilePhoto = headerView.findViewById(R.id.profilePhoto);
            displayMyProfilePhoto(sp.getString("HelloWorldUserProfilePhoto", null), myProfilePhoto);

            // Updating the profile photo, if the user clicked on edit profile photo button.
            if(shouldUpdateProfilePhoto) {
                setShouldUpdateProfilePhoto(false);
                selectProfileImage();
            }
        }
    }



    // --- X --- X --- X --- X --- X --- X --- X --- X --- X --- X ---
    // Managing Attachments.


    // Uploading all the pending attachments to server.
    public void uploadAttachmentsToServer() {
        if(databaseHandler.getAttachmentsCount() > 0) {
            Map<String, String> row = databaseHandler.getAttachment();
            Uri filepath = Uri.parse(row.get("filepath"));

            InputStream inputStream;
			try {
				inputStream = getContentResolver().openInputStream(filepath);
				int noOfBytesRead;
				byte[] buffer = new byte[10240];
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				while((noOfBytesRead = inputStream.read(buffer)) != -1)
					byteArrayOutputStream.write(buffer, 0, noOfBytesRead);

				byte[] bytesOfFile = byteArrayOutputStream.toByteArray();
				String encodedFileStr = android.util.Base64.encodeToString(bytesOfFile, Base64.DEFAULT);

				Map<String, String> postData = new HashMap<>();
                postData.put("whatToDo", "saveAttachedFile");
                postData.put("msgid", row.get("msgid"));
				postData.put("temp_filename", row.get("temp_filename"));
				postData.put("encoded_file_str", encodedFileStr);
				uploadFile(postData);

			} catch (IOException e) {
                Log.e(TAG, e.toString());
			}
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadAttachmentsToServer();
                }
            }, 3000);
        }
    }

    // Uploading the attachment.
    private void uploadFile(Map<String, String> postData) {
        Volley.newRequestQueue(this).add(
                new StringRequest(
                        Request.Method.POST,
                        Base.getBaseUrl() + "/manageAttachment.php",
                        response -> {
                            if(response.equals("1"))
                                databaseHandler.deleteAttachment(postData.get("msgid"));
                            uploadAttachmentsToServer();
                        },
                        error -> {
                            Log.e(TAG, error.toString());
                            uploadAttachmentsToServer();
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        return postData;
                    }
                }
        );
    }



    // --- X --- X --- X --- X --- X --- X --- X --- X --- X --- X ---
    // Managing Profile Photo.


    // Displaying profile photo of the user.
    private void displayMyProfilePhoto(String profilePhotoName, ImageView profilePhotoView) {
        if(profilePhotoName.equals("null")) {
            profilePhotoView.setOnClickListener(v -> {
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    selectProfileImage();
                else
                    Toast.makeText(this, "Please grant permission to Read External Storage.", Toast.LENGTH_SHORT).show();
            });
            profilePhotoView.setScaleType(ImageView.ScaleType.CENTER);
            profilePhotoView.setImageResource(R.drawable.icon_add_image);
        }
        else {
            DisplayProfileImage.display(this, profilePhotoName, profilePhotoView);
            profilePhotoView.setOnClickListener(v -> {
                Intent intentProfileImage = new Intent(this, ViewProfileImageActivity.class);
                intentProfileImage.putExtra(PROFILE_IMAGE_NAME, profilePhotoName);
                startActivity(intentProfileImage);
            });
        }
    }

    // Selecting an image to set as profile photo.
    private void selectProfileImage() {
        Intent intentSelectProfileImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentSelectProfileImage.setType("image/*");
        startActivityForResult(intentSelectProfileImage, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(filepath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                int imageSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
                Bitmap croppedImage = Bitmap.createBitmap(bitmap, 0, 0, imageSize, imageSize);

                encodeProfilePhoto(sp.getString("HelloWorldUserId", null), croppedImage);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.toString());
                Toast.makeText(this, "Unable to set profile photo, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Encoding profile image to String.
    private void encodeProfilePhoto(String userid, Bitmap profileImage) {
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1);
        profileImage.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream2);

        byte[] bytesOfFile1 = byteArrayOutputStream1.toByteArray();
        byte[] bytesOfFile2 = byteArrayOutputStream2.toByteArray();
        String encodedProfileImage = android.util.Base64.encodeToString(bytesOfFile1, Base64.DEFAULT);
        String encodedLowQualityProfileImage = android.util.Base64.encodeToString(bytesOfFile2, Base64.DEFAULT);

        Map<String, String> postData = new HashMap<>();
        postData.put("whatToDo", "saveProfileImage");
        postData.put("userid", userid);
        postData.put("profile_image", encodedProfileImage);
        postData.put("reduced_profile_image", encodedLowQualityProfileImage);
        uploadProfilePhotoToServer(postData);
    }

    // Uploading profile photo to the server.
    private void uploadProfilePhotoToServer(Map<String, String> postData) {
        myProfilePhoto.setEnabled(false);
        Volley.newRequestQueue(this).add(
                new StringRequest(
                        Request.Method.POST,
                        Base.getBaseUrl() + "/manageProfileImage.php",
                        response -> {
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("HelloWorldUserProfilePhoto", response);
                            ed.apply();
                            Toast.makeText(this, "Profile photo updated successfully.", Toast.LENGTH_SHORT).show();
                            myProfilePhoto.setEnabled(true);
                            displayMyProfilePhoto(response, myProfilePhoto);
                        },
                        error -> {
                            Log.e(TAG, error.toString());
                            Toast.makeText(this, "Unable to set profile photo, please try again.", Toast.LENGTH_SHORT).show();
                            myProfilePhoto.setEnabled(true);
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        return postData;
                    }
                }
        );
    }

    // Deleting the stored profile images frequently.
    private void deleteProfileImages() {
        if(databaseHandler.getDifferenceBetweenDateTime() > 86400 &&
                ManageFolders.createFoldersForProfileImages()) {
            File profileImagesDirectory = new File(Base.getProfileImagesFolder());
            File[] profileImgs = profileImagesDirectory.listFiles();
            for(File profileImg : profileImgs)
                profileImg.delete();
            databaseHandler.updateLastDeletedProfileImagesDateTime(databaseHandler.getCurrentDateTime());
        }
    }



    // --- X --- X --- X --- X --- X --- X --- X --- X --- X --- X ---


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}