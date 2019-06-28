package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

/**
 * Created by Rohit on 2/23/2017.
 */

public class LocationFormat extends Format {
    private boolean useCurrentLocation;

    public boolean isUseCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean useCurrentLocation) {
        this.useCurrentLocation = useCurrentLocation;
    }
}
