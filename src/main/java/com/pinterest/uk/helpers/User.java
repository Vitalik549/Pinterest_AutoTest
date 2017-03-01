package com.pinterest.uk.helpers;

public class User {

    public String firstName;
    public String lastName;

    public String password;
    public String login;
    public String email;

    public String getFullNaming() {
        if (firstName == null || lastName == null) {
            return login;
        } else {
            return firstName + " " + lastName;
        }
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}