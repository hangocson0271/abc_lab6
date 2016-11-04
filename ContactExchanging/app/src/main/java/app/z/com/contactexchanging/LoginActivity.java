package app.z.com.contactexchanging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import app.z.com.contactexchanging.HttpHandler.HttpHandler;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogin;
    TextView txtRegister;
    EditText edUser,edPassword;
    String username,userPass;

    String messErr;
    ProgressDialog pDialog;
    String url_get_token ;
    String url_login ;
    String key_wow,id_wow;

    String userName,userPhone,userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        pDialog = new ProgressDialog(this);

        id_wow = getString(R.string.id_wow_contatcs);
        key_wow =getString(R.string.k_wow_contacts);
        url_get_token = getString(R.string.url_get_token);
        url_login = getString(R.string.url_check_pass_login);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        txtRegister =(TextView)findViewById(R.id.changeRegister);

        edUser = (EditText)findViewById(R.id.user_email);
        edPassword =(EditText)findViewById(R.id.user_password1);

        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                checklogin();
                break;
            case R.id.changeRegister:

                change2Register();
                break;
        }
    }

    void change2Register(){
        Intent IT = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(IT);
    }

    void chang2Pro5(){
        Intent IT2 = new Intent(LoginActivity.this,ProfileUser.class);

        startActivity(IT2);
    }


    void checklogin(){

        username = edUser.getText().toString();
        userPass= edPassword.getText().toString();

        new GetContacts().execute();
    }

    public class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {


                HttpHandler httpHandler = new HttpHandler(url_login);
                HttpHandler tk = new HttpHandler(url_get_token);

                tk.addParameter("client_id",id_wow);
                tk.addParameter("client_secret",key_wow);
                tk.addParameter("grant_type","client_credentials");

                tk.execute(HttpHandler.RequestMethod.POST);
                String jsonToken = tk.getResponse();

                String tokenJson,tokenType;
                JSONObject jsonObject = new JSONObject(jsonToken);
                tokenJson = jsonObject.optString("access_token");
                tokenType = jsonObject.optString("token_type");

                httpHandler.addHeader("Authorization",tokenType+" "+tokenJson);
                httpHandler.addParameter("password","123456789abcA");
                httpHandler.addParameter("email","abc@gmail.com");

                httpHandler.execute(HttpHandler.RequestMethod.POST);

                String jsonStatus =httpHandler.getResponse();
                JSONObject jsonObject1 = new JSONObject(jsonStatus);
                String status = jsonObject1.optString("status");

                if(status.equals("true")==true){
                    String result = jsonObject1.optString("result");
                    JSONObject jsonObject2 = new JSONObject(result);

                    userName = jsonObject2.optString("first_name");
                    userPhone =jsonObject2.optString("last_name");
                    userEmail =jsonObject2.optString("email");
                    messErr ="done";
                }
                else {

                    String err = jsonObject1.optString("errors");
                    messErr =err;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
                chang2Pro5();
                Toast.makeText(getBaseContext(),showMess(),Toast.LENGTH_SHORT).show();
        }
    }
    public String getUsername(){
        return userName;
    }
    public String getUserPhone(){
        return userPhone;
    }
    public String getUserEmail(){
        return userEmail;
    }

    public String showMess(){
        return messErr;
    }
}