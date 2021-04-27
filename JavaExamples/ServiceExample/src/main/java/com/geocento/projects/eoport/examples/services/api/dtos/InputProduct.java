package com.geocento.projects.eoport.examples.services.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputProduct {

    String taskID;
    String downstreamURI;
    String dataType;
    Integer partCounter;
    String objectURI;
    String pipelineID;
    String dumpID;
    String mission;
    Boolean taskFinished;
    Metadata metadata;

    public InputProduct() {
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getDownstreamURI() {
        return downstreamURI;
    }

    public void setDownstreamURI(String downstreamURI) {
        this.downstreamURI = downstreamURI;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getPartCounter() {
        return partCounter;
    }

    public void setPartCounter(Integer partCounter) {
        this.partCounter = partCounter;
    }

    public String getObjectURI() {
        return objectURI;
    }

    public void setObjectURI(String objectURI) {
        this.objectURI = objectURI;
    }

    public String getPipelineID() {
        return pipelineID;
    }

    public void setPipelineID(String pipelineID) {
        this.pipelineID = pipelineID;
    }

    public String getDumpID() {
        return dumpID;
    }

    public void setDumpID(String dumpID) {
        this.dumpID = dumpID;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public Boolean getTaskFinished() {
        return taskFinished;
    }

    public void setTaskFinished(Boolean taskFinished) {
        this.taskFinished = taskFinished;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
