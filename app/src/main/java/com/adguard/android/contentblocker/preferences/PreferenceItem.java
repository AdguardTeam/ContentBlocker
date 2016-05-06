package com.adguard.android.contentblocker.preferences;

/**
 *
 */
public class PreferenceItem {
    public String name;
    public String title;
    public String summary;
    public Object value;

    public PreferenceItem(String name, String title, String summary, Object value) {
        this.name = name;
        this.title = title;
        this.summary = summary;
        this.value = value;
    }
}
