package com.itesm.application.service.activity;

public final class ActivityActions {

    private ActivityActions() {
    }

    public static final String LOGIN = "LOGIN";

    public static final String CREATE_USER = "CREATE_USER";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String DEACTIVATE_USER = "DEACTIVATE_USER";
    public static final String REACTIVATE_USER = "REACTIVATE_USER";
    public static final String CHANGE_USER_PASSWORD = "CHANGE_USER_PASSWORD";

    public static final String COMPARE_STATES = "COMPARE_STATES";
    public static final String COMPARE_MUNICIPALITIES = "COMPARE_MUNICIPALITIES";

    public static final String UPLOAD_BATCH_CREATED = "UPLOAD_BATCH_CREATED";
    public static final String UPLOAD_BATCH_COMPLETED = "UPLOAD_BATCH_COMPLETED";
    public static final String UPLOAD_BATCH_FAILED = "UPLOAD_BATCH_FAILED";
}