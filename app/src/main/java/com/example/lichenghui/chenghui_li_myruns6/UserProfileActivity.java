package com.example.lichenghui.chenghui_li_myruns6;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class UserProfileActivity extends AppCompatActivity {

    //To identify the camera activity
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_GALLERY = 1;
    //To identify the permission request
    public static final int PERMISSION_REQUEST_CODE = 0;
    //To store the pic temporarily in case of change of orientation
    private static String TEMP_PHOTO_KEY = "temp_photo_uri";
    private static Context MyContext;
    public String photo_file_name = "Myphoto.jpg";

    public String[] RequestString = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    private Uri CaptureUri;
    private Uri CroppedUri;
    private ImageView myImageView;
    private RadioGroup Group;
    private EditText Name;
    private EditText Email;
    private EditText Phone;
    private EditText Class;
    private EditText Major;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        myImageView = (ImageView) findViewById(R.id.imageProfile);
        MyContext = getApplicationContext();
        Group = (RadioGroup) findViewById(R.id.radio_gender);
        Name = (EditText) findViewById(R.id.edit_name);
        Email = (EditText) findViewById(R.id.edit_email);
        Phone = (EditText) findViewById(R.id.edit_phone);
        Class = (EditText) findViewById(R.id.edit_class);
        Major = (EditText) findViewById(R.id.edit_major);

        //The PermissionRequests that we're gonna ask users:
        //External Stroage and Camera permission (Refer to Android Developer website
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, RequestString, PERMISSION_REQUEST_CODE);
        }
        //Load User data
        load();

    }


    @Override //Refer to Android Developer Website
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, RequestString, PERMISSION_REQUEST_CODE);
                }
                //All the request permissions must be granted or the app cannot work!
                //Keep asking the user if any request is denied
            }
        }

    }

    // Implemented in case of change of orientation
    // Save the image capture uri and the user data before the activity goes into background
    //Note: Called before onstop() (Since activities are considered to be killable only after they're stopped
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TEMP_PHOTO_KEY, CroppedUri);
    }


    //Note: Called after onCreate()
    //set the profile photo to the one that just taken
    @Override
    protected void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        if (inState != null)
        {
            CroppedUri = inState.getParcelable(TEMP_PHOTO_KEY);
            myImageView.setImageURI(CroppedUri);
        }
    }


    //Event handler that handle the event when Save Button is clicked
    public void Save_Click(View v){
        save();
        Toast.makeText(getApplicationContext(),"Profile Saved",Toast.LENGTH_SHORT).show();
        finish();
    }

    //Event handler that handle the event when Cancel Button is clicked
    public void Cancel_Click(View v) {
        System.exit(0);
    }


    // Take photo from camera, since we don't need to pick pictures from gallery at this stage
    // The method of how to store and get back ImageUri is adapted from the lecture notes "Phone
    // Camera and Data Storage"
    public void Change_Click(View v) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserProfileActivity.this);
        mBuilder.setTitle("Pick Profile Photo from");

        ArrayAdapter<String> PhotoAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                android.R.layout.select_dialog_singlechoice);
        PhotoAdapter.add("Camera");
        PhotoAdapter.add("Gallery");
        mBuilder.setAdapter(PhotoAdapter, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface mDialog, int position){
                if ( position == 0 )
                    Snap();
                else if (position == 1)
                    PickFromGallery();
                else return;
            }
        });
        mBuilder.show();

    }

    public void Snap(){
        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Refer to the course website
        ContentValues cvalue = new ContentValues();
        cvalue.put(MediaStore.Images.Media.TITLE, "Image");
        CaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cvalue);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, CaptureUri);

        startActivityForResult(CameraIntent, REQUEST_CODE_TAKE_FROM_CAMERA);
    }

    public void PickFromGallery(){
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(GalleryIntent, REQUEST_CODE_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Make sure the request was successful
        if (data==null){
            Log.d("UU", "came here");
        }
        if (resultCode == RESULT_OK && data!=null) {
            if (requestCode == REQUEST_CODE_TAKE_FROM_CAMERA) {
                //if the activity is to capture
                // Crop the captured image
                Crop(CaptureUri,CaptureUri);
                File temFile = new File(CaptureUri.getPath());
                if (temFile.exists())
                    temFile.delete();
            }
            else if (requestCode == Crop.REQUEST_CROP) {
                //if the activity is to crop
                // Set the view to the cropped image
                CroppedUri = Crop.getOutput(data);
                myImageView.setImageDrawable(null);
                myImageView.setImageURI(CroppedUri);
            }
            else if (requestCode == REQUEST_CODE_GALLERY){
                Uri SelectedPhoto = data.getData();
                Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop(SelectedPhoto,destination);

            }
            else return;
        }
        else return;
    }


    //load all user profile and user photo
    private void load() {

        // Load photo from internal storage, refer to the course website
        try {
            FileInputStream fis = openFileInput("photo.png");
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            myImageView.setImageBitmap(bitmap);
            fis.close();
        }
        catch (Exception e)
        {
            //if no photo saved before, set it to the default one
            myImageView.setImageResource(R.drawable.default_profile);
            e.printStackTrace();
        }

        // Load user data from shared preference

        String mKey = getString(R.string.preference_name);
        SharedPreferences Mypreference = getSharedPreferences(mKey, MODE_PRIVATE);

        // Load the user name

        mKey = getString(R.string.preference_key_profile_name);
        String mValue = Mypreference.getString(mKey, " ");
        if (mValue != " ")
            Name.setText(mValue);

        // Load the user email

        mKey = getString(R.string.preference_key_profile_email);
        mValue = Mypreference.getString(mKey, " ");
        if (mValue != " ")
            Email.setText(mValue);

        // Load the user phone

        mKey = getString(R.string.preference_key_profile_phone);
        mValue = Mypreference.getString(mKey, " ");
        if (mValue != " ")
            Phone.setText(mValue);

        // Load the user class

        mKey = getString(R.string.preference_key_profile_class);
        mValue = Mypreference.getString(mKey, " ");
        if (mValue != " ")
            Class.setText(mValue);

        // Load the user major

        mKey = getString(R.string.preference_key_profile_major);
        mValue = Mypreference.getString(mKey, " ");
        if (mValue != " ")
            Major.setText(mValue);

        // Load gender info and set radio box

        mKey = getString(R.string.preference_key_profile_gender);

        //Returns -1 if no radiobutton is checked
        int CheckedIndex = Mypreference.getInt(mKey, -1);
        //Access the radio group and find the button that is ticked
        RadioButton CheckedButton = (RadioButton) Group.getChildAt(CheckedIndex);
        if (CheckedButton != null)
            CheckedButton.setChecked(true);
    }

    //save all user profile and user photo
    private void save() {

        // Save photo  into internal storage.
        myImageView.buildDrawingCache();
        Bitmap bitmap = myImageView.getDrawingCache();
        //Try to store and catch error if crashes, refer to the Course website
        try {
            FileOutputStream fos = openFileOutput("photo.png", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        // Save user data into shared preference
        String mKey = getString(R.string.preference_name);
        SharedPreferences Mypreference = getSharedPreferences(mKey, MODE_PRIVATE);
        SharedPreferences.Editor mEditor = Mypreference.edit();
        mEditor.clear();

        // Save Name

        mKey = getString(R.string.preference_key_profile_name);
        String mValue =  Name.getText().toString();
        mEditor.putString(mKey, mValue);

        // Save email information

        mKey = getString(R.string.preference_key_profile_email);
        mValue =  Email.getText().toString();
        mEditor.putString(mKey, mValue);

        // Save phone information

        mKey = getString(R.string.preference_key_profile_phone);
        mValue = Phone.getText().toString();
        mEditor.putString(mKey, mValue);

        // Save class information

        mKey = getString(R.string.preference_key_profile_class);
        mValue =  Class.getText().toString();
        mEditor.putString(mKey, mValue);

        // Save major information

        mKey = getString(R.string.preference_key_profile_major);
        mValue = Major.getText().toString();
        mEditor.putString(mKey, mValue);

        // Read which index the radio is checked.

        mKey = getString(R.string.preference_key_profile_gender);

        int CheckedID = Group.getCheckedRadioButtonId();
        RadioButton CheckedBtn = (RadioButton) Group.findViewById(CheckedID);
        int CheckedIndex = Group.indexOfChild(CheckedBtn);
        mEditor.putInt(mKey, CheckedIndex);
        //Store the Index of checked button

        // Commit all the changes。
        mEditor.commit();

    }

    //Crop the picture taken from camera, refer to the CameraActivity Project
    private void Crop(Uri source, Uri destination) {
        Crop.of(source, destination).asSquare().start(UserProfileActivity.this);
    }

}
