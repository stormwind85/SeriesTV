package fr.eni.campus.series.seriestv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UtilsLogin {
    public static Boolean isUserLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean logged = prefs.getBoolean("isUserLoggedIn", false);
        return logged;
    }

    public static void setUserLoggedIn(Context context, Boolean isLoggedIn)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", isLoggedIn);
        editor.commit();
    }

    public static void logout(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isUserLoggedIn", false);
        editor.commit();
    }

    public static void saveUsernameAndPassword(Context context, String username, String password)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("login", username);
        editor.putString("password", password);
        editor.commit();
    }

    public static String hashPassword(String password){
        byte[] byteChain = null;
        MessageDigest md = null;

        try {
            byteChain = password.getBytes("UTF-8");
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e("hash","Erreur d'encodage du mot de passe");
        }

        byte[] hash = md.digest(byteChain);

        return hash.toString();
    }
}
