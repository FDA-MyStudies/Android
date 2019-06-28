package com.harvard.gatewayModule.model;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/24/2017.
 */

public class Gateway {
    private String message;
    private ArrayList<Info> infos = new ArrayList<>();
    private ArrayList<Resources> resources = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Info> getInfos() {
        return infos;
    }

    public void setInfos(ArrayList<Info> infos) {
        this.infos = infos;
    }

    public ArrayList<Resources> getResources() {
        return resources;
    }

    public void setResources(ArrayList<Resources> resources) {
        this.resources = resources;
    }
}
