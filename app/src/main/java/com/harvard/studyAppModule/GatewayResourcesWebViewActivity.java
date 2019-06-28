package com.harvard.studyAppModule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.harvard.webserviceModule.apiHelper.ConnectionDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.crypto.CipherInputStream;

import static android.os.Build.VERSION_CODES.M;

public class GatewayResourcesWebViewActivity extends AppCompatActivity {
    private AppCompatTextView mTitle;
    private RelativeLayout mBackBtn;
    private WebView mWebView;
    private RelativeLayout mShareBtn;
    private AppCompatImageView mShareIcon;
    private PDFView mPdfView;
    //    private String mDownloadedFilePath = "/storage/emulated/0/SamplePDF/";
    private String mDownloadedFilePath;
    private String mFileName;
    private String downloadingFileURL = "";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private String mIntentTitle;
    private String mIntentType;
    private String mIntentContent;
    private File mFinalMSharingFile;
    private ConnectionDetector connectionDetector = new ConnectionDetector(GatewayResourcesWebViewActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_web_view);

        mDownloadedFilePath = "/data/data/" + getPackageName() + "/files/";
        initializeXMLId();
        downloadingFileURL = getIntent().getStringExtra("content");
        mIntentTitle = getIntent().getStringExtra("title");
        mIntentType = getIntent().getStringExtra("type");
        mIntentContent = getIntent().getStringExtra("content");

        defaultPDFShow();
        /////// downloading and show pdf
        /*String title;
        // removing space b/w the string : name of the pdf
        try {
            title = mIntentTitle.replaceAll("\\s+", "");
        } catch (Exception e) {
            title = mIntentTitle;
            e.printStackTrace();
        }
        mFileName = title;

        if (mIntentType.equalsIgnoreCase("pdf")) {
            mWebView.setVisibility(View.GONE);
            mTitle.setText(mIntentTitle);
            // checking the permissions
            if ((ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(permission)) {
                    ActivityCompat.requestPermissions((Activity) GatewayResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        // starting new Async Task for downlaoding pdf file
                        new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                    } else {
                        // offline functionality
                        offLineFunctionality();
                    }
                }
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    // starting new Async Task for downlaoding pdf file
                    new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                } else {
                    // offline functionality
                    offLineFunctionality();
                }
            }
        } else {
            setTextForView();
        }*/

