package edu.byu.cs240.FamilyMapClient.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import edu.byu.cs240.FamilyMapClient.R;

public class LoginFragment extends Fragment {

    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Button signInButton;
    private Button registerButton;
    public static String serverHost;
    public static String serverPort;
    private final MainActivity main;
    LoginFragment(MainActivity m) { main = m; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        serverHostEditText = (EditText)v.findViewById(R.id.serverHostEditText);
        serverPortEditText = (EditText)v.findViewById(R.id.serverPortEditText);
        userNameEditText = (EditText)v.findViewById(R.id.userNameEditText);
        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
        firstNameEditText = (EditText)v.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText)v.findViewById(R.id.lastNameEditText);
        emailEditText = (EditText)v.findViewById(R.id.emailEditText);

        maleRadioButton = (RadioButton)v.findViewById(R.id.radio_male);
        maleRadioButton.setChecked(true);

        femaleRadioButton = (RadioButton)v.findViewById(R.id.radio_female);
        femaleRadioButton.setChecked(false);

        signInButton = (Button)v.findViewById(R.id.login_button);
        signInButton.setEnabled(false);

        registerButton = (Button)v.findViewById(R.id.register_button);
        registerButton.setEnabled(false);

        makeListeners();
        return v;
    }

    public String getServerHost() { return serverHost; }
    public String getServerPort() { return serverPort; }

    public void setServerHost() { serverHost = serverHostEditText.getText().toString(); }
    public void setServerPort() { serverPort = serverPortEditText.getText().toString(); }

    public LoginRequest getLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setUserName(userNameEditText.getText().toString());
        req.setPassword(passwordEditText.getText().toString());
        return req;
    }

    public RegisterRequest getRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setUserName(userNameEditText.getText().toString());
        req.setPassword(passwordEditText.getText().toString());
        req.setFirstName(firstNameEditText.getText().toString());
        req.setLastName(lastNameEditText.getText().toString());
        req.setEmail(emailEditText.getText().toString());
        req.setGender(getGender());
        return req;
    }

    public LoginResult login() {
        LoginResult result = new LoginResult();
        try {
            LoginAsyncTask task = new LoginAsyncTask();
            result = (LoginResult) task.execute(getLoginRequest()).get();
            if (result.isSuccess()) {
                main.setMapsFragment(); }
            else { main.failToastLogin(); }
        } catch (Exception e) { Log.e("LoginFragment", e.getMessage(), e); }
        return result;
    }

    public RegisterResult register() {
        RegisterResult result = new RegisterResult();
        try {
            LoginAsyncTask task = new LoginAsyncTask();
            result = (RegisterResult) task.execute(getRegisterRequest()).get();
            System.out.println("This is the message on the register result: " + result.getMessage());
            if (result.isSuccess()) {
                main.setMapsFragment(); }
            else { main.failToastRegister(); }
        } catch (Exception e) { Log.e("LoginFragment", e.getMessage(), e); }
        return result;
    }

    public String getGender() {
        if (maleRadioButton.isChecked()) { return "m"; }
        else { return "f"; }
    }

    public void checkLoginAbility() {
        setServerHost();
        setServerPort();
        boolean enable = true;
        if (getServerHost().equals("")) { enable = false; }
        if (getServerPort().equals("")) { enable = false; }
        if (userNameEditText.getText().toString().equals("")) { enable = false; }
        if (passwordEditText.getText().toString().equals("")) { enable = false; }
        signInButton.setEnabled(enable);
    }

    public void checkRegisterAbility() {
        setServerHost();
        setServerPort();
        boolean enable = true;
        if (getServerHost().equals("")) { enable = false;  }
        if (getServerPort().equals("")) { enable = false; }
        if (userNameEditText.getText().toString().equals("")) { enable = false; }
        if (passwordEditText.getText().toString().equals("")) { enable = false; }
        if (firstNameEditText.getText().toString().equals("")) { enable = false; }
        if (lastNameEditText.getText().toString().equals("")) { enable = false; }
        if (emailEditText.getText().toString().equals("")) { enable = false; }
        registerButton.setEnabled(enable);
    }

    public void makeListeners() {
        serverHostEditText.addTextChangedListener(getTextWatcher());
        serverPortEditText.addTextChangedListener(getTextWatcher());
        userNameEditText.addTextChangedListener(getTextWatcher());
        passwordEditText.addTextChangedListener(getTextWatcher());
        firstNameEditText.addTextChangedListener(getTextWatcher());
        lastNameEditText.addTextChangedListener(getTextWatcher());
        emailEditText.addTextChangedListener(getTextWatcher());

        maleRadioButton.setOnClickListener(v -> maleRadioButton.setChecked(true));
        femaleRadioButton.setOnClickListener(v -> femaleRadioButton.setChecked(true));
        signInButton.setOnClickListener(v -> { LoginResult r = login(); });
        registerButton.setOnClickListener(v -> { RegisterResult r = register(); });
    }

    public TextWatcher getTextWatcher() {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkLoginAbility();
                checkRegisterAbility();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }
}