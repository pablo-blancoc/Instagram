package com.example.instagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    public String getComment() {
        return getString(KEY_TEXT);
    }

    public void setComment(String comment) {
        put(KEY_TEXT, comment);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST, post);
    }

}