        setFont();
//        setColor();

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setData(Uri.parse("mailto:"));
                    shareIntent.setType("application/pdf");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, mIntentTitle);
                    // old way working
                    /*// if pdf then attach and send else content send
                    if (mIntentType.equalsIgnoreCase("pdf")) {
                        File file = new File(mDownloadedFilePath + mFileName + ".pdf");
                        if (file.exists()) {
                            mFinalMSharingFile = copy(file);
                            Uri fileUri = FileProvider.getUriForFile(GatewayResourcesWebViewActivity.this, "com.myfileprovider", mFinalMSharingFile);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        }
                    } else {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, mIntentContent);
                    }
                    startActivity(shareIntent);
                    */

                    ///////// default pdf show
                    if (mFinalMSharingFile.exists()) {
                        Uri fileUri = FileProvider.getUriForFile(GatewayResourcesWebViewActivity.this, "com.myfileprovider", mFinalMSharingFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        startActivity(shareIntent);
                    }
                    ////////
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void defaultPDFShow() {
        if (mIntentType.equalsIgnoreCase("pdf")) {
            mWebView.setVisibility(View.GONE);
            mTitle.setText(mIntentTitle);
            // checking the permissions
            if ((ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(permission)) {
                    ActivityCompat.requestPermissions((Activity) GatewayResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
                } else {
                    mFinalMSharingFile = getAssetsPdfPath();
                    DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
                }
            } else {
                mFinalMSharingFile = getAssetsPdfPath();
                DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(GatewayResourcesWebViewActivity.this, getResources().getString(R.string.permission_deniedDate), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // old way working
                    /*if (connectionDetector.isConnectingToInternet()) {
                        new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                    } else {
                        Toast.makeText(GatewayResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    }*/

                    ///////// default pdf show
                    mFinalMSharingFile = getAssetsPdfPath();
                    DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
                    /////////
                }
                break;
        }
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.webView);
        mShareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
        mShareIcon = (AppCompatImageView) findViewById(R.id.shareIcon);
        mPdfView = (PDFView) findViewById(R.id.pdfView);
    }

    private void setTextForView() {
        String title = mIntentTitle;
        mTitle.setText(title);
        String webData = mIntentContent;
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

//        String type = getIntent().getStringExtra("type");
//        if (type.equalsIgnoreCase("pdf")) {
        // curently hiding these and shows pdfview
//            String url = "http://docs.google.com/gview?embedded=true&url=" + webData;
//            mWebView.setWebViewClient(new WebViewClient());
//            mWebView.loadUrl(url);

//        } else if (type.equalsIgnoreCase("text")) {
        mWebView.loadData(webData, "text/html; charset=utf-8", "UTF-8");
//        }

    }

    private void setFont() {
        mTitle.setTypeface(AppController.getTypeface(this, "bold"));
    }

    private void setColor() {
        try {
//            Drawable mDrawable = getResources().getDrawable(R.drawable.share1_blue);
//            mDrawable.setColorFilter(new
//                    PorterDuffColorFilter(0x990000, PorterDuff.Mode.DST_OVER));
//            mShareIcon.setBackground(mDrawable);

            mShareIcon.setColorFilter(Color.RED);

        } catch (Exception e) {
            Log.e("rajeesh", "**********  " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public File copy(File src) throws IOException {
//        String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + new Date().getTime() + ".pdf";
        String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mIntentTitle + ".pdf";
//        String filePath = "/data/data/"+getPackageName()+"/files/" + primaryStoragePath + ".pdf";
        File file = new File(primaryStoragePath);
        if (!file.exists())
            file.createNewFile();

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(file);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* try {
            File file = new File(mDownloadedFilePath + mFileName + ".pdf");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            if (mFinalMSharingFile.exists()) {
                mFinalMSharingFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        String downloadUrl = "";
        String filePath = "";
        String fileName = "";

        public DownloadFileFromURL(String downloadUrl, String filePath, String fileName) {
            this.downloadUrl = downloadUrl;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppController.getHelperProgressDialog().showProgress(GatewayResourcesWebViewActivity.this, "", "", false);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(downloadUrl);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
//                OutputStream output = new FileOutputStream("/storage/emulated/0/SamplePDF/downloadedfile.pdf");
                OutputStream output = new FileOutputStream(filePath + fileName + ".pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                // while downloading time, net got disconnected so delete the file
                try {
                    new File(filePath + fileName + ".pdf").delete();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Updating progress bar
         */
        /*protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
        }*/

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            try {
                // downlaod success mean file exist else check offline file
                File file = new File(filePath + fileName + ".pdf");
                if (file.exists()) {
                    AppController.genarateEncryptedConsentPDF(filePath, fileName);
                    DisplayPDFView(filePath + fileName + ".pdf");
                } else {
                    // offline functionality
                    offLineFunctionality();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // dismiss the dialog after the file was downloaded
            AppController.getHelperProgressDialog().dismissDialog();

        }

    }

    private void offLineFunctionality() {
        try {
            // checking encrypted file is there or not?
            File file = new File(mDownloadedFilePath + mFileName + ".txt");
            if (file.exists()) {
                // decrypt the file
                File decryptFile = getEncryptedFilePath(mDownloadedFilePath + mFileName + ".txt");
                DisplayPDFView(decryptFile.getAbsolutePath());
            } else {
                Toast.makeText(GatewayResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(GatewayResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private File getEncryptedFilePath(String filePath) {
        try {
            CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filePath);
            byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
            File file = new File(mDownloadedFilePath + mFileName + ".pdf");
            if (!file.exists() && file == null) {
                file.createNewFile();
            }
            OutputStream output = new FileOutputStream(file);
            output.write(byteArray);
            output.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void DisplayPDFView(String filePath) {
        mPdfView.setVisibility(View.VISIBLE);
        try {
            mPdfView.fromFile(new File(filePath))
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(GatewayResourcesWebViewActivity.this))
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getAssetsPdfPath() {
//        String filePath = "/storage/emulated/0/SamplePDF/downloadedfile.pdf";
//        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + new Date().getTime() + ".pdf";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mIntentTitle + ".pdf";
//        String filePath = "/data/data/"+getPackageName()+"/files/" + primaryStoragePath + ".pdf";
        Log.e("filePath", "" + filePath);


        File destinationFile = new File(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            InputStream inputStream = getAssets().open("pdf/appglossary.pdf");
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Error.");
        }

        return destinationFile;
    }

}
