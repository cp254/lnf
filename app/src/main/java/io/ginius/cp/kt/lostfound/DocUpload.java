package io.ginius.cp.kt.lostfound;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.ginius.cp.kt.lostfound.models.Result;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.android.volley.VolleyLog.TAG;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_LNAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_REF;
import static io.ginius.cp.kt.lostfound.PreferenceManager.USER_ID;

/**
 * Created by cyprian on 7/4/18.
 */

public class DocUpload extends MainBaseActivity{

    public int SUCCESS = 0;
    ArrayList<HashMap<String, String>> transfersList;
    Toolbar toolbar;
    Result docObjList[];
    private Context mContext;
    private Activity mActivity;
    InputMethodManager imm;
    ImageView preview;
    int PICK_IMAGE_REQUEST = 111;
    Bitmap bitmap;
    String imageString;
    Button next, upload;
    String refDocNo, name, userId;
    private io.ginius.cp.kt.lostfound.PreferenceManager prefManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);
        toolbar = findViewById(R.id.toolbar);
        mContext = getApplicationContext();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preview = findViewById(R.id.preview);
        next = findViewById(R.id.btn_next);
        upload =  findViewById(R.id.btn_upload);
        mActivity = DocUpload.this;
        prefManager = new io.ginius.cp.kt.lostfound.PreferenceManager(this);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                        requestStoragePermission();
                picker();
            }
        });
        try {
            refDocNo = prefManager.loadPrefs(DOC_REF, "");
            name = prefManager.loadPrefs(DOC_LNAME,"")+"_"+prefManager.loadPrefs(DOC_ID, "");
            userId = prefManager.loadPrefs(USER_ID, "");
        } catch (Exception e) {
            onBackPressed();
        }



        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    webServiceRequest(POST, getString(R.string.service_url), uploadImage(),
                            "upload_document");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public JSONObject uploadImage() throws JSONException {
        if(bitmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);
        } else {
            imageString = "";
        }
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put("image_name", name);
        dataItem.put("base64", imageString);
        dataItem.put("doc_ref", refDocNo);
        dataItem.put("uploadby", userId);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "upload_doc_image");
        return dataW;
    }
    public void webServiceRequest(final int reqMethod, final String url, JSONObject jsonReq, final String reqIdentifier) {
        if (jsonReq != null) {
            pd.show();
            try {
                Log.e(TAG + "WebService Request ", jsonReq.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            Log.i(TAG + "WebService Request ", url);

        JsonObjectRequest jor = new JsonObjectRequest(reqMethod, url, jsonReq,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i(TAG + " WebService response", response.toString());
                            if (reqIdentifier.equalsIgnoreCase("upload_document"))
                                docImgUploadResponse(response);


                        } catch (Exception ex) {
                            Log.e(TAG + " error", ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                createErrorDetailDialog(DocUpload.this, error, "Error").show();
                Log.e(TAG, error.toString());
            }
        });
//        SingletonRequestQueue.getInstance().addToRequestQueue(jor,"search_doc");
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jor);
    }

    public void docImgUploadResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.i(TAG, jsonObject.toString());
            Log.e("upload response0", jsonObject.toString(4));
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.success_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCanceledOnTouchOutside(false);
                    TextView message = dialog.findViewById(R.id.text);
                    message.setText("Image uploaded successfully");
                    dialog.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(mActivity, MainActivity.class);
                            startActivity(intent);
//
                        }
                    });
                    dialog.show();
                Log.e("upload response1", jsonObject.toString(4 ));

            } else {
                String respMsg = jsonObject.getString(getString(R.string.statusname));
                Log.e("upload response2", respMsg.toString());
                Toast.makeText(this, respMsg, Toast.LENGTH_LONG).show();

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    private void requestStoragePermission() {
//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        // check if all permissions are granted
//                        if (report.areAllPermissionsGranted()) {
//                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
//                            picker();
//                        }
//
//                        // check for permanent denial of any permission
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            // show alert dialog navigating to Settings
//                            //Toast.makeText(getApplicationContext(), "Grant gallery permission in your phone settings.", Toast.LENGTH_SHORT).show();
//                            picker();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//
//                    }
//
//
//                }).
//                withErrorListener(new PermissionRequestErrorListener() {
//                    @Override
//                    public void onError(DexterError error) {
//                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .onSameThread()
//                .check();
    }

    void picker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();



            try {

//

                Bitmap image, rotated;
                image = getBitmap(getContentResolver(), filePath);
                rotated = getResizedBitmap(image, 300, 300);
                bitmap = rotateImageIfRequired(rotated, Uri.parse(getRealPathFromURIA(filePath)));
                preview.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("error",e.getMessage());
            }
        }
    }

    public String getRealPathFromURIA(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }





    @Override
    protected void onPause() {
        super.onPause();
    }
}
