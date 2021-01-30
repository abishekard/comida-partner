package com.abishek.comidapartner.loginAndSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "SignUp";
    private TextView btnLogin;
    private Button btnSignUp;
    private EditText edtEmail,edtMobile,edtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnLogin = findViewById(R.id.login);
        btnSignUp  =findViewById(R.id.sign_up);
        edtEmail = findViewById(R.id.edt_email);
        edtName  = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);


        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login: onBackPressed();
                break;
            case R.id.sign_up: //getDataFromUi();
                startActivity(new Intent(SignUp.this,OtpVerification.class));
                break;
        }
    }

    public void getDataFromUi()
    {
        String email = edtEmail.getText().toString();
        String name = edtName.getText().toString();
        String mobile = edtMobile.getText().toString();

        if(name.equals(""))
        {
            Toast.makeText(SignUp.this,"Please enter name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.equals(""))
        {
            Toast.makeText(SignUp.this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.equals(""))
        {
            Toast.makeText(SignUp.this,"Please enter mobile number",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!email.contains("@gmail.com"))
        {
            Toast.makeText(SignUp.this,"Please enter a valid email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobile.length()<10)
        {
            Toast.makeText(SignUp.this,"Please enter a valid mobile",Toast.LENGTH_SHORT).show();
            return;
        }

      //  createAccount(email,name,mobile);

    }

/*    public void createAccount(String email,String name,String mobile)
    {


        btnSignUp.setEnabled(false);
        Log.e(TAG, "SignUp : called");

        final String URL = BASE_SIGN_UP;


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "........."+response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    int status = jsonObject.getInt("status");
                    if(status==200)
                    {
                        startActivity(new Intent(SignUp.this,OtpVerification.class).putExtra("email",email));
                        Toast.makeText(SignUp.this,"Account created and Otp sent to your email",Toast.LENGTH_SHORT).show();
                        finish();
                        return;

                    }
                    if(status==300)
                    {
                        Toast.makeText(SignUp.this,"email or mobile is already taken",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(SignUp.this,"Something went wrong",Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                Toast.makeText(SignUp.this,"server problem",Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("name",name);
                params.put("mobile",mobile);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(SignUp.this).addToRequestQueue(stringRequest);


    }*/
}