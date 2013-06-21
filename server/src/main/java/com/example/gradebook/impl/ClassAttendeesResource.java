package com.example.gradebook.impl;

import com.example.gradebook.ClassAttendee;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.server.UpdateResponse;
import com.linkedin.restli.server.annotations.Key;
import com.linkedin.restli.server.annotations.RestLiAssociation;
import com.linkedin.restli.server.resources.AssociationResourceTemplate;

import java.util.HashMap;
import java.util.HashSet;

/**
 * User: douglas
 * Date: 6/23/13
 * Time: 10:44 PM
 */

@RestLiAssociation(name = "classAttendees", namespace = "com.example.gradebook",
    assocKeys = {
        @Key(name = "teacherId", type = Long.class),
        @Key(name = "classSessionId", type = Long.class),
        @Key(name = "studentId", type = long.class)
    }
)

public class ClassAttendeesResource extends AssociationResourceTemplate<ClassAttendee>
{
    // teacher id -> class id -> student id
    public static HashMap<Long, HashMap<Long, HashMap<Long, ClassAttendee>>> attendees =
            new HashMap<Long, HashMap<Long, HashMap<Long, ClassAttendee>>>();
    static {
        HashMap<Long, ClassAttendee> class1 = new HashMap<Long, ClassAttendee>();
        class1.put(0L, new ClassAttendee().setGrade(95));
        class1.put(2L, new ClassAttendee().setGrade(80));
        attendees.put(1L, new HashMap<Long, HashMap<Long, ClassAttendee>>());
        attendees.get(1L).put(0L, class1);
    }

    @Override
    public ClassAttendee get(CompoundKey key)
    {
        Long teacherId = key.getPartAsLong("teacherId");
        Long classSessionId = key.getPartAsLong("classSessionId");
        Long studentId = key.getPartAsLong("studentId");

        HashMap<Long, HashMap<Long, ClassAttendee>> m = attendees.get(teacherId);
        if(m == null)
            return null;
        HashMap<Long, ClassAttendee> p = m.get(classSessionId);
        if(p == null)
            return null;
        if(p.get(studentId) == null)
            return null;

        return p.get(studentId);
    }

    @Override
    public UpdateResponse update(CompoundKey key, ClassAttendee entity)
    {
        Long teacherId = key.getPartAsLong("teacherId");
        Long classSessionId = key.getPartAsLong("classSessionId");
        Long studentId = key.getPartAsLong("studentId");

        if(ClassSessionResource.classes.get(teacherId) == null ||
           ClassSessionResource.classes.get(classSessionId) == null ||
           StudentResource.students.get(studentId) == null)
        {
           return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        }

        HashMap<Long, HashMap<Long, ClassAttendee>> m = attendees.get(teacherId);
        if(m == null)
            attendees.put(teacherId, new HashMap<Long, HashMap<Long, ClassAttendee>>());
        HashMap<Long, ClassAttendee> p = m.get(classSessionId);
        if(p == null)
            m.put(classSessionId, new HashMap<Long, ClassAttendee>());
        p.put(studentId, entity);
        return new UpdateResponse(HttpStatus.S_200_OK);

    }

    @Override
    public UpdateResponse delete(CompoundKey key)
    {
        Long teacherId = key.getPartAsLong("teacherId");
        Long classSessionId = key.getPartAsLong("classSessionId");
        Long studentId = key.getPartAsLong("studentId");

        HashMap<Long, HashMap<Long, ClassAttendee>> m = attendees.get(teacherId);
        if(m == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        HashMap<Long, ClassAttendee> p = m.get(classSessionId);
        if(p == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);
        if(p.get(studentId) == null)
            return new UpdateResponse(HttpStatus.S_404_NOT_FOUND);

        p.remove(studentId);

        return new UpdateResponse(HttpStatus.S_200_OK);
    }
}
