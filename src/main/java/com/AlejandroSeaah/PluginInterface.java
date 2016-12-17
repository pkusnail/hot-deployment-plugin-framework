package com.AlejandroSeaah;

/**
 * Created by alejandroseaah on 16/12/17.
 */
public interface PluginInterface {
    void init( String ... config);
    Output Process (Input input);
    void close();
}
