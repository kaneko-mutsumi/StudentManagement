package raisetech.StudentManagement.controller.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
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
   * @return StudentDetailリスト（各StudentDetailはList<StudentsCourses>を保持）
   */
  public List<StudentDetail> toDetails(List<Student> students, List<StudentsCourses> courses) {
    // O(C): コースリストを学生IDでグループ化（studentId → List<StudentsCourses>）
    Map<Integer, List<StudentsCourses>> courseMap = courses.stream()
        .collect(Collectors.groupingBy(StudentsCourses::getStudentId));

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
  private StudentDetail createStudentDetail(Student student, List<StudentsCourses> courses) {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);

    // Null安全：コースが無い場合は空Listを設定
    detail.setStudentsCourses(courses != null ? courses : new ArrayList<>());

    return detail;
  }
}