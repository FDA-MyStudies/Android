package com.harvard.studyAppModule.consent.consentLARStep;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harvard.R;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConsentLARStepBody<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private ConsentLARStep step;
    private AnswerFormat format;
//    private Set<T> currentSelected;
    private StepResult<T[]> result;
    private ArrayList<String> userResponses;
    private EditText patientRelationship;
    private EditText patientFirstName;
    private EditText patientLastName;

    public ConsentLARStepBody(Step step, StepResult result) {
        this.step = (ConsentLARStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (AnswerFormat) this.step.getAnswerFormat();
        userResponses = new ArrayList<>();
        // Restore results
//        currentSelected = new LinkedHashSet<>();
        T[] resultArray = this.result.getResult();
        if (resultArray != null && resultArray.length > 0) {
//            currentSelected.addAll(Arrays.asList(resultArray));
            userResponses.addAll((Collection<? extends String>) Arrays.asList(resultArray));
        }

        if (userResponses.size() == 0) {
            userResponses.add("");
            userResponses.add("");
            userResponses.add("");
        } else if(userResponses.size() == 1) {
            userResponses.add("");
            userResponses.add("");
        } else if(userResponses.size() == 2) {
            userResponses.add("");
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            return initViewDefault(inflater, parent);
        } else if (viewType == VIEW_TYPE_COMPACT) {
            return initViewCompact(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(final LayoutInflater inflater, ViewGroup parent) {

        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) LinearLayout.LayoutParams.WRAP_CONTENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams((int) LinearLayout.LayoutParams.MATCH_PARENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 20, 0, 0);
        params.setMargins(0, 20, 0, 20);
        TextView label = new TextView(inflater.getContext());
        label.setText(parent.getContext().getResources().getString(R.string.participant_relationship2));
        label.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        label.setTextSize(18f);
        label.setLayoutParams(params);

        TextView firstNameLastName = new TextView(inflater.getContext());
        firstNameLastName.setText(parent.getContext().getResources().getString(R.string.participant_first_last_name));
        firstNameLastName.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        firstNameLastName.setTextSize(18f);
        firstNameLastName.setLayoutParams(params);
        TextView firstName = new TextView(inflater.getContext());
        firstName.setText(parent.getContext().getResources().getString(R.string.participant_first_name2));
        firstName.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        firstName.setTextSize(16f);
        firstName.setLayoutParams(params);
        TextView lastName = new TextView(inflater.getContext());
        lastName.setText(parent.getContext().getResources().getString(R.string.participant_last_name2));
        lastName.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        lastName.setTextSize(16f);
        lastName.setLayoutParams(params);
        patientRelationship = new EditText(inflater.getContext());
        patientRelationship.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimaryBlack));
        patientRelationship.setTextSize(16f);
        patientRelationship.setHint(parent.getContext().getResources().getString(R.string.first_name3));
        patientRelationship.setLayoutParams(params1);
        patientFirstName = new EditText(inflater.getContext());
        patientFirstName.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimaryBlack));
        patientFirstName.setTextSize(16f);
        patientFirstName.setHint(parent.getContext().getResources().getString(R.string.first_name3));
        patientFirstName.setLayoutParams(params1);
        patientLastName = new EditText(inflater.getContext());
        patientLastName.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimaryBlack));
        patientLastName.setTextSize(16f);
        patientLastName.setHint(parent.getContext().getResources().getString(R.string.first_name3));
        patientLastName.setLayoutParams(params1);
        if (userResponses != null && userResponses.size() > 0) {
            for(int i = 0; i < userResponses.size(); i++) {
                if (i == 0) {
                    patientRelationship.setText(userResponses.get(i));
                }
                else if (i == 1) {
                    patientFirstName.setText(userResponses.get(i));
                }
                else if (i == 2) {
                    patientLastName.setText(userResponses.get(i));
                }
            }
        }

        patientRelationship.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userResponses.set(0, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        patientFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userResponses.set(1, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        patientLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userResponses.set(2, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        linearLayout.addView(label);
        linearLayout.addView(patientRelationship);
        linearLayout.addView(firstNameLastName);
        linearLayout.addView(firstName);
        linearLayout.addView(patientFirstName);
        linearLayout.addView(lastName);
        linearLayout.addView(patientLastName);


        return linearLayout;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

        TextView label = (TextView) inflater.inflate(R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
        label.setText(step.getTitle());
        compactView.addView(label, 0);

        return compactView;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {

        result.setResult((T[]) userResponses.toArray());

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (userResponses.get(0).equalsIgnoreCase("") || userResponses.get(1).equalsIgnoreCase("") || userResponses.get(2).equalsIgnoreCase("")) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_default);
        } else {
            return BodyAnswer.VALID;
        }
    }

}