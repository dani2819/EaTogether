package com.applicoders.msp_2017_project.eatogether;

/**
 * Created by rafay on 3/8/2017.
 */

public class Constants {
    Constants(){

    }

    public static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoicmFmYUBnbWFpbC5jb20iLCJpYXQiOjE0OTIyMjA0MzgsImV4cCI6MTUwMDg2MDQzOH0.fFhy7ukKS2Oq03o-t7YF6jT2mkSQY51JecEfwAI_APo";

    public static final String TOKEN_PREF = "TOKEN";
    public static final String PROFILE_IMAGE_LINK = "PROFILE_IMAGE";

    public static final String User_First_Name_PREF = "FirstName";
    public static final String User_Last_Name_PREF = "LastName";
    public static final String User_Email_PREF = "Email";
    public static final String User_Phone_PREF = "Phone";
    public static final String User_Gender_PREF = "Gender";
    public static final String User_Bio_PREF = "Bio";

    public static final String SERVER_HOST = "http://eatogather.herokuapp.com/";

    public static final int SERVER_PORT = 0;

    public static final String MyPREFERENCES = "MyPrefs" ;

    public static final String SERVER_RESOURCE_LOGIN = "users/authenticate";

    public static final String SERVER_RESOURCE_LOGOUT = "logout";

    public static final String SERVER_RESOURCE_SIGNUP = "users/signup";


    public static final String SERVER_RESOURCE_UPDATE = "users/update";

    public static final String SERVER_RESOURCE_HOST = "hosts/create";

    public static final String SERVER_RESOURCE_NEARBY = "hosts";

}
