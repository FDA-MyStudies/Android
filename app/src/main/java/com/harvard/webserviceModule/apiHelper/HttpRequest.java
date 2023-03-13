package com.harvard.webserviceModule.apiHelper;

import android.os.Build;
import android.util.Base64;
import com.google.gson.Gson;
import com.harvard.AppConfig;
import com.harvard.FDAApplication;
import com.harvard.R;
import com.harvard.utils.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;

public class HttpRequest {

    private static String basicAuth = AppConfig.API_TOKEN;
    private static final String SESSION_EXPIRED = "session expired";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String CONNECTION = "Connection";
    private static final String LANGUAGE = "language";
    private static final String KEEPALIVE = "keep-alive";
    private static final String RESPONSE = "Response";
    private static final String HTTP_NOT_OK = "http_not_ok";
    private static final String STATUS_MESSAGE = "StatusMessage";
    private static final String ENGLISH = "english";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String SERVER_ERROR = "server error";
    private static final String SUCCESS =  "success";
    private static final String TIMEOUT =  "timeout";
    private static final String DELETE =   "DELETE";








    /**
     * To make a Get request
     *
     * @param url          -->  url path
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    public static Responsemodel getRequest(String url, HashMap<String, String> mHeadersData, String serverType) {
        StringBuffer response = new StringBuffer();
        Responsemodel responseModel = new Responsemodel();
        String responsee;
        String responseData;
        HttpURLConnection urlConnection;
        int responseCode = 0;
        try {
            URL obj = new URL(url);
            urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(180000);// 3 min timeout
            urlConnection.setConnectTimeout(180000);// 3 min timeout
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                urlConnection.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                urlConnection.setRequestProperty(CONNECTION, KEEPALIVE);
                urlConnection.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }else if(serverType.equalsIgnoreCase(RESPONSE)){
                urlConnection.setRequestProperty(CONNECTION, KEEPALIVE);
                urlConnection.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            urlConnection.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
            urlConnection.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    urlConnection.setRequestProperty(keyValue, value);
                }
            }
            try {
                // Will throw IOException if server responds with 401.
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now CONNECTION has the correct internal state.
                responseCode = urlConnection.getResponseCode();
            }
            if (serverType.equalsIgnoreCase(RESPONSE)) {
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                } catch (IOException e) {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    e.printStackTrace();
                }
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                urlConnection.disconnect();
                responsee = response.toString();
                responseData = response.toString();
            }
            else {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    urlConnection.disconnect();
                    responsee = response.toString();
                    responseData = response.toString();
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    responseData = "";
                    responsee = SESSION_EXPIRED;
                } else {
                    responseData = "";
                    responsee = HTTP_NOT_OK;
                }
            }
            if (urlConnection.getHeaderField(STATUS_MESSAGE) != null) {
                if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                    responseModel.setServermsg(base64Decode(urlConnection.getHeaderField(STATUS_MESSAGE)));
                }else {
                    responseModel.setServermsg(urlConnection.getHeaderField(STATUS_MESSAGE));
                }
                String loc = urlConnection.getHeaderField(STATUS_MESSAGE);
                byte [] locbytes = new byte[loc.length()];
                for (int index = 0; index < locbytes.length; index++)
                {
                    locbytes[index] = (byte) loc.charAt(index);

                }
            }
            else if (responseCode != HttpURLConnection.HTTP_OK && urlConnection.getHeaderField(STATUS_MESSAGE) == null) {
                responseModel.setServermsg(SERVER_ERROR);
            }
            else {
                responseModel.setServermsg(SUCCESS);
            }
        }
        catch (ConnectException e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            responseData = TIMEOUT;
            responsee = TIMEOUT;
            e.printStackTrace();
        } catch (Exception e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            responsee = "";
            responseData = "";
            e.printStackTrace();
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(responsee);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * To make post request using hashmap
     *
     * @param url          --> url path
     * @param params       --> Hashmap params
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    public static Responsemodel postRequestsWithHashmap(String url, HashMap<String, String> params, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            if (params.size() > 0) {
                conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            }
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }else if(serverType.equalsIgnoreCase(RESPONSE)){
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
            conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);

            if (mHeadersData != null) {

                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            if (params.size() > 0) {
                writer.write(getPostDataString(params));
            }

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            }
            catch (IOException e) {
                e.printStackTrace();
                // Will return 401, because now CONNECTION has the correct internal state.
                responseCode = conn.getResponseCode();
            }
            if (serverType.equalsIgnoreCase(RESPONSE)) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    e.printStackTrace();
                }

                while ((line = br.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
            }
            else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = SESSION_EXPIRED;
                    responseData = "";
                } else {
                    responseData = "";
                    response = HTTP_NOT_OK;
                }
            }

            if (conn.getHeaderField(STATUS_MESSAGE)!= null) {
                if(!Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)) {
                    responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                }else{
                    responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                }
            }
            else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField(STATUS_MESSAGE) == null) {
                responseModel.setServermsg(SERVER_ERROR);
            } else {
                responseModel.setServermsg(SUCCESS);
            }
        }
        catch (SocketTimeoutException e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            responseData = "";
            response = TIMEOUT;
            e.printStackTrace();
        } catch (Exception e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            responseData = "";
            response = "";
            e.printStackTrace();
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * To make post request using json object
     *
     * @param urlpath      -->url path
     * @param jsonObject   -->json object
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    static Responsemodel makePostRequestWithJson(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }else if(serverType.equalsIgnoreCase(RESPONSE)){
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
            conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now CONNECTION has the correct internal state.
                responseCode = conn.getResponseCode();
            }

            if (serverType.equalsIgnoreCase(RESPONSE)) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    e.printStackTrace();
                }
                while ((line = br.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
            } else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = SESSION_EXPIRED;
                    responseData = "";
                } else {
                    response = HTTP_NOT_OK;
                    responseData = "";
                }
            }
            if (conn.getHeaderField(STATUS_MESSAGE) != null) {
                if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                    responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                }else {
                    responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                }

            } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField(STATUS_MESSAGE) == null) {
                responseModel.setServermsg(SERVER_ERROR);
            } else {
                responseModel.setServermsg(SUCCESS);
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = TIMEOUT;
            responseData = "";
            e.printStackTrace();
        } catch (Exception e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = "";
            responseData = "";
            e.printStackTrace();
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * To make post request using json object
     *
     * @param urlpath      -->url path
     * @param jsonObject   -->json object
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    static Responsemodel makePostRequestWithJsonRefreshToken(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        StringBuilder response = new StringBuilder();
        StringBuilder responseData = new StringBuilder();
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            else if(serverType.equalsIgnoreCase(RESPONSE)){
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
            conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now CONNECTION has the correct internal state.
                responseCode = conn.getResponseCode();
            }

            if (serverType.equalsIgnoreCase(RESPONSE)) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    e.printStackTrace();
                }
                while ((line = br.readLine()) != null) {
                    response.append(line);
                    responseData.append(line);
                }
            } else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                        responseData.append(line);
                    }
                } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    response = new StringBuilder(SESSION_EXPIRED);
                    responseData = new StringBuilder();
                } else {

                    response = new StringBuilder(HTTP_NOT_OK);
                    responseData = new StringBuilder();
                }
            }
            if (conn.getHeaderField(STATUS_MESSAGE) != null) {
                if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                    responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                }else {
                    responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                }

            } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField(STATUS_MESSAGE) == null) {
                responseModel.setServermsg(SERVER_ERROR);
            } else {
                responseModel.setServermsg(SUCCESS);
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = new StringBuilder(TIMEOUT);
            responseData = new StringBuilder();
            e.printStackTrace();
        } catch (Exception e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = new StringBuilder();
            responseData = new StringBuilder();
            e.printStackTrace();
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response.toString());
        responseModel.setResponseData(responseData.toString());
        return responseModel;
    }

    /**
     * method to parse hashmap to json
     *
     * @param params --> params of hash map
     * @return String
     */
    private static String getPostDataString(HashMap<String, String> params) {


        return new Gson().toJson(params);
    }


