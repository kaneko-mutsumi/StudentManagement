package raisetech.StudentManagement.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;

@Component
public class StudentConverter {

  public List<StudentDetail> convertStudentDetails(
      List<Student> students,
      List<StudentsCourses> studentsCourses) {
    List<StudentDetail> studentDetails = new ArrayList<>();

    for (Student student : students) {
      StudentDetail detail = new StudentDetail();
      detail.setStudent(student);

      StudentsCourses matchedCourse = studentsCourses.stream()
          .filter(sc -> Objects.equals(student.getId(), sc.getStudentId()))
          .findFirst()
          .orElse(null);

      detail.setStudentsCourse(matchedCourse);
      studentDetails.add(detail);
    }
    return studentDetails;
  }
}