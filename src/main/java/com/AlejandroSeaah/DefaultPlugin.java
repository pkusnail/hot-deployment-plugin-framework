package com.AlejandroSeaah;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alejandroseaah on 16/12/17.
 */
public class DefaultPlugin implements  PluginInterface{
    private  static Logger logger = LoggerFactory.getLogger(DefaultPlugin.class);
    public Input preProcess(Input input){
        return input;
    }
    public Output postProcess(Output output){
        return output;
    }
    public void init(String... config) {
        if ( null == config ){
            return;
        }
        for(String str : config) {
            logger.info("Arguments from Config : " + str);
        }
    }

    /**
     * Only demo, the output is just the input ,
     * @param input
     * @return
     */
    public Output Process(Input input) {
        Input tmp = preProcess(input);
        //do some thing common within all topics or business data
        Output output = postProcess((Output)tmp);//just for demo purpose , should not be like this
        return output;
    }

    public void close() {

    }
}
