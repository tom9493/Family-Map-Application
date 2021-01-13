package edu.byu.cs240.FamilyMapClient.ui;

import android.os.AsyncTask;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import edu.byu.cs240.FamilyMapClient.model.DataCache;
import edu.byu.cs240.FamilyMapClient.net.ServerProxy;

public class LoginAsyncTask extends AsyncTask<Object, Integer, Object> {

    private final ServerProxy sp = new ServerProxy();

    @Override
    protected Object doInBackground(Object... o) {
        sp.setServerHostName(LoginFragment.serverHost);
        sp.setServerPortNumber(Integer.parseInt(LoginFragment.serverPort));
        
        if (o[0].getClass() == LoginRequest.class) {
            LoginRequest r = (LoginRequest) o[0];
            LoginResult result = sp.login(r);
            if (result.isSuccess()) {
                fillDataCache(result.getAuthToken());
                DataCache.makeFamilyTreeLists();
                DataCache.makeFilteredEvents();
                DataCache.makePersonEvents();
            }
            return result;
        }

        else {
            RegisterRequest r = (RegisterRequest) o[0];
            RegisterResult result = sp.register(r);
            if (result.isSuccess()) {
                fillDataCache(result.getAuthToken());
                DataCache.makeFamilyTreeLists();
                DataCache.makeFilteredEvents();
                DataCache.makePersonEvents();
            }
            return result;
        }
    }

    protected void fillDataCache(String authToken) {
        sp.getAllEvents(authToken);
        sp.getAllPeople(authToken);
    }

}
