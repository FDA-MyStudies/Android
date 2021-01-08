package com.harvard;

public class AppConfig {

    public static String PackageName = BuildConfig.APPLICATION_ID;
    public static String API_TOKEN = com.harvard.BuildConfig.apikey;
    static String GateWay = "gateway";
    static String Standalone = "standalone";
    public static String AppType = GateWay;
    public static String StudyId = BuildConfig.StudyId;
    public static boolean isGatewayResourceRequired = true;
    public static boolean isStudyConsentRequired = true;
    public static boolean isStudyConsentRequiredInOverview = true;


    //AppId
    public static String APP_ID_KEY = BuildConfig.APP_ID_KEY;
    public static String APP_ID_VALUE = BuildConfig.APP_ID_VALUE; //live
   //public static String APP_ID_VALUE = "FMSTM001"; //test
    //OrgId
    public static String ORG_ID_KEY = BuildConfig.ORG_ID_KEY;
    public static String ORG_ID_VALUE = BuildConfig.ORG_ID_VALUE;
}
