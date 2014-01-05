package com.dyanote.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dyanote.android.utils.JsonUtils;
import com.dyanote.android.utils.NetworkUtils;

import java.util.Map;

public class User implements Parcelable {
    private String token;
    private String email;

    // Create new User with the settings value
    public static User loadFromSettings(SharedPreferences settings) {
        String email = settings.getString("email", new String());
        String token = settings.getString("token", new String());
        return new User(email, token);
    }

    // Remove remembered user from settings
    public static void forgetSettings(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("token");
        editor.remove("email");
        editor.commit();
    }

    public User() {
    }

    public User(String email, String token) {
        this.email = email;
        this.token = token;
    }

    private User(Parcel parcel) {
        this.token = parcel.readString();
        this.email = parcel.readString();
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean login(String password, Context c) {
        // curl -X POST -d "client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=password&username=YOUR_USERNAME&password=YOUR_PASSWORD" http://localhost:8000/oauth2/access_token/
        // {"access_token": "<your-access-token>", "scope": "read", "expires_in": 86399, "refresh_token": "<your-refresh-token>"}

        String url = c.getString(R.string.login_url);
        String data = String.format("client_id=%s&client_secret=%s&grant_type=password&username=%s&password=%s",
                c.getString(R.string.client_id), c.getString(R.string.client_secret), email, password);

        String response = NetworkUtils.post(url, data);
        Map<String, String> json = JsonUtils.parseObject(response);

        boolean isLoginSuccessful = false;
        if(json.containsKey("access_token")) {
            token = json.get("access_token");
            Log.i("Network", "Login successful:" + token);
            return true;
        } else if(json.containsKey("error") && json.get("error") == "invalid_grant") {
            Log.w("Network", "Wrong credentials");
        } else {
            Log.w("Network", "Strange response to login request: " + response);
        }
        return false;
    }

    public boolean isLoggedIn() {
        // TODO: improve this check.
        return !token.isEmpty() && !email.isEmpty() && token.length() > 0;
    }

    public boolean exists() {
        // TODO: check if account exists.
        return false;
    }

    public void saveToSettings(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token);
        editor.putString("email", email);
        editor.commit();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(token);
        parcel.writeString(email);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
