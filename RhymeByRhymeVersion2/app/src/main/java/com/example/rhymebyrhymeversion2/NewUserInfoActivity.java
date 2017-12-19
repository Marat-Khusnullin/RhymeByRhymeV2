package com.example.rhymebyrhymeversion2;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView background;
    private CircleImageView profile;
    private EditText name;
    private EditText surname;
    private EditText country;
    private Toolbar toolbar;
    private TextView titleText;
    private ImageView mAcceptImage;
    private ImageView mCancelImage;
    private FirebaseUser currentUser;
    private String path;

    private File mTempPhoto;
    private String mImageUri = "";
    private String mRereference = "";
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private Context context = this;

    private static final int REQUEST_CODE_PERMISSION_RECEIVE_CAMERA = 102;
    private static final int REQUEST_CODE_TAKE_PHOTO = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.new_poem_action_bar);
        ((Toolbar)getSupportActionBar().getCustomView().getParent()).setContentInsetsAbsolute(0,0);
        titleText = (TextView) findViewById(R.id.new_pub_toolbar_text);
        titleText.setText("Редактирование");
        mAcceptImage = (ImageView) findViewById(R.id.new_pub_accept_image);
        mCancelImage = (ImageView) findViewById(R.id.new_pub_close_image);


        background = (ImageView) findViewById(R.id.background_image);
        profile = (CircleImageView) findViewById(R.id.profile_image);

        name = (EditText) findViewById(R.id.new_name_text);
        surname = (EditText) findViewById(R.id.new_surname_text);
        country = (EditText) findViewById(R.id.new_country_text);



        mAcceptImage.setOnClickListener(this);
        mCancelImage.setOnClickListener(this);
        background.setOnClickListener(this);
        profile.setOnClickListener(this);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        setOldInfo();






    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.background_image){
            path = "images/background/";
            addPhoto();
        }
        else if (v.getId() == R.id.new_pub_accept_image){
            /*progressDialog.setMessage("Загрузка...");
            progressDialog.show();*/
            Map<String, Object> childUpdates = new HashMap<>();
            if (!name.getText().toString().equals("")){
                childUpdates.put("name", name.getText().toString());
            }
            if (!surname.getText().toString().equals("")){
                childUpdates.put("surname", surname.getText().toString());
            }

            mRef.child("users").child(currentUser.getUid()).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference();
                    Intent intent = new Intent(NewUserInfoActivity.this, MainProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else if(v.getId() == R.id.new_pub_close_image) {
            finish();
        }
        if (v.getId() == R.id.profile_image) {
            path = "images/";
            addPhoto();
        }

    }

    //Метод для добавления фото
    private void addPhoto() {

        //Проверяем разрешение на работу с камерой
        boolean isCameraPermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        //Проверяем разрешение на работу с внешнем хранилещем телефона
        boolean isWritePermissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        //Если разрешения != true
        if(!isCameraPermissionGranted || !isWritePermissionGranted) {

            String[] permissions;//Разрешения которые хотим запросить у пользователя

            if (!isCameraPermissionGranted && !isWritePermissionGranted) {
                permissions = new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            } else if (!isCameraPermissionGranted) {
                permissions = new String[] {android.Manifest.permission.CAMERA};
            } else {
                permissions = new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }
            //Запрашиваем разрешения у пользователя
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_RECEIVE_CAMERA);
        } else {
            //Если все разрешения получены
            try {
                mTempPhoto = createTempImageFile(getExternalCacheDir());
                mImageUri = mTempPhoto.getAbsolutePath();

                //Создаём лист с интентами для работы с изображениями
                List<Intent> intentList = new ArrayList<>();
                Intent chooserIntent = null;


                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                takePhotoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempPhoto));

                intentList = addIntentsToList(this, intentList, pickIntent);
                intentList = addIntentsToList(this, intentList, takePhotoIntent);

                if (!intentList.isEmpty()) {
                    chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),"Choose your image source");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                }

                /*После того как пользователь закончит работу с приложеним(которое работает с изображениями)
                 будет вызван метод onActivityResult
                */
                startActivityForResult(chooserIntent, REQUEST_CODE_TAKE_PHOTO);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
    }



    public static File createTempImageFile(File storageDir) throws IOException {

        // Генерируем имя файла
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());//получаем время
        String imageFileName = "photo_" + timeStamp;//состовляем имя файла

        //Создаём файл
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }


    public static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        switch (requestCode){
            case REQUEST_CODE_TAKE_PHOTO:
                if(resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        mImageUri = getRealPathFromURI(data.getData());
                        if(path.equals("images/background/")) {
                            Picasso.with(getBaseContext())
                                    .load(data.getData())
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(background);
                        } else {
                            Picasso.with(this)
                                    .load(mImageUri)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(profile);
                        }
                        uploadFileInFireBaseStorage(data.getData());
                    } else if (mImageUri != null) {
                        mImageUri = Uri.fromFile(mTempPhoto).toString();

                        if(path.equals("images/background/")) {
                            Picasso.with(this)
                                    .load(mImageUri)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(background);
                        }  else {
                            Picasso.with(this)
                                    .load(mImageUri)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(profile);
                        }

                        uploadFileInFireBaseStorage(Uri.fromFile((mTempPhoto)));
                    }
                }
                break;
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int columnIndex = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }



    private void uploadFileInFireBaseStorage (Uri uri){
        UploadTask uploadTask = mStorageRef.child(path + FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(uri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred());
                Log.i("Load","Upload is " + progress + "% done");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                Log.i("Load" , "Uri download" + downloadUri);
            }
        });
    }


    private void setOldInfo() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();

        name.setText(getIntent().getStringExtra("name"));
        surname.setText(getIntent().getStringExtra("surname"));
        country.setText(getIntent().getStringExtra("country"));


        mStorageRef.child("images/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                Picasso.with(context).load(path).resize(200,200).centerCrop().into(profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(context)
                        .load(R.drawable.profile)
                        .resize(200,200).centerCrop().into(profile);
            }
        });

        mStorageRef.child("images/background/" + user.getUid()).getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String path = storageMetadata.getDownloadUrl().toString();
                Picasso.with(context).load(path).resize(200,200).centerCrop().into(background);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(context)
                        .load(R.drawable.background)
                        .resize(200,200).centerCrop().into(background);
            }
        });
    }



}
