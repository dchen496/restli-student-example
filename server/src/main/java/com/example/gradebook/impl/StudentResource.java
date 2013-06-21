package com.example.gradebook.impl;

import com.example.gradebook.Student;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.PagingContext;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Context;
import com.linkedin.restli.server.annotations.Finder;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import java.util.*;

/**
 * User: douglas
 * Date: 6/21/13
 * Time: 1:51 PM
 */

@RestLiCollection(name="student", namespace="com.example.gradebook")
public class StudentResource extends CollectionResourceTemplate<Long, Student>
{
    public static SortedMap<Long, Student> students = new TreeMap<Long, Student>();
    private static IDGenerator ids = new IDGenerator(4);
    static {
        students.put(0L, new Student().setGrade(10).setName("Bill"));
        students.put(1L, new Student().setGrade(11).setName("Bob"));
        students.put(2L, new Student().setGrade(9).setName("Joe"));
        students.put(3L, new Student().setGrade(10).setName("Alice"));
    }
    @Override
    public Student get(Long key)
    {
        Student s;
        s = students.get(key);
        if(s == null)
            return null;
        return s;
    }

    @Override
    public CreateResponse create(Student s)
    {
        long id = ids.nextId();
        while(students.get(id) != null)
            id = ids.nextId();
        students.put(id, s);
        return new CreateResponse(id);
    }

    @Override
    public UpdateResponse update(Long key, Student s)
    {
        students.put(key, s);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse delete(Long key)
    {
        if(students.get(key) == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        students.remove(key);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Finder("grade")
    public List<Student> findByGrade(@Context PagingContext ctx,
                                     @QueryParam("grade") Integer grade)
    {
            int i = 0;
            int start = ctx.getStart();
            int count = ctx.getCount();
            ArrayList<Student> res = new ArrayList<Student>();
            for(Map.Entry<Long, Student> s: students.entrySet())
            {
                if(!s.getValue().getGrade().equals(grade))
                    continue;
                if(i >= start && i < start+count)
                {
                    res.add(s.getValue());
                }
                i++;
            }
            return res;
    }
}

