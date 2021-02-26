package com.newrelic.api.Behaviors;

import java.io.Serializable;

public class ThrowBehavior extends Behavior 
{
    public static final String Name = "THROW";

    public ThrowBehavior()
    {
        this.setName(ThrowBehavior.Name);
    }

    public void Execute()
    {
        Behavior reference = null;
        super.Execute();
        reference.Execute();
    }

}
