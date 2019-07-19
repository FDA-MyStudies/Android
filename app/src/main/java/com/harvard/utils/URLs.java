package com.harvard.utils;


public class URLs {


    public static String BASE_URL_PRODUCTION_WCP_SERVER = "";
//        public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "http://192.168.0.44:8080/StudyMetaData/";
//        public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "https://hphci-fdama-te-wcp-01.labkey.com/StudyMetaData/"; //test
//    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "http://23.89.199.27:8080/StudyMetaData/";
    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "https://hpwcp-stage.lkcompliant.net/StudyMetaData/";
//    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "http://192.168.0.44:8080/StudyMetaData/";
//    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "https://hphci-fdama-st-wcp-01.labkey.com/StudyMetaData/"; //UAT
//    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "http://192.168.0.50:8080/StudyMetaData-DEV/";
//    public static String BASE_URL_DEVELOPMENT_WCP_SERVER = "httpA://192.168.0.32:8080/StudyMetaData/";
    public static String BASE_URL_PRODUCTION_REGISTRATION_SERVER = "";
//    public static String BASE_URL_DEVELOPMENT_REGISTRATION_SERVER = "http://192.168.0.44:8085/labkey/fdahpUserRegWS/";
        public static String BASE_URL_DEVELOPMENT_REGISTRATION_SERVER = "https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/"; //test
//    public static String BASE_URL_DEVELOPMENT_REGISTRATION_SERVER = "https://hphci-fdama-st-ur-01.labkey.com/fdahpUserRegWS/"; //UAT
    public static String BASE_URL_PRODUCTION_RESPONSE_SERVER = "";
//        public static String BASE_URL_DEVELOPMENT_RESPONSE_SERVER = "https://hphci-fdama-te-ds-01.labkey.com/"; //test
//    public static String BASE_URL_DEVELOPMENT_RESPONSE_SERVER = "https://hphci-fdama-st-ds-01.labkey.com/"; //UAT
    public static String BASE_URL_DEVELOPMENT_RESPONSE_SERVER = "https://hpresp-stage.lkcompliant.net/"; //UAT

    /**
     * Registration Server
     */
    public static String LOGIN = "login.api";
    public static String REGISTER_USER = "register.api";
    public static String RESEND_CONFIRMATION = "resendConfirmation.api";
    public static String CONFIRM_REGISTER_USER = "verify.api";
    public static String FORGOT_PASSWORD = "forgotPassword.api";
    public static String CHANGE_PASSWORD = "changePassword.api";
    public static String GET_USER_PROFILE = "userProfile.api";
    public static String UPDATE_USER_PROFILE = "updateUserProfile.api";
    public static String UPDATE_USER_PREFERENCE = "updatePreferences.api";
    public static String USER_PREFERENCE = "userPreferences.api";
    public static String UPDATE_STUDY_PREFERENCE = "updateStudyState.api";
    public static String UPDATE_ACTIVITY_PREFERENCE = "updateActivityState.api";
    public static String LOGOUT = "logout.api";
    public static String CONSENT_METADATA = "eligibilityConsent";
    public static String DELETE_ACCOUNT = "deactivate.api";
    public static String ACTIVITY = "activity";
    public static String STUDY_STATE = "studyState.api";
    public static String ACTIVITY_STATE = "activityState.api";
    public static String WITHDRAW = "withdraw.api";
    public static String UPDATE_ELIGIBILITY_CONSENT = "updateEligibilityConsentStatus.api";
    public static String REFRESH_TOKEN = BASE_URL_DEVELOPMENT_REGISTRATION_SERVER + "refreshToken.api";
    /**
     * WCP server
     */
    public static String STUDY_INFO = "studyInfo";
    public static String STUDY_LIST = "studyList";
    public static String SPECIFIC_STUDY = "study";
    public static String STUDY_UPDATES = "studyUpdates";
    public static String ACTIVITY_LIST = "activityList";
    public static String CONTACT_US = "contactUs";
    public static String FEEDBACK = "feedback";
    public static String RESOURCE_LIST = "resources";
    public static String APP_UPDATES = "appUpdates";
    public static String NOTIFICATIONS = "notifications";
    public static String DASHBOARD_INFO = "studyDashboard";
    /**
     * Response server
     */

    public static String VALIDATE_ENROLLMENT_ID = "mobileappstudy-validateenrollmenttoken.api?";

    public static String ENROLL_ID = "mobileappstudy-enroll.api?";
    public static String GET_CONSENT_DOC = "consentDocument";

    public static String GET_TERMS_AND_CONDITION = "termsPolicy";
    public static String PROCESS_RESPONSE = "mobileappstudy-processResponse.api";

    public static String CONSENTPDF = "consentPDF.api";

    public static String WITHDRAWFROMSTUDY = "withdrawFromStudy";
    public static String PROCESSRESPONSEDATA = BASE_URL_DEVELOPMENT_RESPONSE_SERVER + "mobileappstudy-executeSQL.api?";
}