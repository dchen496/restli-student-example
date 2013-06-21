package com.example.gradebook.impl;

/**
 * User: douglas
 * Date: 6/23/13
 * Time: 9:05 PM
 */
public class IDGenerator
{
    long i = 0;

    public IDGenerator()
    {

    }

    public IDGenerator(int start)
    {
        i = start;
    }

    public synchronized long nextId() {
        return i++;
    }
}
