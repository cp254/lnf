package io.ginius.cp.kt.lostfound;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.ginius.cp.kt.lostfound.models.Result;

import static com.android.volley.VolleyLog.TAG;
import static io.ginius.cp.kt.lostfound.PreferenceManager.COSTNOT;
import static io.ginius.cp.kt.lostfound.PreferenceManager.COSTSUB;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DATEFOUND;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_DETAILS;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_FNAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_LNAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_NAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_REF;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_TYPE;
import static io.ginius.cp.kt.lostfound.PreferenceManager.IS_LOGGED_IN;
import static io.ginius.cp.kt.lostfound.PreferenceManager.LAT;
import static io.ginius.cp.kt.lostfound.PreferenceManager.LON;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKEDBY;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKEDBYCONTACT;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKUPLOCATION;
import static io.ginius.cp.kt.lostfound.PreferenceManager.SEARCH_HISTORY;
import static io.ginius.cp.kt.lostfound.PreferenceManager.USER_EMAIL;
import static io.ginius.cp.kt.lostfound.PreferenceManager.USER_ID;
import static io.ginius.cp.kt.lostfound.PreferenceManager.USER_NAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.USER_PHONE_NUMBER;

public class MainActivity extends MainBaseActivity implements DocSearchAdapter.customButtonListener, NavigationView.OnNavigationItemSelectedListener {

