package com.example.taper.Models;

public class UserSettings {
    private User user;
    private UserAccountSetting setting;

    public UserSettings(User user, UserAccountSetting setting) {
        this.user = user;
        this.setting = setting;
    }
    public UserSettings() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSetting getSetting() {
        return setting;
    }

    public void setSetting(UserAccountSetting setting) {
        this.setting = setting;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "user=" + user +
                ", setting=" + setting +
                '}';
    }
}
