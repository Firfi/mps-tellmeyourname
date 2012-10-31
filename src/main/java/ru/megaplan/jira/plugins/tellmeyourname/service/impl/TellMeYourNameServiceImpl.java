package ru.megaplan.jira.plugins.tellmeyourname.service.impl;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugins.tellmeyourname.service.TellMeYourNameService;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/24/12
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class TellMeYourNameServiceImpl implements TellMeYourNameService {

    Logger log = Logger.getLogger(TellMeYourNameService.class);

    private Map<String, String> names;

    TellMeYourNameServiceImpl(PluginAccessor pluginAccessor, JiraHome jiraHome) throws IOException {
         Plugin thisPlugin = pluginAccessor.getPlugin("ru.megaplan.jira.plugins.mps-tellmeyourname");
        InputStream is = thisPlugin.getResourceAsStream("peoplenames.properties");
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        names = new HashMap<String, String>();
        String s;
        while ((s = reader.readLine()) != null) {
            String[] line = s.split("=");
            names.put(line[0],line[1]);
        }
    }


    @Override
    public String getFirstname(String fullname) {
        if (fullname!=null) {
            for (String part : fullname.split(" ")) {
                if (part.length() < 2) continue;
                part = part.toLowerCase();
                String name = names.get(part.toLowerCase());
                if (name != null) {
                    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }
            }
        }
        return null;
    }
}
