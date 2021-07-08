package com.example.instagram;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseObject {

    public static final String KEY_BIO = "bio";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_HANDLE = "handle";

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_PICTURE);
    }

    public void setImage(ParseFile image) {
        put(KEY_PICTURE, image);
    }

    public String getHandle() {
        return getString(KEY_HANDLE);
    }

    public void setHandle(String handle) {
        put(KEY_HANDLE, handle);
    }

}
