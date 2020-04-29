package com.harvard;

public class AppConfig {

    public static String API_TOKEN = com.harvard.BuildConfig.apikey;
    public static String PackageName = BuildConfig.APPLICATION_ID;
    static String GateWay = "gateway";
    static String Standalone = "standalone";
    public static String AppType = GateWay;
    public static String StudyId = "Demo2";
    public static boolean isGatewayResourceRequired = false;
    public static boolean isStudyConsentRequired = false;
    public static boolean isStudyConsentRequiredInOverview = false;


    //AppId
    public static String APP_ID_KEY = "applicationId";
    public static String APP_ID_VALUE = "CCFSIBD001";
    //OrgId
    public static String ORG_ID_KEY = "orgId";
    public static String ORG_ID_VALUE = "CACFND";
}
