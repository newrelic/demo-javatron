package com.newrelic.api.Behaviors;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.lib.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;

public class BehaviorServiceTest
{
    @Test
    public void shouldCreateService()
    {
        var service = GivenService();
        assertTrue( service != null );
    }

    @Test
    public void shouldExecuteBehaviorForAll()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "a value" );
    }
    
    @Test
    public void shouldExecuteBehaviorCaseInsensitive()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-Demo-test1-pre-App1", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "a value" );
    }

    @Test
    public void shouldExecuteBehaviorForPost()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-POST", "a value");
        var service = GivenService();

        service.HandlePostFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "a value" );
    }

    @Test
    public void shouldExecuteBehaviorForSpecific()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE-APP1", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "a value" );
    }

    @Test
    public void shouldExecuteBehaviorForSpecificOverride()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE", "a value");
        GivenHeader("X-DEMO-TEST1-PRE-APP1", "another specific value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "another specific value" );
    }

    @Test
    public void shouldExecuteBehaviorForSpecificOverrideDifferentOrder()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE-APP1", "another specific value");
        GivenHeader("X-DEMO-TEST1-PRE", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).Count() == 1 );
        assertTrue( AllCreatedBehaviors.get(0).getValue() == "another specific value" );
    }

    @Test
    public void shouldNotExecuteBehaviorNoHeader()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 0 );
    }

    @Test
    public void shouldNotExecuteBehaviorWhenNoDefinitions()
    {
        GivenAppId("APP1");
        GivenHeader("X-DEMO-TEST1-PRE", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 0 );
    }

    @Test
    public void shouldNotExecuteBehaviorDifferentStep()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE", "a value");
        var service = GivenService();

        service.HandlePostFunc();

        assertTrue( AllCreatedBehaviors.size() == 0 );
    }

    @Test
    public void shouldNotExecuteBehaviorDifferentAppId()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-TEST1-PRE-ANOTHERAPP2", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 0 );
    }

    @Test
    public void shouldNotExecuteBehaviorDifferentBehaviorName()
    {
        GivenAppId("APP1");
        GivenBehaviorName("TEST1");
        GivenHeader("X-DEMO-ANOTHERNAME-PRE", "a value");
        var service = GivenService();

        service.HandlePreFunc();

        assertTrue( AllCreatedBehaviors.size() == 0 );
    }

    @Before
    public void Init()
    {
        AllHeaders = new Hashtable<String,String>();
        AllBehaviorNames = new ArrayList<String>();
        AllCreatedBehaviors = new ArrayList<IncrementBehavior>();
        _appConfigRepository = mock(IAppConfigRepository.class);
        _httpUtil = mock(IHttpUtil.class);
        when(_httpUtil.GetRequestHeaders()).thenReturn(AllHeaders);
        _behaviorRepository = mock(IBehaviorRepository.class);
        when(_behaviorRepository.FindAllNames()).thenReturn(AllBehaviorNames);
        when(_behaviorRepository.Create(anyString(), anyString()))
            .thenAnswer(i -> CreateBehavior( (String)i.getArgument(0), (String)i.getArgument(1)));
    }

    public BehaviorService GivenService()
    {
        if (Service == null)
        {
            Service = new BehaviorService(_behaviorRepository, _httpUtil, _appConfigRepository);
        }
        return Service;
    }

    public void GivenHeader(String key, String value)
    {
        AllHeaders.put(key, value);
    }

    public void GivenBehaviorName(String name)
    {
        AllBehaviorNames.add(name);
    }

    public Behavior CreateBehavior(String name, String value)
    {
        var behavior = new IncrementBehavior(name, value);
        AllCreatedBehaviors.add(behavior);
        return behavior;
    }

    public void GivenAppId(String appId)
    {
        when(_appConfigRepository.FindAppId()).thenReturn(appId);
    }

    private ArrayList<IncrementBehavior> AllCreatedBehaviors;
    private ArrayList<String> AllBehaviorNames;
    private Hashtable<String,String> AllHeaders;
    private IHttpUtil _httpUtil;
    private IBehaviorRepository _behaviorRepository;
    private IAppConfigRepository _appConfigRepository;
    private BehaviorService Service;

    public class IncrementBehavior extends Behavior
    {
        private int count;

        public IncrementBehavior(String name, String value)
        {
            count = 0;
            this.setName(name);
            this.setValue(value);
        }

        public int Count()
        {
            return count;
        }

        public void Execute()
        {
            count++;
        }
    }
}
