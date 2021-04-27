package com.geocento.projects.eoport.examples.services.api.dtos;

public class ResponseProduct {

    String taskID;
    String dataType;
    Integer partCounter;
    String objectURI;
    String pipelineID;
    String dumpID;
    String mission;
    Boolean taskFinished;
    Metadata metadata;
    UsageReport usage;

    public ResponseProduct() {
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
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

    public UsageReport getUsage() {
        return usage;
    }

    public void setUsage(UsageReport usage) {
        this.usage = usage;
    }
}