    /**
     * To make post request for form data and files upload
     *
     * @param urlPath  --> url path
     * @param headers  --> null if no header
     * @param formData --> null if no form data
     * @param files    --> null if no files
     * @return web-service response as String
     */
    static Responsemodel postRequestMultipart(String urlPath, HashMap<String, String> headers, HashMap<String, String> formData, HashMap<String, File> files, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        HttpURLConnection httpConn;
        String response = "";
        String responseData = "";
        String LINE_FEED = "\r\n";
        int responseCode = 0;
        try {
            URL url = new URL(urlPath);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setReadTimeout(180000);
            httpConn.setConnectTimeout(180000);
            httpConn.setRequestProperty(CONTENT_TYPE, "multipart/form-data;");
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                httpConn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                httpConn.setRequestProperty(CONNECTION, KEEPALIVE);
                httpConn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }else if(serverType.equalsIgnoreCase(RESPONSE)){
                httpConn.setRequestProperty(CONNECTION, KEEPALIVE);
                httpConn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
            }
            httpConn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
            httpConn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
            OutputStream outputStream = httpConn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            if (headers != null) {
                Set keys = headers.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    writer.append(i.next() + ": " + headers.get(i.next())).append(LINE_FEED);
                    writer.flush();
                }
            }
            if (formData != null) {
                Set keys = formData.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    writer.append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + i.next() + "\"").append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(formData.get(i.next())).append(LINE_FEED);
                    writer.flush();
                }
            }
            if (files != null) {
                Set keys = files.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    String fileName = files.get(i.next()).getName();
                    writer.append("Content-Disposition: form-data; name=\"" + i.next() + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();
                    FileInputStream inputStream = null;
                    try{
                        inputStream = new FileInputStream(files.get(i.next()));
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        assert inputStream != null;
                        inputStream.close();
                    }

                    writer.append(LINE_FEED);
                    writer.flush();
                }
            }
            writer.close();
            // checks server's status code first
            responseCode = httpConn.getResponseCode();
            if (serverType.equalsIgnoreCase(RESPONSE)) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                } catch (IOException e) {
                    reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
                    e.printStackTrace();
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
                reader.close();
                httpConn.disconnect();
            } else {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                    reader.close();
                    httpConn.disconnect();
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = SESSION_EXPIRED;
                    responseData = "";
                } else {
                    response = HTTP_NOT_OK;
                    responseData = "";
                }
            }
            if (httpConn.getHeaderField(STATUS_MESSAGE) != null) {
                if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                    responseModel.setServermsg(base64Decode(httpConn.getHeaderField(STATUS_MESSAGE)));
                }else {
                    responseModel.setServermsg(httpConn.getHeaderField(STATUS_MESSAGE));
                }
            } else if (responseCode != HttpURLConnection.HTTP_OK && httpConn.getHeaderField(STATUS_MESSAGE) == null) {
                responseModel.setServermsg(SERVER_ERROR);
            } else {
                responseModel.setServermsg(SUCCESS);
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = TIMEOUT;
            responseData = "";
            e.printStackTrace();
        } catch (Exception e) {
            responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
            response = "";
            responseData = "";
            e.printStackTrace();
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }


    /**
     * To make DELETE request using hashmap
     *
     * @param url          --> url path
     * @param params       --> Hashmap params
     * @param mHeadersData --> null if no header
     * @param serverType
     * @return Responsemodel
     */

    static Responsemodel deleteRequestsWithHashmap(String url, HashMap<String, String> params, HashMap<String, String> mHeadersData, String serverType) {

        Responsemodel responseModel = new Responsemodel();
        StringBuilder response = new StringBuilder();
        StringBuilder responseData = new StringBuilder();
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                url1 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod(DELETE);
                conn.setDoInput(true);
                conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);

                conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();
                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now CONNECTION has the correct internal state.
                    responseCode = conn.getResponseCode();
                }

                InputStream is = null;
                if (responseCode >= 200 && responseCode < 400) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }
                String line;
                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        e.printStackTrace();
                    }
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                        responseData.append(line);
                    }
                    br.close();
                    conn.disconnect();
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                            responseData.append(line);
                        }
                        br.close();
                        conn.disconnect();
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = new StringBuilder();
                        response = new StringBuilder(SESSION_EXPIRED);
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }
                if (conn.getHeaderField(STATUS_MESSAGE) != null) {
                    if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                        responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                    }else {
                        responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                    }

                } else {
                    responseModel.setServermsg(SUCCESS);
                }

            } catch (SocketTimeoutException e) {
                responseData = new StringBuilder();
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                response = new StringBuilder(TIMEOUT);
                e.printStackTrace();
            } catch (Exception e) {
                responseData = new StringBuilder();
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                response = new StringBuilder();
                e.printStackTrace();
            }
        } else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);
                OwnHttpDelete httppost = new OwnHttpDelete(url);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                httppost.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                httppost.addHeader(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                httppost.addHeader(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);

                StringEntity params1 = new StringEntity(getPostDataString(params));
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try(
                                InputStreamReader ir = new InputStreamReader(instream);
                                BufferedReader br = new BufferedReader(ir)
                        ) {
                            // do something useful
                            while ((line = br.readLine()) != null) {
                                responseData.append(line);
                                response.append(line);
                            }
                            br.close();
                        } finally {
                            instream.close();
                        }
                    }
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try(InputStreamReader ir = new InputStreamReader(instream);
                                BufferedReader br = new BufferedReader(ir)
                            ) {
                                while ((line = br.readLine()) != null) {
                                    responseData.append(line);
                                    response.append(line);
                                }
                                br.close();
                            } finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = new StringBuilder();
                        response = new StringBuilder(SESSION_EXPIRED);
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }

                if (response1.getFirstHeader(STATUS_MESSAGE) != null) {
                    responseModel.setServermsg(response1.getFirstHeader(STATUS_MESSAGE).getValue());
                } else {
                    responseModel.setServermsg(SUCCESS);
                }

            } catch (ConnectTimeoutException|SocketTimeoutException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response.toString());
        responseModel.setResponseData(responseData.toString());
        return responseModel;
    }

    static Responsemodel makeDeleteRequestWithJson(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {

        Responsemodel responseModel = new Responsemodel();
        StringBuilder response = new StringBuilder();
        StringBuilder responseData = new StringBuilder();
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                url1 = new URL(urlpath);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod(DELETE);
                conn.setDoInput(true);
                conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
                if (serverType.equalsIgnoreCase("WCP")) {
                    String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                    conn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                    conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
                }else if(serverType.equalsIgnoreCase(RESPONSE)){
                    conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
                }
                conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonObject.toString());

                writer.flush();
                writer.close();
                os.close();

                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now CONNECTION has the correct internal state.
                    responseCode = conn.getResponseCode();
                }
                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    String line;
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        e.printStackTrace();
                    }
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                        responseData.append(line);
                    }
                } else {
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                            responseData.append(line);
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        response = new StringBuilder(SESSION_EXPIRED);
                        responseData = new StringBuilder();
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }
                if (conn.getHeaderField(STATUS_MESSAGE) != null) {
                    if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                        responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                    }else {
                        responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                    }
                } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField(STATUS_MESSAGE) == null) {
                    responseModel.setServermsg(SERVER_ERROR);
                } else {
                    responseModel.setServermsg(SUCCESS);
                }
            } catch (SocketTimeoutException e) {
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                responseData = new StringBuilder();
                response = new StringBuilder(TIMEOUT);
                e.printStackTrace();
            } catch (Exception e) {
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                responseData = new StringBuilder();
                response = new StringBuilder();
                e.printStackTrace();
            }
        }
        else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);
                OwnHttpDelete httppost = new OwnHttpDelete(urlpath);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                if(serverType.equalsIgnoreCase(RESPONSE)){
                    httppost.addHeader(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
                }
                httppost.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                httppost.addHeader(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                httppost.addHeader(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
                StringEntity params1 = new StringEntity(jsonObject.toString());
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try(InputStreamReader ir = new InputStreamReader(instream);
                            BufferedReader br = new BufferedReader(ir)
                        ) {
                            while ((line = br.readLine()) != null) {
                                responseData.append(line);
                                response.append(line);
                            }
                            br.close();
                        } finally {
                            instream.close();
                        }
                    }
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try (InputStreamReader ir = new InputStreamReader(instream);
                                 BufferedReader br = new BufferedReader(ir)
                            ){
                                while ((line = br.readLine()) != null) {
                                    responseData.append(line);
                                    response.append(line);
                                }
                            } finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = new StringBuilder();
                        response = new StringBuilder(SESSION_EXPIRED);
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }

                if (response1.getFirstHeader(STATUS_MESSAGE) != null) {
                    responseModel.setServermsg(response1.getFirstHeader(STATUS_MESSAGE).getValue());
                } else {
                    responseModel.setServermsg(SUCCESS);
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response.toString());
        responseModel.setResponseData(responseData.toString());
        return responseModel;
    }

    static Responsemodel makeDeleteRequestWithJsonArray(String urlpath, JSONArray jsonArray, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        StringBuilder response = new StringBuilder();
        StringBuilder responseData = new StringBuilder();
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                url1 = new URL(urlpath);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod(DELETE);
                conn.setDoInput(true);
                conn.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
                if (serverType.equalsIgnoreCase("WCP")) {
                    String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                    conn.setRequestProperty(AUTHORIZATION, BASIC + encoding);
                    conn.setRequestProperty(CONNECTION, KEEPALIVE);
                    conn.setRequestProperty(LANGUAGE, AppController.deviceDisplayLanguage(Locale.getDefault().getDisplayLanguage()));
                }
                conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                conn.setRequestProperty(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonArray.toString());

                writer.flush();
                writer.close();
                os.close();

                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now CONNECTION has the correct internal state.
                    responseCode = conn.getResponseCode();
                }
                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    String line;
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        e.printStackTrace();
                    }
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                        responseData.append(line);
                    }
                } else {
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                            responseData.append(line);
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        response = new StringBuilder(SESSION_EXPIRED);
                        responseData = new StringBuilder();
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }
                if (conn.getHeaderField(STATUS_MESSAGE) != null) {
                    if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(ENGLISH)){
                        responseModel.setServermsg(base64Decode(conn.getHeaderField(STATUS_MESSAGE)));
                    }else {
                        responseModel.setServermsg(conn.getHeaderField(STATUS_MESSAGE));
                    }
                } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField(STATUS_MESSAGE) == null) {
                    responseModel.setServermsg(SERVER_ERROR);
                } else {
                    responseModel.setServermsg(SUCCESS);
                }
            } catch (SocketTimeoutException e) {
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                responseData = new StringBuilder();
                response = new StringBuilder(TIMEOUT);
                e.printStackTrace();
            } catch (Exception e) {
                responseModel.setServermsg(FDAApplication.getInstance().getResources().getString(R.string.network_exception));
                responseData = new StringBuilder();
                response = new StringBuilder();
                e.printStackTrace();
            }
        } else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);
                OwnHttpDelete httppost = new OwnHttpDelete(urlpath);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                httppost.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                httppost.addHeader(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);
                httppost.addHeader(AppConfig.ORG_ID_KEY, AppConfig.ORG_ID_VALUE);
                StringEntity params1 = new StringEntity(jsonArray.toString());
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase(RESPONSE)) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try (InputStreamReader ir = new InputStreamReader(instream);
                             BufferedReader br = new BufferedReader(ir)
                        ){
                            while ((line = br.readLine()) != null) {
                                responseData.append(line);
                                response.append(line);
                            }
                            br.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            instream.close();
                        }
                    }
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try(
                                    InputStreamReader ir = new InputStreamReader(instream);
                                    BufferedReader br = new BufferedReader(ir)
                            ) {
                                while ((line = br.readLine()) != null) {
                                    responseData.append(line);
                                    response.append(line);
                                }
                                br.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = new StringBuilder();
                        response = new StringBuilder(SESSION_EXPIRED);
                    } else {
                        responseData = new StringBuilder();
                        response = new StringBuilder(HTTP_NOT_OK);
                    }
                }

                if (response1.getFirstHeader(STATUS_MESSAGE) != null) {
                    responseModel.setServermsg(response1.getFirstHeader(STATUS_MESSAGE).getValue());
                } else {
                    responseModel.setServermsg(SUCCESS);
                }

            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response.toString());
        responseModel.setResponseData(responseData.toString());
        return responseModel;
    }

    private static class OwnHttpDelete extends HttpPost {
        public static final String METHOD_NAME = DELETE;

        public OwnHttpDelete() {
            super();
        }

        public OwnHttpDelete(URI uri) {
            super(uri);
        }

        public OwnHttpDelete(String uri) {
            super(uri);
        }

        public String getMethod() {
            return METHOD_NAME;
        }


    }
    public static String base64Decode(String message){
        String val="";
        val = new String(Base64.decode(message, Base64.DEFAULT),StandardCharsets.UTF_8);
        return val;
    }

}