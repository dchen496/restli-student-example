package com.example.gradebook;

import com.linkedin.r2.message.rest.RestException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.common.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * User: douglas
 * Date: 6/24/13
 * Time: 10:36 AM
 */
public class GradebookClient
{
    private Scanner sc = new Scanner(System.in);
    private HttpClientFactory http = new HttpClientFactory();
    private Client r2Client = new TransportClientAdapter(http.getClient(Collections.<String, String>emptyMap()));
    private RestClient restClient = new RestClient(r2Client, "http://localhost:8080/server/");

    public static void main(String[] args) {
       new GradebookClient().run();
    }

    public void run() {
        String[] actions = {
                "Exit",
                "Get a student",
                "Create a student",
                "Update a student",
                "Delete a student",
                "Get a teacher",
                "Create a teacher",
                "Update a teacher",
                "Delete a teacher",
                "Get a class",
                "Create a class",
                "Update a class",
                "Delete a class",
                "Get student's grade in class",
                "Add student to class",
                "Delete student from class",
                "Find students by grade"
        };
        while(true) {
            try {
                System.out.println();
                for(int i = 0; i < actions.length; i++)
                    System.out.printf("(%d) %s\n", i, actions[i]);
                int action = (int) getLong("");
                switch(action)
                {
                    case 0:
                        return;
                    case 1:
                        getStudent();
                        break;
                    case 2:
                        createStudent();
                        break;
                    case 3:
                        updateStudent();
                        break;
                    case 4:
                        deleteStudent();
                        break;
                    case 5:
                        getTeacher();
                        break;
                    case 6:
                        createTeacher();
                        break;
                    case 7:
                        updateTeacher();
                        break;
                    case 8:
                        deleteTeacher();
                        break;
                    case 9:
                        getClassSession();
                        break;
                    case 10:
                        createClass();
                        break;
                    case 11:
                        updateClass();
                        break;
                    case 12:
                        deleteClass();
                        break;
                    case 13:
                        getAttendee();
                        break;
                    case 14:
                        updateAttendee();
                        break;
                    case 15:
                        deleteAttendee();
                        break;
                    case 16:
                        findStudentsByGrade();
                        break;
                }
            }
            catch (RestException e)
            {
                if(e.getResponse().getStatus() == HttpStatus.S_404_NOT_FOUND.getCode())
                {
                    System.out.println("Not found.");
                }
                else
                {
                    System.out.println(e);
                    System.out.println(e.getResponse().getEntity().asString("UTF-8"));
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }

    private long getLong(String prompt)
    {
        System.out.println(prompt);
        boolean pass = false;
        long ret = 0;
        while(!pass)
        {
            try
            {
                ret = Long.parseLong(sc.nextLine());
                pass = true;
            }
            catch(NumberFormatException e) {}
        }
        return ret;
    }

    private String getLine(String prompt)
    {
        System.out.println(prompt);
        return sc.nextLine();
    }

    private void getStudent() throws Exception
    {
        long id = getLong("ID?");
        StudentGetBuilder builder = new StudentBuilders().get();
        Response<Student> resp = restClient.sendRequest(builder.id(id).build()).getResponse();
        Student s = resp.getEntity();
        System.out.printf("Name: %s, Grade: %d\n", s.getName(), s.getGrade());
    }

    private void createStudent() throws Exception
    {
        String name = getLine("Name?");
        long grade = getLong("Grade?");
        StudentCreateBuilder builder = new StudentBuilders().create();
        Student s = new Student().setGrade((int) grade).setName(name);
        Response<EmptyRecord> resp = restClient.sendRequest(builder.input(s).build()).getResponse();
        System.out.printf("new ID: %s\n", resp.getId());
    }

    private void updateStudent() throws Exception
    {
        long id = getLong("ID?");
        String name = getLine("Name?");
        long grade = getLong("Grade?");
        StudentUpdateBuilder builder = new StudentBuilders().update();
        Student s = new Student().setGrade((int) grade).setName(name);
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(id).input(s).build()).getResponse();
    }

    private void deleteStudent() throws Exception
    {
        long id = getLong("ID?");
        StudentDeleteBuilder builder = new StudentBuilders().delete();
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(id).build()).getResponse();
    }
    
    private void getTeacher() throws Exception
    {
        long id = getLong("ID?");
        TeacherGetBuilder builder = new TeacherBuilders().get();
        Response<Teacher> resp = restClient.sendRequest(builder.id(id).build()).getResponse();
        Teacher s = resp.getEntity();
        System.out.printf("Name: %s\n", s.getName());
    }

    private void createTeacher() throws Exception
    {
        String name = getLine("Name?");
        TeacherCreateBuilder builder = new TeacherBuilders().create();
        Teacher s = new Teacher().setName(name);
        Response<EmptyRecord> resp = restClient.sendRequest(builder.input(s).build()).getResponse();
        System.out.printf("new ID: %s\n", resp.getId());
    }

    private void updateTeacher() throws Exception
    {
        long id = getLong("ID?");
        String name = getLine("Name?");
        TeacherUpdateBuilder builder = new TeacherBuilders().update();
        Teacher s = new Teacher().setName(name);
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(id).input(s).build()).getResponse();
    }

