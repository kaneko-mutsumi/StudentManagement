package raisetech.StudentManagement.domain;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;

@Getter
@Setter
public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private StudentsCourses studentsCourse;

  public StudentDetail() {
  }

  public StudentDetail(Student student, StudentsCourses studentsCourse) {
    this.student = student;
    this.studentsCourse = studentsCourse;
  }

  // 学生名を取得するメソッド
  public String getStudentName() {
    if (student == null) {
      return "不明";
    }
    return student.getName();
  }

  // コース名を取得するメソッド
  public String getCourseName() {
    if (studentsCourse == null) {
      return "未登録";
    }
    return studentsCourse.getCourseName();
  }

  @Override
  public String toString() {
    return "StudentDetail{" +
        "studentName=" + getStudentName() +
        ", courseName=" + getCourseName() +
        '}';
  }
}