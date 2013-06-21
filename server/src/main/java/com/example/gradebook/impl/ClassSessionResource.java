package com.example.gradebook.impl;

import com.example.gradebook.ClassAttendee;
import com.example.gradebook.ClassPeriod;
import com.example.gradebook.ClassSession;
import com.linkedin.data.template.SetMode;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.server.CreateResponse;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Optional;
import com.linkedin.restli.server.annotations.QueryParam;
import com.linkedin.restli.server.annotations.RestLiCollection;
import com.linkedin.restli.server.annotations.RestMethod;
import com.linkedin.restli.server.resources.CollectionResourceTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * User: douglas
 * Date: 6/23/13
 * Time: 9:13 PM
 */

@RestLiCollection(name="classes", namespace="com.example.gradebook", parent=TeacherResource.class)
public class ClassSessionResource extends CollectionResourceTemplate<Long, ClassSession>
{
    // teacher id -> class id
    public static HashMap<Long, HashMap<Long, ClassSession> > classes
            = new HashMap<Long, HashMap<Long, ClassSession> >();
    private static HashMap<Long, IDGenerator> ids = new HashMap<Long, IDGenerator>();
    static {
        ClassSession cs = new ClassSession();
        cs.setName("Physics");
        cs.setDescription("How objects move.");
        cs.setPeriod(ClassPeriod.AFTERNOON);
        classes.put(1L, new HashMap<Long, ClassSession>());
        classes.get(1L).put(0L, cs);
        ids.put(1L, new IDGenerator(1));
    }

    private Long getTeacherKey()
    {
        return getContext().getPathKeys().get("teacherId");
    }

    private double computeMeanGrade(Long teacherKey, Long classKey)
    {
        HashMap<Long, HashMap<Long, ClassAttendee>> n = ClassAttendeesResource.attendees.get(teacherKey);
        if(n == null)
            return 0;
        HashMap<Long, ClassAttendee> m = n.get(classKey);
        if(m == null)
            return 0;
        int count = 0;
        double sum = 0;
        for(Map.Entry<Long, ClassAttendee> c : m.entrySet())
        {
            sum += c.getValue().getGrade();
            count++;
        }
        if(count == 0)
            return 0;
        return sum / count;
    }

    @RestMethod.Get
    public ClassSession get(Long classKey, @Optional("false") @QueryParam("grade") boolean computeGrade)
    {
        HashMap<Long, ClassSession> m = classes.get(getTeacherKey());
        if(m == null)
            return null;
        ClassSession c;
        c = m.get(classKey);
        if(c == null)
            return null;
        if(!computeGrade)
            return c;
        return c.setMeanGrade(computeMeanGrade(getTeacherKey(), classKey));
    }

    @Override
    public CreateResponse create(ClassSession c)
    {
        HashMap<Long, ClassSession> m = classes.get(getTeacherKey());
        if(m == null)
        {
            m = new HashMap<Long, ClassSession>();
            classes.put(getTeacherKey(), m);
            ids.put(getTeacherKey(), new IDGenerator());
        }
        long id = ids.get(getTeacherKey()).nextId();
        while(m.get(id) != null)
            id = ids.get(getTeacherKey()).nextId();
        m.put(id, c);
        return new CreateResponse(id);
    }

    @Override
    public UpdateResponse update(Long key, ClassSession c)
    {
        HashMap<Long, ClassSession> m = classes.get(getTeacherKey());
        if(m == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        m.put(key, c);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }

    @Override
    public UpdateResponse delete(Long key)
    {

        HashMap<Long, ClassSession> m = classes.get(getTeacherKey());
        if(m == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        if(m.get(key) == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);

        m.remove(key);
        return new UpdateResponse(HttpStatus.S_200_OK);
    }
}
