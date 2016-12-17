package com.AlejandroSeaah;


import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by bjsheguihua on 2016/11/11.
 * 负责插件变动监听和插件加载，工作流程为启动时扫描插件目录并加载插件，然后启动JarFileListener类监听变动
 */
public class JarWatcher  extends  Thread {
    private  static Logger logger = LoggerFactory.getLogger(JarWatcher.class);
    private  int refreshInteral = 1 ; // in seconds , careful
    private  ServiceProvider sp  = null ;
    public JarWatcher(ServiceProvider sp ){
        this.sp = sp;
    }

    public void scanAndLoadPlugin(){
        logger.info("Scan the plugin folder : " + sp.jarFolderPath);
        try {
            File file = new File(sp.jarFolderPath);
            File[] tempList = file.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile() && tempList[i].getName().toString().endsWith("jar") && (null != sp.getConfigByJar(tempList[i].getName().toString()))) {
                    logger.info("Detect file: " + tempList[i].getName().toString());
                    try {
                        String jarName = tempList[i].getName().toString();
                        jarLoarder(jarName);
                    } catch (Exception e) {
                        logger.error(e.getMessage() );
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage() );
        }
    }

    public void run (){
        logger.info("Start monitoring : " + sp.jarFolderPath);
        FileAlterationObserver observer = new FileAlterationObserver(sp.jarFolderPath,null, null); // 监控目录jarFolderPath
        observer.addListener(new JarFileListener(this));
        FileAlterationMonitor monitor = new FileAlterationMonitor(this.refreshInteral,observer);
        try {
            monitor.start();
        }catch (Exception e){
            logger.error(e.getMessage() );
        }
    }


    public Boolean jarLoarder(String jarName) throws Exception{

        logger.debug("jarLoarder() called, jarName : " + jarName);
        if( null == sp.getConfigByJar(jarName)){
            sp.seekMappingConfig(jarName);//seek to jar info in config
            if( null == sp.getConfigByJar(jarName)) {
                return false;//not a valid jar name at least
            }
        }
//        jarUnloader(jarName);
        PluginClassLoader loader = new PluginClassLoader();
        String pluginUrl =  "jar:file:" + sp.jarFolderPath+jarName+"!/";  //  be careful
        logger.info("plugin url: " +pluginUrl);

        URL url = null;
        try {
            url = new URL(pluginUrl);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage() );
            return false;
        }

        String classpath = null;
        try{
            loader.addURLFile(url);
            if( null != sp.getConfigByJar(jarName)) {
                classpath = sp.getConfigByJar(jarName).getClassPath().trim();
            }else{
                loader.unloadJarFile();
                logger.warn("Can NOT find class path in config, "+url.toString()+" file CAN NOT  be loaded!");
                return false;
            }
        }catch (Exception e){
            loader.unloadJarFile();
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }

        try {
        	logger.info("classpath is "+classpath);

            Class<?> forName = Class.forName(classpath, true, loader);
            PluginInterface ins = (PluginInterface)forName.newInstance();

            MFConfig mfc =  sp.getConfigByJar(jarName);
            ins.init(mfc.getArgs());
            logger.info("instance is "+ins.getClass().getName());
            sp.topic2InstanceMapper.put(sp.getConfigByJar(jarName).getTopic(), ins);
            sp.classPath2InstanceMapper.put(sp.getConfigByJar(jarName).getClassPath(), ins);
            logger.info("plugin "+ jarName + " loaded successfully!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }finally {
            loader.unloadJarFile();
        }
        return true;
    }

    public void jarUnloader(String jarName){
        logger.debug("jarUnloader() called, jarName : " + jarName);
        try {
            if (sp.classPath2InstanceMapper.containsKey( sp.getConfigByJar(jarName).getClassPath() )) {
                sp.classPath2InstanceMapper.remove(sp.getConfigByJar(jarName).getClassPath());
            }

            if (sp.topic2InstanceMapper.containsKey( sp.getConfigByJar(jarName).getTopic() )) {
                sp.topic2InstanceMapper.get(sp.getConfigByJar(jarName).getTopic()).close();
                sp.topic2InstanceMapper.remove(sp.getConfigByJar(jarName).getTopic());
            }

        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
