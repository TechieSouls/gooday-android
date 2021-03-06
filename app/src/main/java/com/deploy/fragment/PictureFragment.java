package com.deploy.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.DeviceManager;
import com.deploy.Manager.InternetManager;
import com.deploy.Manager.UrlManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.R;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.util.CenesUtils;
import com.deploy.util.ImageUtils;
import com.deploy.util.RoundedDrawable;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rohan on 10/10/17.
 */

public class PictureFragment extends CenesFragment {

    public final static String TAG = "PictureFragment";

    ImageView ivAttachment;
    Button btnTakePicture, btnSelectPicture;
    private ProgressDialog pDialog;
    Uri fileUri;
    File file;
    private String isTakeOrUpload = "take_picture";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE = 12;
    private static final int CLICK_IMAGE = 13;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlertManager alertManager;
    ValidationManager validationManager;
    InternetManager internetManager;
    UrlManager urlManager;
    DeviceManager deviceManager;
    ApiManager apiManager;

    User loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_picture, container, false);

        init(v);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakeOrUpload = "take_picture";
                if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    firePictureIntent();
                }
            }
        });

        btnSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakeOrUpload = "upload_picture";
                if (ContextCompat.checkSelfPermission(getCenesActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    firePictureIntent();
                }
            }
        });

        return v;
    }

    public void nextClickListener() {
        if (internetManager.isInternetConnection(getCenesActivity()) && file != null) {
            new DoFileUpload().execute();
        } else {
            //Toast.makeText(getCenesActivity(), "No Internet Connection !", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            firePictureIntent();
        }
    }

    Uri cameraFileUri;

    public void firePictureIntent() {
        if (isTakeOrUpload == "take_picture") {
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cenes";
            File newdir = new File(dir);
            newdir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = new File(dir + File.separator + "IMG_" + timeStamp + ".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cameraFileUri = null;
            cameraFileUri = Uri.fromFile(file);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            startActivityForResult(takePictureIntent, CLICK_IMAGE);
        } else if (isTakeOrUpload == "upload_picture") {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, PICK_IMAGE);
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLICK_IMAGE || requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (isTakeOrUpload == "take_picture") {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), cameraFileUri);
                        RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(imageBitmap, file.getAbsolutePath()));
                        ivAttachment.setImageDrawable(drawable);

                    } else if (isTakeOrUpload == "upload_picture") {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), data.getData());
                        Log.e("Action ******** ", data.getDataString());

                        Uri uri = Uri.parse(data.getDataString());
                        String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), uri);

                        RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(imageBitmap, filePath));
                        ivAttachment.setImageDrawable(drawable);

                        file = new File(filePath); //To Upload File
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CLICK_IMAGE || requestCode == PICK_IMAGE) {
                try {
                    if (isTakeOrUpload == "take_picture") {
                        ImageUtils.cropImageWithAspect(cameraFileUri, this, 512, 512);
                    } else if (isTakeOrUpload == "upload_picture") {
                        ImageUtils.cropImageWithAspect(Uri.parse(data.getDataString()), this, 512, 512);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == Crop.REQUEST_CROP) {
                try {
                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                    file = new File(filePath);

                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), Crop.getOutput(data));
                    RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(imageBitmap, filePath));

                    ivAttachment.setImageDrawable(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init(View v) {
        //----------------------------------
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alertManager = coreManager.getAlertManager();
        validationManager = coreManager.getValidatioManager();
        internetManager = coreManager.getInternetManager();
        urlManager = coreManager.getUrlManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
        //-----------------------------------------
        btnTakePicture = (Button) v.findViewById(R.id.bt_takepicture);
        btnSelectPicture = (Button) v.findViewById(R.id.bt_selectpicture);
        ivAttachment = (ImageView) v.findViewById(R.id.ivAttachment);

        loggedInUser = userManager.getUser();
        if (loggedInUser != null && !CenesUtils.isEmpty(loggedInUser.getPicture())) {
            //new DownloadImageTask(ivAttachment).execute(user.getPicture());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.circleCrop();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            Glide.with(getCenesActivity()).load(loggedInUser.getPicture()).apply(requestOptions).into(ivAttachment);
        }
    }

    class DoFileUpload extends AsyncTask<String, JSONObject, JSONObject> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(getCenesActivity());
            pDialog.setMessage("wait uploading Image..");
            pDialog.setIndeterminate(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                JSONObject response = apiManager.postMultipart(user, file, getCenesActivity());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                if (response != null && response.getInt("errorCode") == 0) {
                    if (response.has("photo")) {
                        loggedInUser.setPicture(response.getString("photo"));
                        userManager.updateProfilePic(loggedInUser);
                    }
                } else {
                    getCenesActivity().showRequestTimeoutDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
