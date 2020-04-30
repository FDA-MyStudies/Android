package com.harvard;

public class AppConfig {

    public static String PackageName = BuildConfig.APPLICATION_ID;
    public static String API_TOKEN = com.harvard.BuildConfig.apikey;
    static String GateWay = "gateway";
    static String Standalone = "standalone";
    public static String AppType = GateWay;
    public static String StudyId = "Demo2";
    public static boolean isGatewayResourceRequired = true;
    public static boolean isStudyConsentRequired = true;
    public static boolean isStudyConsentRequiredInOverview = true;


    //AppId
    public static String APP_ID_KEY = "applicationId";
    public static String APP_ID_VALUE = "FMSA001";
    //OrgId
    public static String ORG_ID_KEY = "orgId";
    public static String ORG_ID_VALUE = "FDAHPH";
}
