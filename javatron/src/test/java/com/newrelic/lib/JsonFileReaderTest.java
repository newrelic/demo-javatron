package com.newrelic.lib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JsonFileReaderTest
{
    @Test
    public void shouldReadEmptyArray()
    {
        String json = "[]";
        var reader = new JsonFileReader();
        var dictionary = reader.ReadJson(json);
        assertEquals(dictionary.size(), 0);
    }
    
    @Test
    public void shouldReadSingleElement()
    {
        String json = "[{\"id\":\"" +123 + "\", \"field1\":\"abc\"}]";
        var reader = new JsonFileReader();
        var dictionary = reader.ReadJson(json);
        assertEquals(dictionary.size(), 1);
        var element = dictionary.get("123");
        assertEquals(element.get("field1"), "abc");
    }
}
