package raisetech.StudentManagement.converter;

import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;

@Component
public class StudentConverter {

  // StudentFormをStudentDetailに変換
  public StudentDetail convertToStudentDetail(StudentForm studentForm) {
    StudentDetail studentDetail = new StudentDetail();

    // Student情報を設定
    Student student = new Student();
    student.setId(studentForm.getId());
    student.setName(studentForm.getName());
    student.setKanaName(studentForm.getKanaName());
    student.setNickname(studentForm.getNickname());
    student.setEmail(studentForm.getEmail());
    student.setArea(studentForm.getArea());
    student.setAge(studentForm.getAge());
    student.setSex(studentForm.getSex());
    student.setRemark(studentForm.getRemark());
    student.setDeleted(false);

    studentDetail.setStudent(student);

    // StudentsCourses情報を設定
    StudentsCourses course = new StudentsCourses();
    course.setStudentId(studentForm.getId());
    course.setCourseName(studentForm.getCourseName());
    course.setCourseStartAt(studentForm.getCourseStartAt());
    course.setCourseEndAt(studentForm.getCourseEndAt());

    studentDetail.setStudentsCourse(course);

    return studentDetail;
  }

  // StudentDetailをStudentFormに変換
  public StudentForm convertToStudentForm(StudentDetail studentDetail) {
    StudentForm studentForm = new StudentForm();

    if (studentDetail.getStudent() != null) {
      Student student = studentDetail.getStudent();
      studentForm.setId(student.getId());
      studentForm.setName(student.getName());
      studentForm.setKanaName(student.getKanaName());
      studentForm.setNickname(student.getNickname());
      studentForm.setEmail(student.getEmail());
      studentForm.setArea(student.getArea());
      studentForm.setAge(student.getAge());
      studentForm.setSex(student.getSex());
      studentForm.setRemark(student.getRemark());
    }

    if (studentDetail.getStudentsCourse() != null) {
      StudentsCourses course = studentDetail.getStudentsCourse();
      studentForm.setCourseName(course.getCourseName());
      studentForm.setCourseStartAt(course.getCourseStartAt());
      studentForm.setCourseEndAt(course.getCourseEndAt());
    }

    return studentForm;
  }
}