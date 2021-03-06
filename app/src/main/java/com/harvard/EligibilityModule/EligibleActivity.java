package com.harvard.EligibilityModule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.ConsentCompletedActivity;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.studyAppModule.StudyFragment;
import com.harvard.studyAppModule.consent.ConsentBuilder;
import com.harvard.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyAppModule.consent.model.Consent;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static com.harvard.studyAppModule.StudyFragment.CONSENT;

public class EligibleActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private static final int CONSENT_RESPONSECODE = 100;
    EligibilityConsent eligibilityConsent;
    DBServiceSubscriber dbServiceSubscriber;
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 200;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligible);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm=AppController.getRealmobj(this);

        TextView button = (TextView) findViewById(R.id.continueButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra("studyId"), mRealm);
                startconsent(eligibilityConsent.getConsent());
            }
        });
        updateuserpreference();
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    private void startconsent(Consent consent) {
        ConsentBuilder consentBuilder = new ConsentBuilder();
        List<Step> consentstep = consentBuilder.createsurveyquestion(this, consent, getIntent().getStringExtra("title"), getIntent().getStringExtra("type"));
        Task consentTask = new OrderedTask(CONSENT, consentstep);
        Intent intent = CustomConsentViewTaskActivity.newIntent(this, consentTask, getIntent().getStringExtra("studyId"), getIntent().getStringExtra("enrollId"), getIntent().getStringExtra("title"), getIntent().getStringExtra("eligibility"), getIntent().getStringExtra("type"));
        startActivityForResult(intent, CONSENT_RESPONSECODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSENT_RESPONSECODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, ConsentCompletedActivity.class);
                intent.putExtra("enrollId", getIntent().getStringExtra("enrollId"));
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                // get the encrypted file path
                intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
                startActivity(intent);
                finish();
            } else if (resultCode == 12345) {
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(this, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent intent = new Intent(this, StandaloneActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                }
            } else {
                finish();
            }
        }
    }


    public PdfPCell getImage(Image image, int alignment) {
        PdfPCell cell;
        if (image != null) {
            cell = new PdfPCell(image);
        } else {
            cell = new PdfPCell();
        }
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


    public void updateuserpreference() {
        AppController.getHelperProgressDialog().showProgress(EligibleActivity.this, "", "", false);
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put("studyId", getIntent().getStringExtra("studyId"));
            studiestatus.put("status", StudyFragment.YET_TO_JOIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        dbServiceSubscriber.updateStudyPreferenceDB(this,getIntent().getStringExtra("studyId"), StudyFragment.YET_TO_JOIN, "", "", "");
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
    }
}
