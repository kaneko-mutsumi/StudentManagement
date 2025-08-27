package raisetech.StudentManagement.domain;

import lombok.Getter;
import lombok.Setter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import java.util.List;
import java.util.ArrayList;

/**
 * 学生詳細情報（複数コース対応）
 */
@Getter
@Setter
public class StudentDetail {

  private Student student;
  private List<StudentsCourses> studentsCourses = new ArrayList<>();

  /**
   * コースが存在するかチェック
   */
  public boolean hasCourses() {
    return studentsCourses != null && !studentsCourses.isEmpty();
  }

  /**
   * 主コース取得（編集時の後方互換性用）
   */
  public StudentsCourses getPrimaryCourse() {
    return hasCourses() ? studentsCourses.get(0) : null;
  }
}