package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 * 学生とコース情報の変換処理
 */
@Component
public class StudentConverter {

  /**
   * 学生リストとコースリストからStudentDetailリストを生成
   *
   * @param students 学生リスト（S件）
   * @param courses コースリスト（C件）
   * @return StudentDetailリスト（各StudentDetailはList<StudentCourse>を保持）
   */
  public List<StudentDetail> toDetails(List<Student> students, List<StudentCourse> courses) {

    // coursesがnullの場合は空のMapを使用
    Map<Integer, List<StudentCourse>> courseMap = courses != null
        ? courses.stream().collect(Collectors.groupingBy(StudentCourse::getStudentId))
        : new HashMap<>();

    // O(S): 学生ごとにコースリストを結合してStudentDetail生成
    return students.stream()
        .map(student -> createStudentDetail(student, courseMap.get(student.getId())))
        .collect(Collectors.toList());
  }

  /**
   * StudentDetailオブジェクトを生成
   *
   * @param student 学生情報
   * @param courses コースリスト（nullの場合は空Listを設定）
   * @return StudentDetail
   */
  private StudentDetail createStudentDetail(Student student, List<StudentCourse> courses) {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);

    // Null安全：コースが無い場合は空Listを設定
    detail.setStudentCourse(courses != null ? courses : new ArrayList<>());

    return detail;
  }
}