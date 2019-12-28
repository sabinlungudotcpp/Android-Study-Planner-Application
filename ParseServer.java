package com.example.twitterclone;
import android.app.Application;
import com.parse.Parse;

public class ParseServer extends Application {
    @Override
    public void onCreate() { // Override the method
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
    }
}
