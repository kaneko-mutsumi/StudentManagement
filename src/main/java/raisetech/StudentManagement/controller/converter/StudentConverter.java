package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;

@Component
public class StudentConverter {

  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentsCourses> studentsCourses) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      StudentsCourses course = studentsCourses.stream()
          .filter(sc -> student.getId().equals(sc.getStudentId()))
          .findFirst()
          .orElse(null);

      studentDetail.setCourse(course);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
