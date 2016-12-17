package com.AlejandroSeaah;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by alejandroseaah on 16/12/17.
 */
public class ServiceProviderTest {
    @Test
    public void ProfileTest() throws DataProcessException {
        String value = "{\n" +
                "    \"topic\": \"profile\",\n" +
                "    \"username\": \"alejandro\",\n" +
                "    \"gender\": 0,\n" +
                "    \"nationality\": 2,\n" +
                "    \"address\": {\n" +
                "        \"street\": \"颐和园5号.\",\n" +
                "        \"city\": \"北京\",\n" +
                "        \"country\": \"中国\"\n" +
                "    }\n" +
                "}";
        String topic = "profile";
        Input input = new Input(topic, value);
        ServiceProvider sp = null;
        try {
            sp = ServiceProvider.getInstance();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Output output =  sp.Process(input);
        String json = output.getValue();
        JSONObject obj = JSON.parseObject(json);
        assertEquals("中国",obj.getString("nationality"));
    }

    @Test
    public void OrderTest() throws DataProcessException {
        String value = "{\n" +
                "    \"topic\": \"order\",\n" +
                "    \"product\": \"Iphone\",\n" +
                "    \"price\": 100,\n" +
                "    \"discount\": 97,\n" +
                "    \"address\": {\n" +
                "        \"street\": \"颐和园5号.\",\n" +
                "        \"city\": \"北京\",\n" +
                "        \"country\": \"中国\"\n" +
                "    }\n" +
                "}";
        String topic = "order";
        Input input = new Input(topic, value);
        ServiceProvider sp = null;
        try {
            sp = ServiceProvider.getInstance();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Output output =  sp.Process(input);
        String json = output.getValue();
        JSONObject obj = JSON.parseObject(json);
        assertEquals("97",obj.getString("real_price"));
    }
}
