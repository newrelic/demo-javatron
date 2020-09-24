package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;

public class TronHandlerTest
{
    @Test
    public void shouldGetNoHeadersWhenEmpty()
    {
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        assertEquals(headers.size(), 0);
    }

    @Test
    public void shouldGetSingleDemotronHeader()
    {
        GivenHeader("X-DEMOTRON-TEST1", "anything goes");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        assertEquals(headers.size(), 1);
        assertEquals(headers.get("X-DEMOTRON-TEST1"), "anything goes");
    }

    @Test
    public void shouldGetSingleDemotronHeaderCaseInsensitive() throws Exception
    {
        GivenHeader("X-Demotron-Test1", "anything also goes");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        assertEquals(headers.size(), 1);
        assertEquals(headers.get("X-DEMOTRON-TEST1"), "anything also goes");
    }

    @Test
    public void shouldGetMultipleDemotronHeader()  throws Exception
    {
        GivenHeader("X-DEMOTRON-TEST1", "anything goes");
        GivenHeader("X-DEMOTRON-PREF", "blue green red");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        assertEquals(headers.size(), 2);
        assertEquals(headers.get("X-DEMOTRON-TEST1"), "anything goes");
        assertEquals(headers.get("X-DEMOTRON-PREF"), "blue green red");
    }

    @Test
    public void shouldGetDemotronHeaderIgnoreCase()
    {
        GivenHeader("x-demotron-test1", "anything goes");
        GivenHeader("X-DeMoTRoN-PReF", "blue green red");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        assertEquals(headers.size(), 2);
        assertEquals(headers.get("X-DEMOTRON-TEST1"), "anything goes");
        assertEquals(headers.get("X-DEMOTRON-PREF"), "blue green red");
    }

    @Test
    public void shouldNotGetNonDemotronHeader()
    {
        GivenHeader("X-DEMOTRON-TEST1", "anything goes");
        GivenHeader("X-CUSTOM-HEADER", "this should not be there");
        GivenHeader("X-DEMOTRON-PREF", "blue green red");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        var value = headers.get("X-CUSTOM-HEADER");
        assertTrue( value == null );
    }

    @Test
    public void shouldNotGetDemotronHeaderWhenStartsWithOtherText()
    {
        GivenHeader("TEXT-X-DEMOTRON-TEST1", "anything goes");
        var headers = TronHandler().GetDemoHttpHeaders(AllHeaders);
        var value = headers.get("TEXT-X-DEMOTRON-TEST1");
        assertTrue( value == null );
    }

    @Test
    public void shouldHaveDemoTracingEnabled()
    {
        GivenHeader("X-DEMOTRON-TRACE", "any");
        var isEnabled = TronHandler().IsTracingEnabled();
        assertTrue( isEnabled );
    }

    @Test
    public void shouldHaveDemoTracingEnabledCaseInsensitive()
    {
        GivenHeader("X-Demotron-Trace", "any");
        var isEnabled = TronHandler().IsTracingEnabled();
        assertTrue( isEnabled );
    }

    @Test
    public void shouldNotHaveDemoTracingEnabled()
    {
        GivenHeader("X-DEMOTRON-SOMETHING", "any");
        var isEnabled = TronHandler().IsTracingEnabled();
        assertFalse( isEnabled );
    }

    @Test
    public void ShouldExecuteSpecificUrl() throws Exception
    {
        TronHandler().InvokeUrl("http://localhost:1234", "/myRoute");
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:1234/myRoute"), anyObject());
    }

    @Test(expected = Exception.class)
    public void ShouldRaiseError() throws Exception
    {

        when(_httpResponse.GetStatusCode()).thenReturn(404);
        TronHandler().InvokeUrl("http://localhost:1234", "/myRoute");
    }

    @Test
    public void ShouldExecuteSingleDependency() throws Exception
    {
        GivenDependency("app2", new String [] {"http://localhost:1234"});
        TronHandler().InvokeDependencies("/myRoute");
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:1234/myRoute"), anyObject());
    }

    @Test
    public void ShouldExecuteSingleDependencyMultipleUrls() throws Exception
    {
        GivenDependency("app2", new String [] {"http://localhost:1234", "http://localhost:5678"});
        TronHandler().InvokeDependencies("/myRoute");
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:1234/myRoute"), anyObject());
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:5678/myRoute"), anyObject());
    }

