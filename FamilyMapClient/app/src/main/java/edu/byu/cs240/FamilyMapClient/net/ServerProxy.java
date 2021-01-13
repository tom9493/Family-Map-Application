package edu.byu.cs240.FamilyMapClient.net;

import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import Handle.EncoderDecoder;
import Handle.ReadWrite;
import RequestResult.EventResult;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.PersonResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import edu.byu.cs240.FamilyMapClient.model.DataCache;

public class ServerProxy {

    public static String serverHostName;
    public static int serverPortNumber;

    public void setServerHostName(String name) { serverHostName = name; }
    public void setServerPortNumber(int number) { serverPortNumber = number;}

    public LoginResult login(LoginRequest r) {
        Log.i("ServerProxy", "Entering login function");
        LoginResult loginResult = new LoginResult();

        try {
            String requestData = EncoderDecoder.decode(r);
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber +
                    "/user/login");

            Log.i("serverHostName", serverHostName);
            Log.i("serverPortNumber", Integer.toString(serverPortNumber));

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Content-Type", "application/json");

            http.connect();
            Log.i("ServerProxy", "Finished http.connect() in login function");

            OutputStream requestBody = http.getOutputStream();
            ReadWrite.writeString(requestData, requestBody);
            requestBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = ReadWrite.readString(respBody);
                loginResult = (LoginResult) EncoderDecoder.encode(respData, loginResult);

                Log.i("ServerProxy", "Success! Returning login result");
                return loginResult;
            }
            else { System.out.println("Error: " + http.getResponseMessage()); }
        } catch (Exception e) { e.printStackTrace(); }
        return loginResult;
    }

    public RegisterResult register(RegisterRequest r) {
        Log.i("ServerProxy", "Entering register function");
        RegisterResult registerResult = new RegisterResult();

        try {
            String requestData = EncoderDecoder.decode(r);
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber +
                    "/user/register");

            Log.i("serverHostName: ", serverHostName);
            Log.i("serverPortNumber: ", Integer.toString(serverPortNumber));

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.addRequestProperty("Content-Type", "application/json");

            http.connect();
            Log.i("ServerProxy", "Finished http.connect() in register function");

            OutputStream requestBody = http.getOutputStream();
            ReadWrite.writeString(requestData, requestBody);
            requestBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = ReadWrite.readString(respBody);
                registerResult = (RegisterResult) EncoderDecoder.encode(respData, registerResult);

                System.out.println("register Result after http connect: " + registerResult.getMessage());
                Log.i("ServerProxy", "Success! Returning registerResult");
                return registerResult;
            }
            else { System.out.println("Error: " + http.getResponseMessage()); }
        } catch (Exception e) { e.printStackTrace(); }
        return registerResult;
    }

    public void getAllPeople(String authToken) {
        Log.i("ServerProxy", "Entering getAllPeople function");
        PersonResult personResult = new PersonResult();

        try {
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/person");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");

            http.connect();
            Log.i("ServerProxy", "Finished http.connect() in getAllPeople function");

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = ReadWrite.readString(respBody);
                personResult = (PersonResult) EncoderDecoder.encode(respData, personResult);

                Log.i("ServerProxy", "Success! Setting people");
                DataCache.setAllPeople(personResult.getPeople());
                Log.i("ServerProxy", "People set...");

            }
            else { System.out.println("ERROR: " + http.getResponseMessage()); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void getAllEvents(String authToken) {
        Log.i("ServerProxy", "Entering getAllEvents function");
        EventResult eventResult = new EventResult();
        try {
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/event");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");

            http.connect();
            Log.i("ServerProxy", "Finished http.connect() in getAllEvents function");

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = ReadWrite.readString(respBody);
                eventResult = (EventResult) EncoderDecoder.encode(respData, eventResult);

                Log.i("ServerProxy", "Success! Setting events");
                DataCache.setAllEvents(eventResult.getEvents());
                Log.i("ServerProxy", "Events set...");
            }
            else { System.out.println("ERROR: " + http.getResponseMessage()); }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
