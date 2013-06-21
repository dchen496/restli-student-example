package com.example.gradebook.impl;

import com.example.gradebook.Teacher;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import java.util.HashMap;

/**
 * User: douglas
 * Date: 6/21/13
 * Time: 2:00 PM
 */

@RestLiCollection(name="teacher", namespace="com.example.gradebook")
public class TeacherResource extends CollectionResourceTemplate<Long, Teacher>
{
    public static HashMap<Long, Teacher> teachers = new HashMap<Long, Teacher>();
    private static IDGenerator ids = new IDGenerator(2);
    static {
        teachers.put(0L, new Teacher().setName("Mr. Scott"));
        teachers.put(1L, new Teacher().setName("Mr. Stark"));
    }
    @Override
    public Teacher get(Long key)
    {
        Teacher t;
        t = teachers.get(key);
        if(t == null)
            return null;
        return t;
    }

    @Override
    public CreateResponse create(Teacher t)
    {
        long id = ids.nextId();
        while(teachers.get(id) != null)
            id = ids.nextId();
        teachers.put(id, t);
        return new CreateResponse(id);
    }

    @Override
    public UpdateResponse update(Long key, Teacher t)
    {
        teachers.put(key, t);
        return new UpdateResponse(HttpStatus.S_200_OK);

    }

    @Override
    public UpdateResponse delete(Long key)
    {
        if(teachers.get(key) == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        teachers.remove(key);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }
}