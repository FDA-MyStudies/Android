package com.harvard.studyAppModule.consent;

import android.util.Log;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.List;

public class ConsentBuilderTask extends OrderedTask {
    public ConsentBuilderTask(String identifier, List<Step> steps) {
        super(identifier, steps);
        for(int i=0;i< steps.size();i++){
            Log.e("Krishna", "ConsentBuilderTask:steps in spanish: getIdentifier "+steps.get(i).getIdentifier());
            Log.e("Krishna", "ConsentBuilderTask:steps in spanish: getText "+steps.get(i).getText());
            Log.e("Krishna", "ConsentBuilderTask:steps in spanish: getTitle "+steps.get(i).getTitle());
            Log.e("Krishna", "ConsentBuilderTask:steps in spanish: getStepTitle "+steps.get(i).getStepTitle());
            Log.e("Krishna", "ConsentBuilderTask:steps in spanish: getStepLayoutClass "+steps.get(i).getStepLayoutClass());
        }
    }

    public static ConsentBuilderTask create(String identifier, List<Step> steps) {
        return new ConsentBuilderTask(identifier, steps);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        if(step == null)
        {
            return steps.get(0);
        }

        if(step.getIdentifier().equalsIgnoreCase("consentLarFirst")) {
            StepResult stepResult = result.getStepResult(step.getIdentifier());
            Object[] objects = (Object[]) stepResult.getResults().get("answer");
            String answer = (String)objects[0];
            if(answer.equalsIgnoreCase("1")) {
                int nextIndex = steps.indexOf(step) + 2;
                return steps.get(nextIndex);
            }
        }

        int nextIndex = steps.indexOf(step) + 1;

        if(nextIndex < steps.size())
        {
            return steps.get(nextIndex);
        }

        return null;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        int nextIndex = steps.indexOf(step) - 1;

        if(nextIndex >= 0)
        {
        if(steps.get(nextIndex).getIdentifier().equalsIgnoreCase("consentLarSecond")) {
            StepResult stepResult = result.getStepResult("consentLarFirst");
            Object[] objects = (Object[]) stepResult.getResults().get("answer");
            String answer = (String)objects[0];
            if(answer.equalsIgnoreCase("1")) {
                return steps.get(nextIndex - 1);
            }
        } else {
            return steps.get(nextIndex);
          }
       }

        return null;
    }
}
