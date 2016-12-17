package com.AlejandroSeaah;
/**
 * Created by bjsheguihua on 2016/11/14.
 */

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JarFileListener extends FileAlterationListenerAdaptor {
    private  static Logger logger = LoggerFactory.getLogger(JarFileListener.class);
    private JarWatcher jarWatcher = null;
    private enum Signal {
        CREATE, DELETE, CHANGE, DUMMY
    }
    private Signal status = Signal.DUMMY;
    public JarFileListener(JarWatcher jarWatcher){
        this.jarWatcher = jarWatcher;
    }

    @Override
    public void onFileCreate(File file) {
        status = Signal.CREATE;
        logger.warn("[New File and Service Start]:" + file.getAbsolutePath());
        if(file.toString().endsWith("jar") && file.isFile()) {
            try {
                long fileLen = 0;
                while (file.exists() && file.isFile() && ( fileLen != file.length()) ){
                    fileLen = file.length();
                    Thread.sleep(200);
                }
                if ( file.exists() ) {
                    String jarName = file.getName();
                    jarWatcher.jarLoarder(jarName); //load jar
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void onFileDelete(File file) {
        status = Signal.DELETE;
        logger.warn("[File Deleted and Service Stop]:" + file.getAbsolutePath());
        if(file.toString().endsWith("jar")) {
            try {
                String jarName = file.getName();
                jarWatcher.jarUnloader(jarName); // unload jar
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void onFileChange(File file) {
        if (status != Signal.DUMMY){
            status = Signal.DUMMY;
            return;
        }
        logger.warn("[File Changed and Service Changed]:" + file.getAbsolutePath());
        if( file.toString().endsWith("jar") && file.isFile() ) {
            try {
                long fileLen = 0;
                while ( file.exists() && ( fileLen != file.length()) ){
                    fileLen = file.length();
                    Thread.sleep(200);
                }
                String jarName = file.getName();
                jarWatcher.jarUnloader(jarName); // unload jar
                if ( file.exists() ) {
                    jarWatcher.jarLoarder(jarName);
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }finally {
                status = Signal.DUMMY;
            }
        }else {
            status = Signal.DUMMY;
        }
    }
}