    private void deleteTeacher() throws Exception
    {
        long id = getLong("ID?");
        TeacherDeleteBuilder builder = new TeacherBuilders().delete();
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(id).build()).getResponse();
    }


    private void getClassSession() throws Exception
    {
        long tid = getLong("Teacher ID?");
        long cid = getLong("Class ID?");

        long choice = getLong("Get average grade? (1 = yes)");

        ClassesGetBuilder builder = new ClassesBuilders().get();
        builder.teacherIdKey(tid).id(cid);
        if(choice == 1)
            builder.gradeParam(true);
        Response<ClassSession> resp = restClient.sendRequest(builder.build()).getResponse();
        ClassSession c = resp.getEntity();
        System.out.printf("Name: %s, Description: %s, Period: %s", c.getName(), c.getDescription(), c.getPeriod());
        if(choice == 1)
            System.out.printf(", Average grade: %f", c.getMeanGrade());
        System.out.println();
    }

    private ClassPeriod getClassPeriod()
    {
        ClassPeriod cp = ClassPeriod.$UNKNOWN;
        while(cp == ClassPeriod.$UNKNOWN)
        {
            int period = (int) getLong("Period? (0 = morning, 1 = afternoon, 2 = evening)");
            switch(period)
            {
                case 0:
                    cp = ClassPeriod.MORNING;
                    break;
                case 1:
                    cp = ClassPeriod.AFTERNOON;
                    break;
                case 2:
                    cp = ClassPeriod.EVENING;
                    break;
            }
        }
        return cp;
    }

    private void createClass() throws Exception
    {
        long tid = getLong("Teacher ID?");
        String name = getLine("Name?");
        String desc = getLine("Description?");
        ClassPeriod cp = getClassPeriod();
        ClassSession c = new ClassSession().setName(name).setDescription(desc).setPeriod(cp);
        ClassesCreateBuilder builder =  new ClassesBuilders().create();
        Request<EmptyRecord> rq = builder.teacherIdKey(tid).input(c).build();
        Response<EmptyRecord> resp = restClient.sendRequest(rq).getResponse();
        System.out.printf("new ID: %s\n", resp.getId());
    }

    private void updateClass() throws Exception
    {
        long tid = getLong("Teacher ID?");
        long cid = getLong("Class ID?");
        String name = getLine("Name?");
        String desc = getLine("Description?");
        ClassPeriod cp = getClassPeriod();
        ClassSession c = new ClassSession().setName(name).setDescription(desc).setPeriod(cp);
        ClassesUpdateBuilder builder =  new ClassesBuilders().update();
        Request<EmptyRecord> rq = builder.teacherIdKey(tid).id(cid).input(c).build();
        Response<EmptyRecord> resp = restClient.sendRequest(rq).getResponse();
    }

    private void deleteClass() throws Exception
    {
        long tid = getLong("Teacher ID?");
        long cid = getLong("Class ID?");
        ClassesDeleteBuilder builder =  new ClassesBuilders().delete();
        Request<EmptyRecord> rq = builder.teacherIdKey(tid).id(cid).build();
        Response<EmptyRecord> resp = restClient.sendRequest(rq).getResponse();
    }

    private CompoundKey getAttendeeKey()
    {
        long tid = getLong("Teacher ID?");
        long cid = getLong("Class ID?");
        long sid = getLong("Student ID?");
        return new CompoundKey().append("teacherId", tid).append("classSessionId", cid).append("studentId", sid);
    }

    private void getAttendee() throws Exception
    {
        ClassAttendeesGetBuilder builder = new ClassAttendeesBuilders().get();
        Response<ClassAttendee> resp = restClient.sendRequest(builder.id(getAttendeeKey()).build()).getResponse();
        ClassAttendee s = resp.getEntity();
        System.out.printf("Grade: %s\n", s.getGrade());
    }

    private void updateAttendee() throws Exception
    {
        ClassAttendeesUpdateBuilder builder = new ClassAttendeesBuilders().update();
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(getAttendeeKey()).build()).getResponse();
    }

    private void deleteAttendee() throws Exception
    {
        ClassAttendeesDeleteBuilder builder = new ClassAttendeesBuilders().delete();
        Response<EmptyRecord> resp = restClient.sendRequest(builder.id(getAttendeeKey()).build()).getResponse();
    }

    private void findStudentsByGrade() throws Exception
    {
        StudentFindByGradeBuilder builder = new StudentBuilders().findByGrade();
        builder.gradeParam((int) getLong("Grade?"));
        Response<CollectionResponse<Student>> resp = restClient.sendRequest(builder.build()).getResponse();
        List<Student> list = resp.getEntity().getElements();
        for(Student s : list)
        {
            System.out.printf("Name: %s, Grade: %d\n", s.getName(), s.getGrade());
        }
    }
}
