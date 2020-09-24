package com.newrelic.lib;

import java.util.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;

public class TomcatFileReader 
{
	public static String GetContent(ClassLoader loader, String name)
    {
        try (var stream = loader.getResourceAsStream(name))
        {
            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return content;
        } catch(IOException ioe) {
            Logger.GetOrCreate().Error("Error while reading tomcat resource "+name+ " detail:"+ioe.toString());
        }
        return null;
	}
}
