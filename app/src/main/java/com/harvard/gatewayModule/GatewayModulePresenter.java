package com.harvard.gatewayModule;

import com.harvard.FDAEventBus;
import com.harvard.gatewayModule.events.GetStartedEvent;

/**
 * Created by Rohit on 3/3/2017.
 */

public class GatewayModulePresenter {
    public void getStarted(GetStartedEvent getStartedEvent)
    {
        FDAEventBus.postEvent(getStartedEvent);
    }
}
