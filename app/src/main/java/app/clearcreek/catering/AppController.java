package app.clearcreek.catering;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import app.clearcreek.catering.data.prefs.PreferencesHelper;

public class AppController extends Application {

    private PreferencesHelper preferencesHelper;
    private FirebaseFirestore db;

    private static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesHelper = new PreferencesHelper(this);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);

        instance = this;
    }

    public static FirebaseFirestore getDb() {
        return instance.db;
    }

    public static PreferencesHelper getPreferencesHelper() {
        return instance.preferencesHelper;
    }
}
