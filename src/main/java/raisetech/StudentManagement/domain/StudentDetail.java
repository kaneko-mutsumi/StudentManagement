package raisetech.StudentManagement.domain;

import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import java.util.List;
import java.util.ArrayList;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 学生詳細情報（複数コース対応）
 */
@Getter
@Setter
public class StudentDetail {

  private Student student;
  private List<StudentCourse> StudentCourse = new ArrayList<>();

  /**
   * コースが存在するかチェック
   */
  public boolean hasCourses() {
    return StudentCourse != null && !StudentCourse.isEmpty();
  }

  /**
   * 主コース取得（編集時の後方互換性用）
   */
  public StudentCourse getPrimaryCourse() {
    return hasCourses() ? StudentCourse.get(0) : null;
  }
}