package com.geocento.projects.eoport.examples.services.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ServiceSchedulerNotification {

    String subscription;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "UTC"
    )
    Date download;

    public ServiceSchedulerNotification() {
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public Date getDownload() {
        return download;
    }

    public void setDownload(Date download) {
        this.download = download;
    }
}
