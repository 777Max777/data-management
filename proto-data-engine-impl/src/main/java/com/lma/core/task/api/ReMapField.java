package com.lma.core.task.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReMapField implements Serializable {

    private List<String> targetPath;
    private List<String> rewritePath;

    public List<String> getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(List<String> targetPath) {
        this.targetPath = targetPath;
    }

    public List<String> getRewritePath() {
        return rewritePath;
    }

    public void setRewritePath(List<String> rewritePath) {
        this.rewritePath = rewritePath;
    }
}
