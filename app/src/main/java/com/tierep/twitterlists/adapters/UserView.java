package com.tierep.twitterlists.adapters;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import twitter4j.User;

/**
 * Created by pieter on 03/02/15.
 */
public class UserView implements Serializable {

    public User user;
    public int actionDrawableId;

    public UserView(User user, int actionDrawableId) {
        this.user = user;
        this.actionDrawableId = actionDrawableId;
    }

    public static LinkedList<UserView> convertFromUsers(List<User> users, int actionDrawableId) {
        LinkedList<UserView> userViews = new LinkedList<>();
        for (User u : users) {
            userViews.add(new UserView(u, actionDrawableId));
        }

        return userViews;
    }
}
