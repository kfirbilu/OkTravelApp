package com.example.oktravelapplictaion.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;

import java.security.SecureRandom;
import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.oktravelapplictaion.R;
import com.example.oktravelapplictaion.feed.MainActivity;
import com.example.oktravelapplictaion.model.Model;
import com.example.oktravelapplictaion.model.User;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class LoginFragment extends Fragment {
    Button login_btn;
    Button signup_btn;
    EditText username_et;
    EditText password_et;
    ProgressBar progressBar;
    SharedPreferences sp;
    LoginButton fbBtn;
    CallbackManager callbackManager;
    final static int RC_SIGN_IN = 1000;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc ;
    SignInButton ggBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        username_et = view.findViewById(R.id.username_te_login_fragment);
        password_et = view.findViewById(R.id.password_te_login_fragment);
        progressBar = view.findViewById(R.id.progressBar_login_fragment_pb);
        progressBar.setVisibility(View.GONE);

        ggBtn = view.findViewById(R.id.gmail_login_btn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc  = GoogleSignIn.getClient(getActivity(), gso);

        ggBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                String userPassword = longstring();
                getGmailData((email, userPhotoUrl)->{
                    String name = email.split("@")[0];
                    HashMap<String,String> map = new HashMap<>();
                    map.put("userName",name);
                    Model.instance.checkIfUserExist(map, new Model.GetUserByUserNameByListener() {
                        @Override
                        public void onComplete(User user) {
                            if(user != null){
                                toFeedActivity(user.getUserName());
                            }
                            else{
                                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToGmailMissingDataFragment(email,userPhotoUrl,userPassword));
                            }
                        }
                    });
                });
            }
        });

        fbBtn = view.findViewById(R.id.facebook_login_Btn);
        callbackManager = CallbackManager.Factory.create();

        fbBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userId = loginResult.getAccessToken().getUserId();
                String userPassword = longstring();
                requestData((email,url)->{
                    String name = email.split("@")[0];
                    HashMap<String,String> map = new HashMap<>();
                    map.put("userName",name);
                    Model.instance.checkIfUserExist(map, new Model.GetUserByUserNameByListener() {
                        @Override
                        public void onComplete(User user) {
                            if(user != null){
                                checkApps(user.getUserName());
                            }
                            else{
                                Navigation.findNavController(view).navigate(LoginFragmentDirections.actionLoginFragmentToFBMissingDataFragment(email,url,userId,userPassword));
                            }
                        }
                    });
                });
            }
            @Override
            public void onCancel() {
            }

            @Override
            public void onError(@NonNull FacebookException e) {
            }
        });

        signup_btn = view.findViewById(R.id.signup_btn_login_fragment);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment());
            }
        });
        login_btn = view.findViewById(R.id.login_btn_login_fragment);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserexist();
            }
        });
        return view;
    }

    public interface GetUserDataListener {
        void onComplete(String userEmail, String url);
    }
    public interface GetUserDataListenerGmail {
        void onComplete(String userEmail, String userPhotoUrl);
    }


    public void getGmailData(GetUserDataListenerGmail listener){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account != null)
            listener.onComplete(account.getEmail(),account.getPhotoUrl().toString());
    }

    public void requestData(GetUserDataListener listener){
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String url = getImageUrl(response);
                            listener.onComplete(email, url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private static String FACEBOOK_FIELD_PICTURE = "picture";
    private static String FACEBOOK_FIELD_DATA = "data";
    private static String FACEBOOK_FIELD_URL = "url";
    private String getImageUrl(GraphResponse response) {
        String url = null;
        try {
            url = response.getJSONObject()
                    .getJSONObject(FACEBOOK_FIELD_PICTURE)
                    .getJSONObject(FACEBOOK_FIELD_DATA)
                    .getString(FACEBOOK_FIELD_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private void toFeedActivity(String userName) {
        sp.edit().putBoolean("logged", true).apply();
        sp.edit().putString("userName", userName).apply();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    public void checkApps(String username){
        progressBar.setVisibility(View.VISIBLE);
        login_btn.setEnabled(false);
        signup_btn.setEnabled(false);
        HashMap <String,String> map = new HashMap<>();
        map.put("userName",username);
        Model.instance.loginApps(map, tokenmap -> {
            if (tokenmap != null){

                sp.edit().putString("accessToken",tokenmap.get("accessToken")).apply();
                sp.edit().putString("refreshToken",tokenmap.get("refreshToken")).apply();
                toFeedActivity(username);
            }
            else{
                Toast.makeText(getContext(), "one of the details wrong", Toast.LENGTH_LONG).show();
                login_btn.setEnabled(true);
                signup_btn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void checkIfUserexist() {
        progressBar.setVisibility(View.VISIBLE);
        login_btn.setEnabled(false);
        signup_btn.setEnabled(false);
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        HashMap <String,String> map = new HashMap<>();
        map.put("userName",username);
        map.put("password", password);
        Model.instance.checkLogin(map, tokenmap -> {
            if (tokenmap != null){

                sp.edit().putString("accessToken",tokenmap.get("accessToken")).apply();
                sp.edit().putString("refreshToken",tokenmap.get("refreshToken")).apply();
                toFeedActivity(username);
            }
            else{
                Toast.makeText(getContext(), "one of the details wrong", Toast.LENGTH_LONG).show();
                login_btn.setEnabled(true);
                signup_btn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }
    private String longstring(){
        final String allowedChars = "abcdefghijklmnopqrstuvwABCDEFGHIJKLMNOP0123456789!§$%&?*+#";
        SecureRandom random = new SecureRandom();
        StringBuilder pass = new StringBuilder();

        for (int i = 0; i < 15; i++)
        {
            pass.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }

        return pass.toString();
    }
}