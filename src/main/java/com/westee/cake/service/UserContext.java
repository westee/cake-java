package com.westee.cake.service;

import com.westee.cake.generate.User;

public class UserContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        setCurrentUser(user);
        return currentUser.get();
    }

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static void clearCurrentUser() {
        currentUser.remove();
    }
}
