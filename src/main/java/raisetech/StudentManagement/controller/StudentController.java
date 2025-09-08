package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

/**
 * 学生管理コントローラー
 */
@RestController
@RequestMapping("/api")
public class StudentController {

  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

  @Autowired
  private StudentService service;

  @Autowired
  private StudentConverter converter;

  /**
   * 学生一覧表示
   */
  @GetMapping("/students")
  public ResponseEntity<List<StudentDetail>> getStudents() {
    try {
      logger.info("学生一覧画面アクセス");

      // 2クエリでデータ取得
      List<Student> students = service.getStudents();
      List<StudentCourse> courses = service.getCourses();

      // Converterで結合（O(S + C)）
      List<StudentDetail> studentDetails = converter.toDetails(students, courses);

      logger.info("REST API: 学生一覧表示完了: {}件", studentDetails.size());
      return ResponseEntity.ok(studentDetails);

    } catch (Exception e) {
      logger.error("REST API: 学生一覧取得エラー", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * コース一覧表示
   */
  @GetMapping("/course")
  public ResponseEntity<List<StudentCourse>> getCourses() {
    try {
      logger.info("REST API: コース一覧取得");
      List<StudentCourse> courses = service.getCourses();
      return ResponseEntity.ok(courses);
    } catch (Exception e) {
      logger.error("REST API: コース一覧取得エラー", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * 特定学生取得
   */
  @GetMapping("/students/{id}")
  public ResponseEntity<StudentForm> getStudent(@PathVariable int id) {
    try {
      logger.info("REST API: 学生詳細取得: ID={}", id);
      StudentForm form = service.getStudentForm(id);
      return ResponseEntity.ok(form);
    } catch (Exception e) {
      logger.error("REST API: 学生詳細取得エラー: ID=" + id, e);
      return ResponseEntity.notFound().build();  // 404 Not Found
    }
  }


  /**
   * 学生新規登録処理
   */
  @PostMapping("/students")
  public ResponseEntity<ApiResponse> createStudent(@Valid @RequestBody StudentForm form) {
    try {
      logger.info("REST API: 学生登録: {}", form.getName());

      service.registerStudent(form);

      ApiResponse response = new ApiResponse("success", "学生を登録しました", form.getName());
      logger.info("REST API: 学生登録成功: {}", form.getName());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.error("REST API: 学生登録エラー: " + form.getName(), e);
      ApiResponse response = new ApiResponse("error", "登録に失敗しました", null);
      return ResponseEntity.badRequest().body(response);  // 400エラー
    }
  }

  /**
   * 学生情報更新
   */
  @PutMapping("/students/{id}")
  public ResponseEntity<ApiResponse> updateStudent(@PathVariable int id,
      @Valid @RequestBody StudentForm form) {
    try {
      logger.info("REST API: 学生更新: ID={}", id);

      form.setId(id);  // URLのIDをフォームに設定
      service.updateStudent(form);

      ApiResponse response = new ApiResponse("success", "学生情報を更新しました", form.getName());
      logger.info("REST API: 学生更新成功: ID={}", id);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.error("REST API: 学生更新エラー: ID=" + id, e);
      ApiResponse response = new ApiResponse("error", "更新に失敗しました", null);
      return ResponseEntity.badRequest().body(response);
    }
  }


  /**
   * 学生削除処理（ラジオボタン単一選択）
   */
  @DeleteMapping("/students/{id}")
  public ResponseEntity<ApiResponse> deleteStudent(@PathVariable int id) {

    try {
      logger.info("学生削除成功: ID={}", id);

      service.deleteStudent(id);

      ApiResponse response = new ApiResponse("success", "学生を削除しました", String.valueOf(id));
      logger.info("REST API: 学生削除成功: ID={}" , id);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      logger.error("REST API :学生削除エラー: ID=" + id, e);
      ApiResponse response = new ApiResponse("error", "削除に失敗しました", null);
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * API レスポンス用クラス
   */
  public static class ApiResponse {
    private String status;
    private String message;
    private String data;

    public ApiResponse(String status, String message, String data) {
      this.status = status;
      this.message = message;
      this.data = data;
    }

    // Getter methods
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getData() { return data; }
  }
}