package com.AlejandroSeaah;


/**
 * Created by bjsheguihua on 2016/11/11.
 */
public class MFConfig {
    private String classPath=null;
    private String jarName=null;
    private String topic=null;
    private String version=null;
    private String args=null;

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJarName() {

        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getTopic() {

        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClassPath() {

        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public MFConfig(String classPath, String jarName, String topic, String args, String version) {
        this.classPath = classPath;
        this.jarName = jarName;
        this.topic = topic;
        this.version = version;
        this.args = args;
    }
}
