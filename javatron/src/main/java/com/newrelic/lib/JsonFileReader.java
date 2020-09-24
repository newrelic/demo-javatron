package com.newrelic.lib;

import java.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonFileReader 
{
	public Hashtable<String, Hashtable<String, String>> GetContent(String filename) 
    {
        try
        {
            byte[] encoded = Files.readAllBytes(Paths.get(filename));
            var fileContent = new String(encoded, StandardCharsets.UTF_8);
            return ReadJson(fileContent); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Hashtable<String, Hashtable<String, String>>();
	}

    public Object ParseJson(String json)
    {
        try{
            var jsonParser = new JSONParser();
            var obj = jsonParser.parse(json);
            return obj;
        } catch (ParseException e) {
            Logger.GetOrCreate().Error("Error while parsing json "+json+ " detail:"+e.toString());
        }
        return null;
    }

    public Hashtable<String, Hashtable<String, String>> ReadJson(String json)
    {
        var dictionary = new Hashtable<String, Hashtable<String, String>>();
        var obj = ParseJson(json);
        if (obj != null)
        {
            var list = (JSONArray)obj;
            list.forEach( element -> AddObject(dictionary, (JSONObject) element) );
        }
        return dictionary;
    }

    public void AddObject(Hashtable<String, Hashtable<String, String>> dictionary, JSONObject obj)
    {
        var element = new Hashtable<String, String>();
        String id = (String) obj.get("id");
        element.put("id", id);
        obj.keySet().forEach( key -> element.put(key.toString(), obj.get(key).toString()));
        dictionary.put(id, element);
    }
}