    public int SUCCESS = 0;
    ArrayList<HashMap<String, String>> transfersList;
    ArrayList<HashMap<String, String>> searchHistori;
    ListView rv;
    TextView desc, tv, tvcbsms;
    EditText sv;
    Toolbar toolbar;
    TextView headerText, headerNames, search, t1, t2;
    Result docObjList[];
    private Context mContext;
    private Activity mActivity;
    LinearLayout header;
    String idQuery = "", contact = "", email = "", password = "", name = "", docNum = "";
    InputMethodManager imm;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    RelativeLayout button;
    LinearLayout no_result, cont;
    LinearLayout nsv;
    Map<String, String> typeMap;
    CheckBox cbSms, cbEmail;
    String OTP, searchItem;
    ImageButton ib;
    JSONArray searchHistory;
    ArrayList<String> notificationType;
    Boolean foundID = false, reg = false, log = false, notFound = false, searchCheck = false, subscribe = false;
    public io.ginius.cp.kt.lostfound.PreferenceManager prefManager;
    private NavigationView navigationView;
    private View HeaderView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        desc = findViewById(R.id.tv_desc);
        sv = findViewById(R.id.searchView);
        header = findViewById(R.id.header);
        button = findViewById(R.id.button);
        search = findViewById(R.id.btn_search);
        cont = findViewById(R.id.cont);
        cbSms = findViewById(R.id.cb_sms);
        ib = toolbar.findViewById(R.id.toolbar_menu);
        cbEmail = findViewById(R.id.cb_email);
        nsv = findViewById(R.id.nsv);
        mContext = getApplicationContext();
        tv = findViewById(R.id.tv_id);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        tvcbsms = findViewById(R.id.tv_cbsms);
        no_result = findViewById(R.id.not_found);
        mActivity = MainActivity.this;
        cont.setVisibility(View.GONE);
        notificationType = new ArrayList<String>();
        prefManager = new io.ginius.cp.kt.lostfound.PreferenceManager(this);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        HeaderView = navigationView.getHeaderView(0);
        headerText = HeaderView.findViewById(R.id.txt);
        headerNames = HeaderView.findViewById(R.id.tv_names);
        headerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, MyProfileSettingsActivity.class));
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setDrawerSlideAnimationEnabled(false);
        toggle.syncState();
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.loadBoolean(IS_LOGGED_IN, false) && prefManager.loadPrefs(USER_ID, "")!= ""){
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        if(prefManager.loadPrefs(USER_NAME, "")!= ""){
            String full = prefManager.loadPrefs(USER_NAME, "");
            String edited= full.substring(0,1);
            headerText.setText(edited);
            headerNames.setText(full);
            Log.e("ce", edited);
            Log.e("cf", full);
        }


        try {
            webServiceRequest(POST, getString(R.string.service_url), getCostsReq(), "charges");
        } catch (JSONException e) {
            e.printStackTrace();
        }




//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                header.setVisibility(View.GONE);
//                idQuery = query;
//                hideKeyboard(MainActivity.this);
//                prefManager.savePrefs(DOC_ID, idQuery);
//                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
//                if(prefManager.loadBoolean(IS_LOGGED_IN, false) && prefManager.loadPrefs(USER_ID, "")!= ""){
//                    searchCheck = true;
//                try {
//                    webServiceRequest(POST, getString(R.string.service_url), searchId(query), "search_document");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                }else{
//                    loginDialog();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchItem = newText;
//                button.setVisibility(View.GONE);
//                return false;
//
//            }
//        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    idQuery = sv.getText().toString().trim();
                searchItem = sv.getText().toString().trim();
                prefManager.savePrefs(DOC_ID, idQuery);
                header.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                hideKeyboard(MainActivity.this);
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
                if(prefManager.loadBoolean(IS_LOGGED_IN, false) && prefManager.loadPrefs(USER_ID, "")!= ""){
                    searchCheck = true;
                    if(!(TextUtils.isEmpty(searchItem))){
                        try {
                            webServiceRequest(POST, getString(R.string.service_url), searchId(searchItem), "search_document");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else
                        Utils.dialogErrorConfig(mActivity, "Blank search query. Enter a number to search");
                }else{
                    loginDialog();
                }
            }
        });



        cbSms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                notificationType.clear();
                if (cbSms.isChecked()) {
                    notificationType.add("SMS");
                    Log.e("++++++", String.valueOf(notificationType));
                } else {
                    notificationType.remove("SMS");
                    Log.e("-----", String.valueOf(notificationType));
                }

                if (cbEmail.isChecked() || cbSms.isChecked()) {
                    cont.setVisibility(View.VISIBLE);
                } else {
                    cont.setVisibility(View.GONE);
                }
            }
        });

        cbEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                notificationType.clear();
                if (cbEmail.isChecked()) {
                    notificationType.add("EMAIL");
                    Log.e("++++++", String.valueOf(notificationType));
                } else {
                    notificationType.remove("EMAIL");
                    Log.e("-----", String.valueOf(notificationType));
                }
                if (cbEmail.isChecked() || cbSms.isChecked()) {
                    cont.setVisibility(View.VISIBLE);
                } else {
                    cont.setVisibility(View.GONE);
                }
            }
        });


        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.loadBoolean(IS_LOGGED_IN, false)){
                    subCont();
                }else {
                    subscribe = true;
                    regDialog();
                }


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foundID = true;
                if(prefManager.loadBoolean(IS_LOGGED_IN, false)) {
                Intent intent = new Intent(mActivity, CreateDocsOne.class);
                            startActivity(intent);
                } else {
                    regDialog();
                }


            }
        });

        //

    }
    void regDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.register);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout bg = dialog.findViewById(R.id.image);
        final EditText ETphone = dialog.findViewById(R.id.et_contact);
        final EditText ETnames = dialog.findViewById(R.id.et_names);
        final EditText ETemail = dialog.findViewById(R.id.et_email);
        final EditText ETpassword = dialog.findViewById(R.id.et_password);
        TextView login = dialog.findViewById(R.id.tvlogin);
        Button submit = dialog.findViewById(R.id.btn_submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        if(email != "" && password != "" && contact != "") {
                if (TextUtils.isEmpty(ETemail.getText().toString()))
                    ETemail.setError("Please enter an email address");

                else{
                    email = ETemail.getText().toString().trim();
                    prefManager.savePrefs(USER_EMAIL, email);
                }
                if (TextUtils.isEmpty(ETphone.getText().toString()))
                    ETphone.setError("Please enter an email address");
                else{
                    contact = ETphone.getText().toString().trim();
                    prefManager.savePrefs(USER_PHONE_NUMBER, contact);
                }
                if (TextUtils.isEmpty(ETpassword.getText().toString()))
                    ETpassword.setError("Please enter an email address");
                else
                    password = ETpassword.getText().toString().trim();
                if (TextUtils.isEmpty(ETnames.getText().toString()))
                    ETnames.setError("Please enter your name");
                else {
                    name = ETnames.getText().toString().trim();
                    prefManager.savePrefs(USER_NAME, name);
                }

                try {
                    reg  = true;
                    webServiceRequest(POST, getString(R.string.service_url), register(), "register");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                        } else
                dialog.dismiss();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                loginDialog();


            }
        });

        dialog.show();
    }

    void loginDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final TextView reg = dialog.findViewById(R.id.tvregister);
        final TextView reset = dialog.findViewById(R.id.tvforgotpassword);
        final EditText ETphone = dialog.findViewById(R.id.et_contact);
        //EditText email =  dialog.findViewById(R.id.et_email);
        final EditText ETpassword = dialog.findViewById(R.id.et_password);
        //TextView login =  dialog.findViewById(R.id.tvlogin);
        Button submit = dialog.findViewById(R.id.btn_submit);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDialog();
                dialog.dismiss();


            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restDialog();
                dialog.dismiss();


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log = true;
                if (TextUtils.isEmpty(ETphone.getText().toString()))
                    ETphone.setError("Please enter phone number");
                else {
                    contact = ETphone.getText().toString().trim();
                    prefManager.savePrefs(USER_PHONE_NUMBER, contact);
                }
                if (TextUtils.isEmpty(ETpassword.getText().toString()))
                    ETpassword.setError("Please enter password");
                else
                    password = ETpassword.getText().toString().trim();


                try {
                    webServiceRequest(POST, getString(R.string.service_url), login(), "register");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                        } else
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    void restDialog() {final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.otp);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);
                       final EditText ETphone =  dialog.findViewById(R.id.et_contact);
                        //EditText email =  dialog.findViewById(R.id.et_email);
                       final EditText ETpassword =  dialog.findViewById(R.id.et_password);
                        //TextView login =  dialog.findViewById(R.id.tvlogin);
                        Button submit =  dialog.findViewById(R.id.btn_submit);
        Button cancel = dialog.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDialog();
                dialog.dismiss();


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(ETphone.getText().toString()))
                    ETphone.setError("Please enter phone number");
                else {
                    contact = ETphone.getText().toString().trim();
                    prefManager.savePrefs(USER_PHONE_NUMBER, contact);
                }

                try {
                    webServiceRequest(POST, getString(R.string.service_url), otpReq(), "forgot_password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    void subscribeDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout bg = dialog.findViewById(R.id.image);
        TextView login = dialog.findViewById(R.id.tvlogin);
        Button yes = dialog.findViewById(R.id.btn_yes);
        Button no = dialog.findViewById(R.id.btn_close);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                dialog.dismiss();
                nsv.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                t2.setVisibility(View.GONE);

                cont.setVisibility(View.VISIBLE);
                desc.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
                button.setVisibility(View.GONE);

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


            }
        });

        dialog.show();
    }

    void otpDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.otp_pass);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final EditText ETotp = dialog.findViewById(R.id.et_otp);
        //EditText email =  dialog.findViewById(R.id.et_email);
        final EditText ETpassword = dialog.findViewById(R.id.et_password);
        TextView resend =  dialog.findViewById(R.id.tvresend);
        Button submit = dialog.findViewById(R.id.btn_submit);
        Button cancel = dialog.findViewById(R.id.btn_cancel);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    webServiceRequest(POST, getString(R.string.service_url), otpReq(), "forgot_password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regDialog();
                dialog.dismiss();


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(ETotp.getText().toString()))
                    ETotp.setError("Please enter phone number");
                else {
                    OTP = ETotp.getText().toString().trim();
                }
                if (TextUtils.isEmpty(ETpassword.getText().toString()))
                    ETpassword.setError("Please enter phone number");
                else {
                    password = ETpassword.getText().toString().trim();
                }
                try {
                    webServiceRequest(POST, getString(R.string.service_url), resetOtpReq(), "change_password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();



            }
        });

                        dialog.show();



    }

    boolean validate() {
        boolean valid;
        if (contact == null || password == null || email == null) {
            valid = false;
        } else
            valid = true;


        return valid;
    }

    public JSONObject getCostsReq() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "get_payment_configs");
        return dataW;
    }

    public JSONObject searchId(String acct) throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put("doc_unique_id", acct);
        dataItem.put("userid", Integer.valueOf(prefManager.loadPrefs(USER_ID, "")));
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "view_document_by_unique_ref");
        return dataW;
    }
    public JSONObject pickUpCode(String pickupcode) throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put("pickupcode", Integer.valueOf(pickupcode));
        //dataItem.put("doc_num",Integer.valueOf(prefManager.loadPrefs(DOC_REF, "")));
        dataItem.put("doc_ref", prefManager.loadPrefs(DOC_REF, ""));
        dataItem.put("userid", Integer.valueOf(prefManager.loadPrefs(USER_ID, "")));
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "view_doc_pickup_code");
        return dataW;
    }

    public JSONObject subscribeT0(String acct, String docType) throws JSONException {
        //TODO make email alerts default. only add sms as an option. remove email checkbox.
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put(getString(R.string.doc_ref), acct);
        dataItem.put("doc_type", docType);
        dataItem.put("user_ref", prefManager.loadPrefs(USER_ID, ""));
        JSONArray list = new JSONArray(notificationType);
        dataItem.put("notification_type", list);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "subscribe");
        Log.e("test", dataW.toString(4));
        return dataW;
    }

    public JSONObject register() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put(getString(R.string.contact), contact);
        dataItem.put(getString(R.string.email), email);
        dataItem.put("password", password);
        dataItem.put("reg_by", name);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), getString(R.string.register_user));
        Log.e("test", dataW.toString(4));
        return dataW;
    }

    public JSONObject login() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put(getString(R.string.contact), contact);
        dataItem.put("password", password);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "mobile_login");
        Log.e("test", dataW.toString(4));
        return dataW;
    }

    public JSONObject searcHistoryReq() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put("user", Integer.valueOf(prefManager.loadPrefs(USER_ID, "")));
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "get_user_search_history");
        Log.e("test", dataW.toString(4));
        return dataW;
    }

    public JSONObject otpReq() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put(getString(R.string.contact), contact);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "forgot_password");
        Log.e("test", dataW.toString(4));
        return dataW;
    }
    public JSONObject resetOtpReq() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        dataItem.put("user_id", prefManager.loadPrefs(USER_ID, ""));
        dataItem.put("otp", OTP);
        dataItem.put("password", password);
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "change_password");
        Log.e("test", dataW.toString(4));
        return dataW;
    }

    private String modifyNumber(String num) {
        String str = "";
        String formatted = "";
        Log.e(TAG + "aaaaaaaaa", num);
        str = "254"+num.substring(1);
        Log.e(TAG + "aaaaaaaaa", str);
        if(str.length() < 12 || str.length() > 12)
            Utils.dialogConfig(this, "Invalid number. Kindly make sure the number prefix is \" 254 \" and the number is 12 digits long. ");
        else
          formatted = str;
        return formatted;
    }

    public JSONObject payment() throws JSONException {
        JSONObject dataW = new JSONObject();
        JSONObject dataItem = new JSONObject();
        String formatted = null;
        dataItem.put("contact",  modifyNumber(prefManager.loadPrefs(USER_PHONE_NUMBER, "")));
        dataItem.put("doc_ref", prefManager.loadPrefs(DOC_REF, ""));
        dataW.put(getString(R.string.data), dataItem);
        dataW.put(getString(R.string.command), "make_mpesa_payment");
        Log.e("test", dataW.toString(4));
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
                            if (reqIdentifier.equalsIgnoreCase("search_document"))
                                docSearchResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("register"))
                                registerResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("subscribe"))
                                subscribeResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("payment"))
                                paymentResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("forgot_password"))
                                otpResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("change_password"))
                                chngePassResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("pick_up_code"))
                                pickUpCodeResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("charges"))
                                chargesResponse(response);
                            else if (reqIdentifier.equalsIgnoreCase("history"))
                                searchHistoryResponse(response);


                        } catch (Exception ex) {
                            Log.e(TAG + " error", ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                createErrorDetailDialog(MainActivity.this, error, "Error").show();
                Log.e(TAG, error.toString());
            }
        });
