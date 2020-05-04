package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rajeesh on 4/12/2017.
 */

public class ContactUsEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
