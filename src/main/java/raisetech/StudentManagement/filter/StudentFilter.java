package raisetech.StudentManagement.filter;
/**
 * 24課題のために作成したクラス
 *
 */

import org.springframework.stereotype.Component;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;

@Component
public class StudentFilter {

  // 30代の受講生だけを判定
  public boolean isIn30s(Student student){
    return student.getAge() >= 30 && student.getAge() <= 39;
  }

  //「javaコース」だけを判定
  public boolean isJavaCourse(StudentsCourses course){
    return "Javaコース".equals(course.getCourseName());
  }

}
