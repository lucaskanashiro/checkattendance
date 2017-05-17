package br.usp.ime.checkattendance;

import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;

import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.models.Student;
import br.usp.ime.checkattendance.utils.Parser;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ParserTest {

    @Test(expected=JSONException.class)
    public void parseSeminarsWithWrongResponseData() throws JSONException {
        // The seminars data is an array value from "seminars" key, instead of "data"
        String response = "{\"success\":true,\"seminars\":" +
                "[{\"id\":\"1\",\"name\":\"seminario 1\"}," +
                "{\"id\":\"2\",\"name\":\"seminario 2\"}," +
                "{\"id\":\"3\",\"name\":\"seminario 3\"}]}";

        ArrayList<Seminar> seminars = Parser.parseSeminars(response);
    }

    @Test(expected=JSONException.class)
    public void perseSeminarsWithoutIdInSeminarsData() throws JSONException {
        // The seminars data are without ids
        String response = "{\"success\":true,\"seminars\": [{\"name\":\"seminario 1\"}," +
                "{\"name\":\"seminario 2\"},{\"name\":\"seminario 3\"}]}";

        ArrayList<Seminar> seminars = Parser.parseSeminars(response);
    }

    @Test(expected=JSONException.class)
    public void perseSeminarsWithoutNameInSeminarsData() throws JSONException {
        // The seminars data are without name
        String response = "{\"success\":true,\"seminars\": [{\"id\":\"1\"}," +
                "{\"id\":\"2\"},{\"id\":\"3\"}]}";

        ArrayList<Seminar> seminars = Parser.parseSeminars(response);
    }

    @Test
    public void parseSeminarsWithExpectedResponse() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"id\":\"1\",\"name\":\"seminario 1\"}," +
                "{\"id\":\"2\",\"name\":\"seminario 2\"}," +
                "{\"id\":\"3\",\"name\":\"seminario 3\"}]}";

        ArrayList<Seminar> seminars = Parser.parseSeminars(response);

        ArrayList<Seminar> seminarsExpected = new ArrayList<Seminar>();
        seminarsExpected.add(new Seminar("1", "seminario 1"));
        seminarsExpected.add(new Seminar("2", "seminario 2"));
        seminarsExpected.add(new Seminar("3", "seminario 3"));

        assertEquals(seminarsExpected.toString(), seminars.toString());
    }

    @Test(expected=JSONException.class)
    public void parseAttendedSeminarsWithWrongResponseData() throws JSONException {
        // The seminars data is an array value from "seminars" key, instead of "data"
        // each seminar has more attributes, but just seminar_id is usefull
        String response = "{\"success\":true,\"seminars\":" +
                "[{\"seminar_id\":\"1\"},{\"seminar_id\":\"2\"},{\"seminar_id\":\"3\"}]}";

        String attendedSeminars = Parser.parseAttendedSeminars(response);
    }

    @Test(expected=JSONException.class)
    public void parseAttendedSeminarsWithoutSeminarId() throws JSONException {
        // The seminars data are without seminar_id
        String response = "{\"success\":true,\"data\":" +
                "[{\"fake\":\"1\"},{\"fake\":\"2\"},{\"fake\":\"3\"}]}";

        String attendedSeminars = Parser.parseAttendedSeminars(response);
    }

    @Test
    public void parseAttendedSeminarsWithExpectedResponse() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"seminar_id\":\"1\"},{\"seminar_id\":\"2\"},{\"seminar_id\":\"3\"}]}";

        String attendedSeminars = Parser.parseAttendedSeminars(response);
        String attendedSeminarsExpected = "1 2 3 ";

        assertEquals(attendedSeminarsExpected, attendedSeminars);
    }

    @Test(expected=JSONException.class)
    public void parseAttendeesWithWrongResponseData() throws JSONException {
        // The students data is an array value from "students" key, instead of "data"
        // each attendee has more attributes, but just student_nusp is usefull
        String response = "{\"success\":true,\"students\":" +
                "[{\"student_nusp\":\"1\"},{\"student_nusp\":\"2\"},{\"student_nusp\":\"3\"}]}";

        String attendees = Parser.parseAttendees(response);
    }

    @Test(expected=JSONException.class)
    public void parseAttendeesWithoutStudentNusp() throws JSONException {
        // The students data are without student_nusp
        String response = "{\"success\":true,\"data\":" +
                "[{\"fake\":\"1\"},{\"fake\":\"2\"},{\"fake\":\"3\"}]}";

        String attendees = Parser.parseAttendees(response);
    }

    @Test
    public void parseAttendeesWithExpectedResponse() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"student_nusp\":\"1\"},{\"student_nusp\":\"2\"},{\"student_nusp\":\"3\"}]}";

        String attendedSeminars = Parser.parseAttendees(response);
        String attendedSeminarsExpected = "1 2 3 ";

        assertEquals(attendedSeminarsExpected, attendedSeminars);
    }

    @Test(expected=JSONException.class)
    public void parseSingleSeminarWithWrongResponseData() throws JSONException {
        // The seminar data is another JSON from "seminars" key, instead of "data"
        String response = "{\"success\":true,\"seminar\":" +
                "{\"id\":\"1\",\"name\":\"seminario 1\"}}";

        Seminar seminar = Parser.parseSingleSeminar(response);
    }

    @Test(expected=JSONException.class)
    public void parseSingleSeminarWithoutId() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "{\"name\":\"seminario 1\"}}";

        Seminar seminar = Parser.parseSingleSeminar(response);
    }

    @Test(expected=JSONException.class)
    public void parseSingleSeminarWithoutName() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "{\"id\":\"1\"}}";

        Seminar seminar = Parser.parseSingleSeminar(response);
    }

    @Test
    public void parseSingleSeminarWithExpectedResponseData() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "{\"id\":\"1\",\"name\":\"seminario 1\"}}";

        Seminar seminar = Parser.parseSingleSeminar(response);
        Seminar seminarExpected = new Seminar("1", "seminario 1");

        assertEquals(seminarExpected.toString(), seminar.toString());
    }

    @Test(expected=JSONException.class)
    public void parseDataWithWrongResponseData() throws JSONException {
        String response = "{\"success\":true,\"fake\":" +
                "{\"id\":\"1\",\"name\":\"seminario 1\"}}";

        String data = Parser.parseData(response, "id");
    }

    @Test
    public void parseDataWithExpectedResponse() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "{\"id\":\"1\",\"name\":\"seminario 1\"}}";

        String id = Parser.parseData(response, "id");
        String name = Parser.parseData(response, "name");

        assertEquals("1", id);
        assertEquals("seminario 1", name);
    }

    @Test
    public void parseStringResponseWithExpectedResponse() {
        String response = "{\"success\":true,\"data\":" +
                "[{\"id\":\"1\",\"name\":\"seminario 1\"}," +
                "{\"id\":\"2\",\"name\":\"seminario 2\"}," +
                "{\"id\":\"3\",\"name\":\"seminario 3\"}]}";

        ArrayList<Seminar> seminars = Parser.parseStringResponse(response);

        ArrayList<Seminar> seminarsExpected = new ArrayList<Seminar>();
        seminarsExpected.add(new Seminar("1", "seminario 1"));
        seminarsExpected.add(new Seminar("2", "seminario 2"));
        seminarsExpected.add(new Seminar("3", "seminario 3"));

        assertEquals(seminarsExpected.toString(), seminars.toString());
    }

    @Test(expected=JSONException.class)
    public void parseStudentWithWrongResponseData() throws JSONException {
        // use "students" key instead of "data"
        String response = "{\"success\":true,\"students\":" +
                "[{\"nusp\":\"11111111\",\"name\":\"Joao\"}," +
                "{\"nusp\":\"22222222\",\"name\":\"Maria\"}]}";

        ArrayList<Student> students = Parser.parseStudents(response);
    }

    @Test(expected=JSONException.class)
    public void parseStudentWithoutNusp() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"name\":\"Joao\"}," +
                "{\"name\":\"Maria\"}]}";

        ArrayList<Student> students = Parser.parseStudents(response);
    }

    @Test(expected=JSONException.class)
    public void parseStudentWithoutName() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"nusp\":\"11111111\"}," +
                "{\"nusp\":\"22222222\"}]}";

        ArrayList<Student> students = Parser.parseStudents(response);
    }


    @Test
    public void parseStudentWithExpectedResponse() throws JSONException {
        String response = "{\"success\":true,\"data\":" +
                "[{\"nusp\":\"11111111\",\"name\":\"Joao\"}," +
                "{\"nusp\":\"22222222\",\"name\":\"Maria\"}]}";

        ArrayList<Student> students = Parser.parseStudents(response);

        ArrayList<Student> studentsExpected = new ArrayList<Student>();
        studentsExpected.add(new Student("11111111", "Joao"));
        studentsExpected.add(new Student("22222222", "Maria"));

        assertEquals(studentsExpected.toString(), students.toString());
    }

    @Test
    public void parseAllStudentWithExpectedResponse() {
        String response = "{\"success\":true,\"data\":" +
                "[{\"nusp\":\"11111111\",\"name\":\"Joao\"}," +
                "{\"nusp\":\"22222222\",\"name\":\"Maria\"}]}";

        ArrayList<Student> students = Parser.parseAllStudents(response);

        ArrayList<Student> studentsExpected = new ArrayList<Student>();
        studentsExpected.add(new Student("11111111", "Joao"));
        studentsExpected.add(new Student("22222222", "Maria"));

        assertEquals(studentsExpected.toString(), students.toString());
    }
}