    @Test
    public void ShouldExecuteMultipleDependencies() throws Exception
    {
        GivenDependency("app2", new String [] {"http://localhost:1234"});
        GivenDependency("app3", new String [] {"http://anotherhost:1234"});
        TronHandler().InvokeDependencies("/myRoute");
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:1234/myRoute"), anyObject());
        verify(_httpUtil, times(1)).QueryService(eq("http://anotherhost:1234/myRoute"), anyObject());
    }

    @Test
    public void ShouldExecuteMultipleDependenciesMultipleUrls() throws Exception
    {
        GivenDependency("app2", new String [] {"http://localhost:1234", "http://localhost:5678"});
        GivenDependency("app3", new String [] {"http://anotherhost:1234", "http://anotherhost:5678"});
        TronHandler().InvokeDependencies("/myRoute");
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:1234/myRoute"), anyObject());
        verify(_httpUtil, times(1)).QueryService(eq("http://anotherhost:1234/myRoute"), anyObject());
        verify(_httpUtil, times(1)).QueryService(eq("http://localhost:5678/myRoute"), anyObject());
        verify(_httpUtil, times(1)).QueryService(eq("http://anotherhost:5678/myRoute"), anyObject());
    }

    @Test
    public void ShouldGetTrace()
    {
        GivenHeader("X-DEMOTRON-TRACE", "any");
        var trace = TronHandler().GetTrace("myid");
        assertEquals(trace, "myid");
    }

    @Test
    public void TraceShouldHaveDependentTraceSegment() throws Exception
    {
        GivenHeader("X-DEMOTRON-TRACE", "any");
        GivenDependency("app2", new String [] {"http://localhost:1234"});
        GivenResponseHeader("X-DEMOTRON-TRACE", "app2");
        TronHandler().InvokeDependencies("/myRoute");
        var trace = TronHandler().GetTrace("myid");
        assertEquals(trace, "myid,app2");
    }

    @Test
    public void TraceShouldHaveMultipleDependentTraceSegment() throws Exception
    {
        GivenHeader("X-DEMOTRON-TRACE", "any");
        GivenDependency("app2", new String [] {"http://localhost:1234", "http://localhost:5678"});
        GivenResponseHeader("X-DEMOTRON-TRACE", "app2");
        TronHandler().InvokeDependencies("/myRoute");
        var trace = TronHandler().GetTrace("myid");
        assertEquals(trace, "myid,app2,app2");
    }

    @Test
    public void TraceShouldBeEmptyWhenDisabled() throws Exception
    {
        GivenDependency("app2", new String [] {"http://localhost:1234"});
        GivenResponseHeader("X-DEMOTRON-TRACE", "app2");
        TronHandler().InvokeDependencies("/myRoute");
        var trace = TronHandler().GetTrace("myid");
        assertEquals(trace, null);
    }

    @Before
    public void Init()
    {
        TronHandler = null;
        AllHeaders = new Hashtable<String,String>();
        ResponseHeaders = new Hashtable<String,String>();
        _dependencies = new ArrayList<AppConfigDependency>();
        _httpUtil = mock(IHttpUtil.class);
        when(_httpUtil.GetRequestHeaders()).thenReturn(AllHeaders);
        _httpResponse = mock(IHttpResponseAdapter.class);
        when(_httpResponse.GetHeaders()).thenReturn(ResponseHeaders);
        when(_httpResponse.GetStatusCode()).thenReturn(200);
        when(_httpUtil.QueryService(anyString(), anyObject())).thenReturn(_httpResponse);
    }

    public TronHandler TronHandler()
    {
        if (TronHandler == null)
        {
            TronHandler = new TronHandler(_httpUtil, GetDependencyAsArray());
        }
        return TronHandler;
    }

    public void GivenHeader(String key, String value)
    {
        AllHeaders.put(key, value);
    }

    public void GivenDependency(String key, String[] urls)
    {
        var dependency = new AppConfigDependency();
        dependency.setId(key);
        dependency.setUrls(urls);
        _dependencies.add(dependency);
    }

    public void GivenResponseHeader(String key, String value)
    {
        ResponseHeaders.put(key, value);
    }

    public AppConfigDependency[] GetDependencyAsArray()
    {
        var array = new AppConfigDependency[_dependencies.size()];
        return _dependencies.toArray(array);
    }

    private Hashtable<String,String> AllHeaders;
    private Hashtable<String,String> ResponseHeaders;
    private ArrayList<AppConfigDependency> _dependencies;
    private IHttpUtil _httpUtil;
    private IHttpResponseAdapter _httpResponse;
    private TronHandler TronHandler;
}
