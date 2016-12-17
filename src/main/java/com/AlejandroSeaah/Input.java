package com.AlejandroSeaah;

/**
 * Created by alejandroseaah on 16/12/17.
 */
public class Input {
    private String topic = null;
    private String value= null;

    public Input(String topic, String value) {
        this.topic = topic;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
