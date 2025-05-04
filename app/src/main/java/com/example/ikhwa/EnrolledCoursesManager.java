package com.example.ikhwa;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class EnrolledCoursesManager {

    private static final String PREF_NAME = "EnrolledCoursesPref";

    public static void enrollCourse(Context context, String uid, String courseTitle) {
        if (uid == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> courses = prefs.getStringSet(uid, new HashSet<>());
        Set<String> updatedCourses = new HashSet<>(courses);
        updatedCourses.add(courseTitle);
        prefs.edit().putStringSet(uid, updatedCourses).apply();
    }

    public static Set<String> getEnrolledCourses(Context context, String uid) {
        if (uid == null) return new HashSet<>();

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(uid, new HashSet<>());
    }
}
