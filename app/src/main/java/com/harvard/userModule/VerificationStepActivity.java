package com.harvard.userModule;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.userModule.event.ResendEmailEvent;
import com.harvard.userModule.event.VerifyUserEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

public class VerificationStepActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private AppCompatTextView mVerificationStepsLabel;
    private AppCompatTextView mVerificationEmailMsgLabel;
    private AppCompatTextView mTapBelowTxtLabel;
    private AppCompatTextView mSubmitBtn;
    private AppCompatTextView mHrLine1;
    private AppCompatTextView mCancelTxt;
    private AppCompatTextView mResend;
    private AppCompatEditText mEmailField;
    private AppCompatEditText mVerificationCode;
    private RelativeLayout mBackBtn;
    private RelativeLayout mCancelBtn;
    final int CONFIRM_REGISTER_USER_RESPONSE = 100;
    final int RESEND_CONFIRMATION = 101;
    final int JOIN_STUDY_RESPONSE = 102;
    private String mFrom;
    private String mUserId;
    private String mAuth;
    private boolean isVerified;
    private String mEmailId;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_step);
        mUserId = getIntent().getStringExtra("userid");
        mAuth = getIntent().getStringExtra("auth");
        isVerified = getIntent().getBooleanExtra("verified", false);
        mEmailId = getIntent().getStringExtra("email");
        mFrom = getIntent().getStringExtra("from");
        mType = getIntent().getStringExtra("type");


        initializeXMLId();
        mHrLine1.setVisibility(View.GONE);
        mEmailField.setVisibility(View.GONE);
        setTextForView();
        setFont();

        bindEvents();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mResend = (AppCompatTextView) findViewById(R.id.resend);
        mHrLine1 = (AppCompatTextView) findViewById(R.id.vrLine1);
        mEmailField = (AppCompatEditText) findViewById(R.id.emailField);
        mVerificationCode = (AppCompatEditText) findViewById(R.id.verificationCode);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mVerificationStepsLabel = (AppCompatTextView) findViewById(R.id.verification_steps_label);
        mVerificationEmailMsgLabel = (AppCompatTextView) findViewById(R.id.verification_email_msg_label);
        mTapBelowTxtLabel = (AppCompatTextView) findViewById(R.id.tap_below_txt_label);
        mSubmitBtn = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setTextForView() {
        String msg = "";
        if (mType.equalsIgnoreCase("signup")) {
            msg = getResources().getString(R.string.verification_email_content1) + " " + mEmailId + getResources().getString(R.string.verification_email_content2);
        } else if (mType.equalsIgnoreCase("signin")) {
            msg = getResources().getString(R.string.verification_email_signin);
        } else {
            msg = getResources().getString(R.string.verification_email_forgotpassword) + "(" + mEmailId + ")" + getResources().getString(R.string.verification_email_forgotpassword1);

        }
        mVerificationEmailMsgLabel.setText(msg);
    }

    private void setFont() {
        try {
            mCancelTxt.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "medium"));
            mVerificationStepsLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mVerificationEmailMsgLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mTapBelowTxtLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mSubmitBtn.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mEmailField.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mVerificationCode.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys("passcode");
                if (pass != null)
                    AppController.deleteKey("passcode_"+pass);
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys("passcode");
                if (pass != null)
                    AppController.deleteKey("passcode_"+pass);
                finish();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VerifyUserEvent verifyUserEvent = new VerifyUserEvent();
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> header = new HashMap<String, String>();
                if (mVerificationCode.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(VerificationStepActivity.this, getResources().getString(R.string.validation_code_error), Toast.LENGTH_SHORT).show();
                } else {
                    AppController.getHelperProgressDialog().showProgress(VerificationStepActivity.this, "", "", false);
                    params.put("emailId", mEmailId);
                    params.put("code", mVerificationCode.getText().toString());
                    RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.CONFIRM_REGISTER_USER, CONFIRM_REGISTER_USER_RESPONSE, VerificationStepActivity.this, LoginData.class, params, header, null, false, VerificationStepActivity.this);
                    verifyUserEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                    UserModulePresenter userModulePresenter = new UserModulePresenter();
                    userModulePresenter.performVerifyRegistration(verifyUserEvent);
                }

            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getHelperProgressDialog().showProgress(VerificationStepActivity.this, "", "", false);
                ResendEmailEvent resendEmailEvent = new ResendEmailEvent();
                HashMap<String, String> header = new HashMap<String, String>();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("emailId", mEmailId);
                RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.RESEND_CONFIRMATION, RESEND_CONFIRMATION, VerificationStepActivity.this, LoginData.class, params, header, null, false, VerificationStepActivity.this);
                resendEmailEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                UserModulePresenter userModulePresenter = new UserModulePresenter();
                userModulePresenter.performResendEmail(resendEmailEvent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == CONFIRM_REGISTER_USER_RESPONSE) {
            LoginData loginData = (LoginData) response;
            if (mFrom != null && mFrom.equalsIgnoreCase(ForgotPasswordActivity.FROM)) {
                Toast.makeText(this, getResources().getString(R.string.account_verification), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else if (mFrom != null && mFrom.equalsIgnoreCase("StudyInfo")) {
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.userid), "" + mUserId);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.auth), "" + mAuth);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.verified), "true");
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.email), "" + mEmailId);


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationStepActivity.this, R.style.MyAlertDialogStyle);
                alertDialogBuilder.setTitle(VerificationStepActivity.this.getApplicationInfo().loadLabel(VerificationStepActivity.this.getPackageManager()).toString());
                alertDialogBuilder.setMessage("Do you want to set a passcode for the app?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(VerificationStepActivity.this, NewPasscodeSetupActivity.class);
                                intent.putExtra("from", "StudyInfo");
                                startActivityForResult(intent, JOIN_STUDY_RESPONSE);

                                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "NO");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "yes");
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.userid), "" + mUserId);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.auth), "" + mAuth);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.verified), "true");
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.email), "" + mEmailId);


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerificationStepActivity.this, R.style.MyAlertDialogStyle);
                alertDialogBuilder.setTitle(VerificationStepActivity.this.getApplicationInfo().loadLabel(VerificationStepActivity.this.getPackageManager()).toString());
                alertDialogBuilder.setMessage(getResources().getString(R.string.verification_dialog_text)).setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.verification_dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(VerificationStepActivity.this, NewPasscodeSetupActivity.class);
                                startActivity(intent);
                                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "NO");
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.verification_dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "yes");
                                //                                Intent intent = new Intent();
//                                setResult(RESULT_OK, intent);
//                                finish();

                                Intent intent = new Intent(VerificationStepActivity.this, StudyActivity.class);
                                ComponentName cn = intent.getComponent();
                                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                                startActivity(mainIntent);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } else if (responseCode == RESEND_CONFIRMATION) {
            Toast.makeText(this, getResources().getString(R.string.resend_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == CONFIRM_REGISTER_USER_RESPONSE) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
                if (mFrom != null && mFrom.equalsIgnoreCase("Activity")) {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                    settings.edit().clear().apply();
// delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_"+pass);
                    Intent intent = new Intent(VerificationStepActivity.this, GatewayActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    AppController.getHelperSessionExpired(VerificationStepActivity.this, errormsg);
                }
            } else {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFrom.equalsIgnoreCase(ForgotPasswordActivity.FROM)) {
            super.onBackPressed();
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_"+pass);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JOIN_STUDY_RESPONSE) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
