package com.AlejandroSeaah;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Scanner;



/**
 * Created by alejandroseaah on 16/12/17.
 */
public class ServiceProvider {
    private  static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);
    protected Hashtable<String , MFConfig> topic2ConfigMapper = null;
    protected Hashtable<String , MFConfig> jar2ConfigMapper = null;
    protected Hashtable<String , PluginInterface> topic2InstanceMapper = null;
    protected Hashtable<String , PluginInterface> classPath2InstanceMapper = null;


    protected  String configFolder = System.getProperty("user.dir") + "/config/";
    protected  String mappingConfigPath = configFolder+ "plugin.json"; //json file, mapping info about kafka topic ,jar name and class path
    protected  String jarFolderPath = System.getProperty("user.dir") +"/plugin/";
    protected  int refreshInterval = 5;


    private  JarWatcher jarWatcher = null;
    private static ServiceProvider sp = null;


    protected  ServiceProvider(){
        topic2ConfigMapper = new Hashtable<>();
        jar2ConfigMapper = new Hashtable<>();
        topic2InstanceMapper = new Hashtable<>();
        classPath2InstanceMapper = new Hashtable<>();
    }

    public static ServiceProvider getInstance()
            throws  JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        logger.debug("getInstance() called");
        if (null == sp) {
            synchronized (ServiceProvider.class) {
                if (null == sp) {
                    sp = new ServiceProvider();
                    sp.prepare();
                }
            }
        }
        return  sp;
    }

    public void prepare()
            throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        logger.debug("prepare() called");
        if ( null == jarWatcher ) {
            synchronized ( JarWatcher.class ) {
                if ( null == jarWatcher ) {
                    mappingConfigReader();
                    jarWatcher = new JarWatcher(this);
                    jarWatcher.scanAndLoadPlugin();
                    jarWatcher.start();

                    logger.info("configFolder : " + configFolder);
                    logger.info("mappingConfigPath: " + mappingConfigPath);
                    logger.info("jarFolderPath : " + jarFolderPath);
                    logger.info("refreshInterval: " + refreshInterval);
                    logger.info("topic2InstanceMapper size  :" + topic2InstanceMapper.size());
                    logger.info("jar2ConfigMapper size : " + jar2ConfigMapper.size());
                    logger.info("topic2ConfigMapper size : " + topic2ConfigMapper.size());
                }
            }
        }
    }

    public MFConfig getConfigByJar(String jarName){
        logger.debug("getConfigByJar() called , jarName : " + jarName);
        if( null == jar2ConfigMapper){
            logger.error("jar2ConfigMapper NULL ");
            return null;
        }
        if ( jar2ConfigMapper.containsKey(jarName)){
            return jar2ConfigMapper.get(jarName);
        }else {
            logger.warn("No config found for jar " + jarName);
            return null;
        }
    }


    public PluginInterface getPluginByTopic(String topic){
        logger.debug("getPluginByTopic() called , topic : " + topic);
        if(null == this.topic2InstanceMapper ){
            logger.error(" topic2InstanceMapper NULL ,return");
            return null;
        }
        if (topic2InstanceMapper.containsKey(topic.trim())){
            return topic2InstanceMapper.get(topic.trim());
        }
        logger.error("no plugin found for topic : " + topic);
        return null;
    }


    public String jsonFileReader(String filePath) throws FileNotFoundException {
        logger.debug("JsonFileReader() called ");
        File file = new File(filePath);
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        scanner = new Scanner(file, "utf-8");
        while (scanner.hasNextLine()) {
            buffer.append(scanner.nextLine());
        }
        scanner.close();
        return buffer.toString();
    }

    public void mappingConfigReader()
            throws JSONException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        logger.debug("mappingConfigReader() called ");

        //add  plugin info from config
        String jsonContext  = jsonFileReader(mappingConfigPath);
        JSONArray jsonArray = JSON.parseArray(jsonContext );
        for(int  i = 0; i < jsonArray.size(); i++){
            String version = null;
            String classPath = null;
            String jarName = null;
            String bizTopic = null;
            String args = null;
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(jsonObject.containsKey("ClassPath")
                    && jsonObject.containsKey("JarName")
                    && jsonObject.containsKey("Args")
                    && jsonObject.containsKey("Version")) {
                classPath = jsonObject.get("ClassPath").toString().trim();
                jarName = jsonObject.get("JarName").toString().trim();
                version = jsonObject.get("Version").toString().trim();
                bizTopic=jsonObject.get("BizTopic").toString().trim();
                args=jsonObject.get("Args").toString().trim();

                MFConfig mfc = new MFConfig(classPath, jarName, bizTopic, args, version);
                this.topic2ConfigMapper.put(bizTopic,mfc);
                this.jar2ConfigMapper.put(jarName,mfc);
                logger.info("Mapping Config Info for Jar : " + jarName + "  Loaded ");
            }else {
                logger.error("Illegal or incomplete plugin config info ");
            }
        }
    }



    public void seekMappingConfig(String jarName) throws JSONException,Exception {
        logger.debug("seekMappingConfig() called ");

        //add  plugin info from config
        String JsonContext = null;
        JsonContext = jsonFileReader(mappingConfigPath);
        JSONArray jsonArray = null;
        jsonArray = JSON.parseArray(JsonContext);
        for(int  i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if( !jsonObject.containsKey("JarName") || ( jarName != jsonObject.get("jarName") )) {
                continue;
            }
            String version = null;
            String classPath = null;
            String bizTopic = null;
            String args = null;
            if(jsonObject.containsKey("ClassPath")
                    && jsonObject.containsKey("JarName")
                    && jsonObject.containsKey("Args")
                    && jsonObject.containsKey("Version")) {
                classPath = jsonObject.get("ClassPath").toString().trim();
                jarName = jsonObject.get("JarName").toString().trim();
                version = jsonObject.get("Version").toString().trim();
                bizTopic=jsonObject.get("BizTopic").toString().trim();

                MFConfig mfc = new MFConfig(classPath, jarName, bizTopic, args, version);
                this.topic2ConfigMapper.put(bizTopic,mfc);
                this.jar2ConfigMapper.put(jarName,mfc);
                logger.info("Mapping Config Info for Jar : " + jarName + "  Loaded ");
            }else {
                logger.error("Illegal or incomplete plugin config info ");
            }
            return;// just for one jar, unnecessary to continue
        }
    }

    public Output Process (Input sr)  throws  DataProcessException{
        logger.debug("filter() called");
        if( topic2InstanceMapper.containsKey(sr.getTopic().toString().trim())){// first priority
            return  sp.getPluginByTopic(sr.getTopic().toString().trim()).Process(sr);
        }
        return null;
    }
}
