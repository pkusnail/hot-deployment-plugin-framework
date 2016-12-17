# Hot_Deployment_Plugin_Framework
 This is a simple hot deployment plugin framework in java , which can dynamically load and unload jar files. It contains 2 jar files as plugin examples,the plugin project is
 [here](https://github.com/pkusnail/DemoPlugin)
 
 When the program is started , it will load configuration , and load jar files , and then start a thread to listen the changes in the plugin folder , if a jar file is created in the plugin folder, the program will see if it is a configured jar file , if the answer is yes ,then the program will load the jar file.
 
 Once started ,the program will listen to the deletion event in the plugin folder ,if a jar file deleteion event presents ,the program will see if it has been loaded ,if the answer is yes ,the program will unload the jar file.

In this way , this framework is able to dynamically load and unload jar file, so we can add ,update or remove services in production environment without interruption. 
# Usage

## How to start 
* Make sure the configuration  is correct,which is in /config/plugin.json

* Implement the PluginInterface.java interface, or extends the DefaultPlugin.java class, examples are [here](https://github.com/pkusnail/DemoPlugin), make sure to add module dependency to the plugin implemention project.

* Build artifact in Intellij idea , or export lib in Eclipse 

* Put the jar file to /plugin/ folder 


## How to stop
 * Just remove the jar file in /plugin/ folder
 
## Test Case
The test case shows how the plugin process data in json, in order to understand the test case, you should read the [codes](https://github.com/pkusnail/DemoPlugin).