//        SingletonRequestQueue.getInstance().addToRequestQueue(jor,"search_doc");
        SingletonRequestQueue.getInstance(mContext).addToRequestQueue(jor);
    }

    public void searchHistoryResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            transfersList = new ArrayList<HashMap<String, String>>();

            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                JSONArray search_history = jsonObject.getJSONArray(getString(R.string.result));
                prefManager.savePrefs(SEARCH_HISTORY, search_history.toString());
                searchHistoryDialog();
            }else
            {
                Utils.dialogErrorConfig(this, "Invalid code. Try again.");
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void chargesResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            transfersList = new ArrayList<HashMap<String, String>>();
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                JSONObject result = jsonObject.getJSONObject(getString(R.string.result));
                String payment_amount = result.getString("payment_amount");
                String subscription_amount = result.getString("subscription_amount");
                prefManager.savePrefs(COSTNOT, payment_amount);
                prefManager.savePrefs(COSTSUB, subscription_amount);
                tvcbsms.setText("Via SMS at (KSh."+prefManager.loadPrefs(COSTSUB, "")+")");



            }else
            {
                Utils.dialogErrorConfig(this, "Invalid code. Try again.");
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void pickUpCodeResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            transfersList = new ArrayList<HashMap<String, String>>();
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                JSONObject documentDetails = jsonObject.getJSONObject(getString(R.string.result));
                JSONObject location = documentDetails.getJSONObject("location");
                JSONArray coordinates = location.getJSONArray("coordinates");
                String lat = String.valueOf(coordinates.getDouble(0));
                String lon = String.valueOf(coordinates.getDouble(1));
                Log.e("lat", lat);
                Log.e("lon", lon);
                String doc_unique_id = documentDetails.getString("doc_unique_id");
                String doc_fname = documentDetails.getString("doc_fname");
                String doc_lname = documentDetails.getString("doc_lname");
                String doc_name = documentDetails.getString("doc_name");
                String created_by = documentDetails.getString("created_by");
                String foundby_contact = documentDetails.getString("foundby_contact");
                String createddate = documentDetails.getString("createddate");
                String pickuplocation = documentDetails.getString("pick_up_location");
                prefManager.savePrefs(PreferenceManager.DOC_ID, doc_unique_id);
                prefManager.savePrefs(DOC_FNAME, doc_fname);
                prefManager.savePrefs(DOC_LNAME, doc_lname);
                prefManager.savePrefs(DOC_NAME, doc_name);
                prefManager.savePrefs(PICKEDBY, created_by);
                prefManager.savePrefs(PICKEDBYCONTACT, foundby_contact);
                prefManager.savePrefs(LAT, lat);
                prefManager.savePrefs(LON, lon);
                prefManager.savePrefs(PICKUPLOCATION, pickuplocation);
                prefManager.savePrefs(DATEFOUND, createddate);
                Intent intent = new Intent(mActivity, ShowPickUpLocation.class);
                startActivity(intent);




            }else
            {
                Utils.dialogErrorConfig(this, "Invalid pick up code. Enter the correct pick up code.");
            }
            }catch(Exception e){
            Log.e(TAG, e.getMessage());
            }
        }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void docSearchResponse(JSONObject jsonObject) {
        pd.dismiss();
        hideKeyboard(this);
        hideKeyboard(MainActivity.this);
        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
        try {
            Log.e(TAG, jsonObject.toString());
            transfersList = new ArrayList<HashMap<String, String>>();
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                JSONObject documentList = jsonObject.getJSONObject(getString(R.string.result));
                JSONObject doc = documentList.getJSONObject("doc");
                JSONObject location = doc.getJSONObject("location");
                JSONArray coordinates = location.getJSONArray("coordinates");
                String lat = String.valueOf(coordinates.getDouble(0));
                String lon = String.valueOf(coordinates.getDouble(1));
                Log.e("lat", lat);
                Log.e("docList", documentList.toString(4));
                String uniqueDocId = doc.getString("doc_unique_id");
                String doc_type = doc.getString("doc_type");
                String doc_name = doc.getString("doc_name");
                String doc_details = doc.getString("doc_details");
                String doc_fname = doc.getString("doc_fname");
                String doc_lname = doc.getString("doc_lname");
                String doc_num = String.valueOf(doc.getString("doc_num"));
                JSONArray doc_images = documentList.getJSONArray("doc_images");
                String image_path = "";
                for (int i = 0; i < doc_images.length(); i++) {
                    image_path = doc_images.getJSONObject(i).getString("image_path");
                    Log.e("image", image_path);
                }
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("DocId", uniqueDocId);
                temp.put("DocName", doc_name);
                temp.put("doc_fname",doc_fname);
                temp.put("doc_lname", doc_lname);
                temp.put("DocType", doc_type);
                temp.put("DocDetails", doc_details);
                temp.put("DocImage", image_path);
                prefManager.savePrefs(DOC_ID, uniqueDocId);
                prefManager.savePrefs(DOC_TYPE, doc_type);
                prefManager.savePrefs(DOC_FNAME, doc_fname);
                prefManager.savePrefs(DOC_LNAME, doc_lname);
                prefManager.savePrefs(DOC_NAME, doc_name);
                prefManager.savePrefs(DOC_REF, doc_num);
                prefManager.savePrefs(DOC_DETAILS, doc_details);
                transfersList.add(temp);


                DocSearchAdapter adapter = new DocSearchAdapter(this, transfersList);
                adapter.setCustomButtonListner(this);
                rv.setAdapter(adapter);
                setListViewHeightBasedOnChildren(rv);
                rv.deferNotifyDataSetChanged();

                if (documentList.length() == 0) {
                    notFound = true;
                    hideKeyboard(this);
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
                    Window window = MainActivity.this.getWindow();
                    if (Build.VERSION.SDK_INT >= 21)
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                    toolbar.setBackgroundResource(R.color.yellow);
                    nsv.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    header.setVisibility(View.INVISIBLE);
                    desc.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.GONE);

                    //desc.setText(getString(R.string.no_results) +" "+idQuery);
                    tv.setText("\"" + idQuery + "\"");
                    rv.setVisibility(View.GONE);
                } else {
                    Window window = MainActivity.this.getWindow();
                    notFound = false;
                    cont.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= 21)
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.skyblue));
                    toolbar.setBackgroundResource(R.color.skyblue);
                    desc.setVisibility(View.VISIBLE);
                    nsv.setVisibility(View.GONE);
                    desc.setText("Your Document Has Been Found. Click to see more");
                    rv.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    hideKeyboard(this);
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sv.getApplicationWindowToken(), 0);
                }
            } else {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
//                String respMsg = jsonObject.getString(getString(R.string.statusname));
//                Utils.dialogConfig(this, respMsg);
//                notFound = true;
//                Window window = MainActivity.this.getWindow();
//                if (Build.VERSION.SDK_INT >= 21)
//                    window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
//                toolbar.setBackgroundResource(R.color.yellow);
                notFound = true;
                Window window = MainActivity.this.getWindow();
                if (Build.VERSION.SDK_INT >= 21)
                    window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                toolbar.setBackgroundResource(R.color.yellow);
                nsv.setVisibility(View.VISIBLE);
                header.setVisibility(View.GONE);
                desc.setVisibility(View.INVISIBLE);
                button.setVisibility(View.GONE);
                notFound = true;
                //desc.setText(getString(R.string.no_results) +" "+idQuery);
                tv.setText("\"" + idQuery + "\"");
                rv.setVisibility(View.GONE);

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    public void registerResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e("t", jsonObject.toString(4));
            if(log) {
                if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                    regResponse();
                    JSONObject result = jsonObject.getJSONObject(getString(R.string.result));
                    JSONObject user = result.getJSONObject(getString(R.string.user));

                    prefManager.saveBoolean(IS_LOGGED_IN, true);
                    Log.e("t", user.getString("user_id"));
                    USERID = user.getString("user_id");
                    prefManager.savePrefs(USER_ID, USERID);
                    if(user.has("reg_by")) {
                        prefManager.savePrefs(USER_NAME, user.getString("reg_by"));
                        String full = user.getString("reg_by");
                        String edited = full.substring(0, 1);
                        Log.e("REGBY", edited);
                        Log.e("REGBY_FULL", full);
                        Log.e("has regby", "true");
                        headerText.setText(edited);
                        headerNames.setText(full);
                    } else {
                        prefManager.savePrefs(USER_NAME, user.getString("names"));
                        String full = user.getString("names");
                        String edited = full.substring(0, 1);
                        Log.e("rRe", edited);
                        Log.e("rR", full);
                        headerText.setText(edited);
                        headerNames.setText(full);
                    }

                        //prefManager.savePrefs(USER_NAME, user.getString("names") );
//                        headerText.setText(user.getString("names"));
                        JSONArray search_history = result.getJSONArray("search_history");
                        prefManager.savePrefs(SEARCH_HISTORY, search_history.toString());
                        Log.e("!!!!!", search_history.toString());
                    //Toast.makeText(this, jsonObject.getString("statusname"), Toast.LENGTH_SHORT).show();
                    prefManager.savePrefs(USER_ID, USERID);
                   // regResponse();


                } else if (jsonObject.getInt(getString(R.string.statuscode)) == 1) {
                    Utils.dialogErrorConfig(this, jsonObject.getString("error_message"));
                    notFound = true;
                    Window window = MainActivity.this.getWindow();
                    if (Build.VERSION.SDK_INT >= 21)
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                    toolbar.setBackgroundResource(R.color.yellow);

                }
            } else if(reg){
                if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                    int result = jsonObject.getInt(getString(R.string.result));
                    Log.e("uid", String.valueOf(result));

                    USERID = String.valueOf(result);
                    regResponse();
                } else if (jsonObject.getInt(getString(R.string.statuscode)) == 1) {
                    Utils.dialogErrorConfig(this, jsonObject.getString("error_message"));
                    notFound = true;
                    Window window = MainActivity.this.getWindow();
                    if (Build.VERSION.SDK_INT >= 21)
                        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                    toolbar.setBackgroundResource(R.color.yellow);

                }

            }

        } catch (Exception e) {

        }

    }

    void regResponse(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        TextView text = dialog.findViewById(R.id.text);
        //prefManager.setUserId(USERID);

        Log.e("&&&&&", prefManager.loadPrefs(USER_ID, ""));

        if (reg)
            text.setText("Registration successful");
        else if (log) {
            text.setText("Login successful");
//            name = user.getString("names");
            //Toast.makeText(mActivity, name, Toast.LENGTH_SHORT).show();
        }
        log = false;
        reg = false;
        Button submit = dialog.findViewById(R.id.btn_next);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log = false;
                reg = false;
                if (foundID) {
                    dialog.dismiss();
                    Intent intent = new Intent(mActivity, CreateDocsOne.class);
                    if(name!=null){
                        prefManager.savePrefs(USER_NAME, name);
                    }
                    startActivity(intent);
                }else if(searchCheck){
                    dialog.dismiss();
                    searchCheck = false;
                } else if(subscribe) {
                    subscribe = false;
                    dialog.dismiss();
                    subCont();
                } else{
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    void subCont() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_id_type);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        final Spinner docTypeSpinner = dialog.findViewById(R.id.spinner_doc_type);
        Button submit = dialog.findViewById(R.id.btn_next);
        ArrayList<String> TypeList = new ArrayList<String>();
        TypeList.add("Tap to select type");
        TypeList.add(getString(R.string.national_id));
        TypeList.add(getString(R.string.passport));
        TypeList.add(getString(R.string.dl));
        TypeList.add(getString(R.string.others));
        typeMap = new HashMap<>();
        typeMap.put(getString(R.string.national_id), "NATIONAL_ID");
        typeMap.put(getString(R.string.passport), "PASSPORT");
        typeMap.put(getString(R.string.dl), "DRIVING_LICENSE");
        typeMap.put(getString(R.string.others), "OTHERS");
        ArrayAdapter<String> arrayAdapterLeaves = new ArrayAdapter<>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, TypeList);
        docTypeSpinner.setAdapter(arrayAdapterLeaves);
        docTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    DOCTYPE = typeMap.get(docTypeSpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                try {
                    webServiceRequest(POST, getString(R.string.service_url), subscribeT0(idQuery, DOCTYPE), "subscribe");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        dialog.show();
    }

    void error(){
        notFound = true;
        Window window = MainActivity.this.getWindow();
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
        toolbar.setBackgroundResource(R.color.yellow);
    }

    void success(){
        notFound = false;

        Window window = MainActivity.this.getWindow();
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.skyblue));
        toolbar.setBackgroundResource(R.color.skyblue);

    }

    public void subscribeResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                success();
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.success_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                TextView text = dialog.findViewById(R.id.text);
                text.setText("Request sent successfully. You will receive an sms once we process your subscription.");
                Button submit = dialog.findViewById(R.id.btn_next);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        notFound = false;
                        Window window = MainActivity.this.getWindow();
                        if (Build.VERSION.SDK_INT >= 21)
                            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.skyblue));
                        toolbar.setBackgroundResource(R.color.skyblue);
                        desc.setVisibility(View.VISIBLE);
                        nsv.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);


                    }
                });

                dialog.show();
            } else {
                String respMsg = jsonObject.getString(getString(R.string.statusname));
                Utils.dialogErrorConfig(this, respMsg);
                error();

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    public void otpResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                String result = String.valueOf(jsonObject.getInt("result"));
                prefManager.savePrefs(USER_ID, result);
                otpDialog();
                success();
            } else {
                String respMsg = jsonObject.getString(getString(R.string.statusname));
                Utils.dialogErrorConfig(this, respMsg);
                error();

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    public void chngePassResponse (JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString());
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                success();
                String result = jsonObject.getString(getString(R.string.result));
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.success_dialog);
                TextView transferResp = (TextView) dialog.findViewById(R.id.text);
                transferResp.setText(result);
                dialog.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        loginDialog();
                    }
                });
                dialog.show();


            } else {
                String respMsg = jsonObject.getString("Error");
                Utils.dialogErrorConfig(this, respMsg);
                error();

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    public void paymentResponse(JSONObject jsonObject) {
        pd.dismiss();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        try {
            Log.e(TAG, jsonObject.toString(4));
            if (jsonObject.getInt(getString(R.string.statuscode)) == SUCCESS) {
                //TODO add response
//                "statuscode": 0,
//                        "statusname": "success",
//                        "result": {
//                    "MerchantRequestID": "16812-4047789-1",
//                            "CheckoutRequestID": "ws_CO_DMZ_50144245_13072018015138120",
//                            "ResponseCode": "0",
//                            "ResponseDescription": "Success. Request accepted for processing",
//                            "CustomerMessage": "Success. Request accepted for processing"
                JSONObject result = jsonObject.getJSONObject(getString(R.string.result));
                String msg = result.getString("CustomerMessage");
                Utils.dialogConfig(this, msg);


                success();
            } else {
                String respMsg = jsonObject.getString("error_message");
                Utils.dialogErrorConfig(this, respMsg);
                error();

            }
        } catch (Exception ex) {
            Log.e(TAG + " error", ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
//            resetLayout();
        }
    }

    public void initTransfersMenu(JSONArray productdetails) throws JSONException {
        transfersList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < productdetails.length(); i++) {
            Log.e(TAG, productdetails.toString(4));


        }
        DocSearchAdapter adapter = new DocSearchAdapter(this, transfersList);
        adapter.setCustomButtonListner(this);
        rv.setAdapter(adapter);
        setListViewHeightBasedOnChildren(rv);
        rv.deferNotifyDataSetChanged();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e("Listview Size ", "" + listView.getCount());
        DocSearchAdapter listAdapter = (DocSearchAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onButtonClickListner(int position, String value, String iDType, String firstName,
                                     String secondName, String doB, String image) {
        Log.e("^^^^", image);
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.doc_preview_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().setBackgroundDrawableResource(Color.TRANSPARENT);
        // dialog.setCancelable(false);
        final RelativeLayout bg = dialog.findViewById(R.id.image);
        final com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout bg1 = dialog.findViewById(R.id.bg);
        TextView textIdType = dialog.findViewById(R.id.doc_type);
        TextView textDocNo = dialog.findViewById(R.id.doc_id);
        TextView textDocNames = dialog.findViewById(R.id.doc_names);
        ImageView expand = dialog.findViewById(R.id.expand);
        textIdType.setText(iDType);
        textDocNo.setText("No: " + value);
        textDocNames.setText("Names: " + firstName + "  " + secondName);

        Picasso.with(this)
                .load("http://18.216.65.139:3000"+image)
                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bg1.setBackground(new BitmapDrawable(bitmap));
                bg.setBackground(new BitmapDrawable(bitmap));
                pd.dismiss();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                pd.dismiss();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                pd.show();

            }
        });

        Button cncl = dialog.findViewById(R.id.button_cancel);
        Button btn = dialog.findViewById(R.id.button_pay);
        ImageView docIcon = dialog.findViewById(R.id.doc_icon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                payToViewDialog();


            }
        });
        cncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                subscribeDialog();
            }
        });
        docIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    void payToViewDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pay_to_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button pay = dialog.findViewById(R.id.btn_next);
        Button code = dialog.findViewById(R.id.btn_enter_code);
        Button cncl = dialog.findViewById(R.id.btn_cancel);
        pay.setText("Pay KSH."+prefManager.loadPrefs(COSTNOT, ""));
        cncl.setVisibility(View.GONE);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    webServiceRequest(POST, getString(R.string.service_url), payment(), "payment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                enterCodeDialog();

            }
        });
        cncl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    void searchHistoryDialog(){
        searchHistori = new ArrayList<HashMap<String, String>>();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_history);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button pay = dialog.findViewById(R.id.btn_next);
        if(prefManager.loadPrefs(SEARCH_HISTORY, "")!=null){
        try {
            searchHistory = new JSONArray( prefManager.loadPrefs(SEARCH_HISTORY, ""));
            Log.e(TAG, prefManager.loadPrefs(SEARCH_HISTORY, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{for(int i=0; i < searchHistory.length(); i++){
            HashMap<String, String> temp = new HashMap<String, String>();
            try {
                temp.put("number", searchHistory.getJSONObject(i).getString("doc_ref"));
                temp.put("date", searchHistory.getJSONObject(i).getString("viewdate"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            searchHistori.add(temp);

        }}catch (Exception e){

        }
        }
        ListView lv = dialog.findViewById(R.id.lv);
        SearchHistory adapter = new SearchHistory(this, searchHistori);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public class SearchHistory extends BaseAdapter {

        public ArrayList<HashMap<String, String>> list;
        Activity activity;

        public SearchHistory(Activity activity, ArrayList<HashMap<String, String>> list) {
            super();
            this.activity = activity;
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.history_item, null);
            }

            TextView no = convertView.findViewById(R.id.doc_id);
            TextView date = convertView.findViewById(R.id.doc_type);


            final HashMap<String, String> map = list.get(position);

            no.setText("ID: "+map.get("number"));
            String full = map.get("date");
            String edited = full.substring(0, 10);
            date.setText("Date: "+edited );


            return convertView;
        }


    }

    void enterCodeDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_code);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button pay = dialog.findViewById(R.id.btn_next);
        final EditText etCode = dialog.findViewById(R.id.et_code);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(TextUtils.isEmpty(etCode.getText().toString()))
                    etCode.setError("Please enter pickup code");
                else {

                    try {
                        webServiceRequest(POST, getString(R.string.service_url), pickUpCode(etCode.getText().toString().trim()), "pick_up_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        });
        dialog.show();

    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(Gravity.START) )
            drawer.closeDrawer(GravityCompat.START);
        else if(notFound){
            Window window = MainActivity.this.getWindow();
            if (Build.VERSION.SDK_INT >= 21)
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.skyblue));
            toolbar.setBackgroundResource(R.color.skyblue);
            desc.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            sv.setText("");
            desc.setText(getString(R.string.search_lost_and_found));
            header.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            nsv.setVisibility(View.GONE);
        }else if (nsv.getVisibility() == View.VISIBLE) {
            nsv.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            sv.setText("");
            desc.setText(getString(R.string.search_lost_and_found));
            button.setVisibility(View.VISIBLE);
        }else if (rv.getVisibility() == View.VISIBLE) {
            nsv.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            desc.setVisibility(View.VISIBLE);
            sv.setText("");
            header.setVisibility(View.VISIBLE);
            desc.setText(getString(R.string.search_lost_and_found));
            button.setVisibility(View.VISIBLE);
        } else {
            cont.setVisibility(View.VISIBLE);
            notFound = false;
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        foundID = false;
        log = false;
        reg = false;
        if(notFound){
            Window window = MainActivity.this.getWindow();
            if (Build.VERSION.SDK_INT >= 21)
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
            toolbar.setBackgroundResource(R.color.yellow);

        }
        if(prefManager.loadPrefs(USER_NAME, "")!= ""){
            String full = prefManager.loadPrefs(USER_NAME, "");
            String edited= full.substring(0,1);
            headerText.setText(edited);
            headerNames.setText(full);
            Log.e("re", edited);
            Log.e("rf", full);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cbEmail.setChecked(false);
        cbSms.setChecked(false);
        foundID = false;
        log = false;
        reg = false;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_found_id:
                //searchHistoryDialog();
                foundID = true;
                if(prefManager.loadBoolean(IS_LOGGED_IN, false)) {
                    Intent intent = new Intent(mActivity, CreateDocsOne.class);
                    startActivity(intent);
                } else {
                    regDialog();
                }
                break;
            case R.id.nav_search_history:
              //searchHistoryDialog();
                try {
                    webServiceRequest(POST, getString(R.string.service_url), searcHistoryReq(), "history");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_logout:
                Utils.dialogConfig(this, "Logged out");
                SharedPreferences settings = this.getSharedPreferences("io.ginius.cp.kt.lostfound", Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                headerNames.setText("");
                headerText.setText("");
                button.setVisibility(View.VISIBLE);
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
