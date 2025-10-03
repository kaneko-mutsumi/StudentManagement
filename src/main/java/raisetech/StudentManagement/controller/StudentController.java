package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import raisetech.StudentManagement.response.StudentApiResponse;
import raisetech.StudentManagement.service.StudentService;

/**
 * 学生管理コントローラー
 */
@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "学生管理", description = "学生情報とコース情報の管理API")
public class StudentController {

  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

  @Autowired
  private StudentService service;

  @Autowired
  private StudentConverter converter;

  /**
   * 学生一覧表示
   */
  @Operation(summary = "学生一覧取得", description = "有効な学生の一覧とそれぞれのコース情報を取得します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
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
  @Operation(summary = "コース一覧取得", description = "全コース情報を取得します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping("/courses")
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
   * 特定学生取得 例外処理テスト用に修正
   */
  @Operation(summary = "学生詳細取得", description = "指定されたIDの学生情報とコース情報を取得します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "404", description = "学生が見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping("/students/{id}")
  public ResponseEntity<StudentForm> getStudent(@PathVariable int id) {
    logger.info("REST API: 学生詳細取得: ID={}", id);

    StudentForm form = service.getStudentForm(id);
    return ResponseEntity.ok(form);
  }

  /**
   * 学生新規登録処理
   */
  @Operation(summary = "学生新規登録", description = "新しい学生とコース情報を登録します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "登録成功"),
      @ApiResponse(responseCode = "400", description = "入力内容エラー")
  })
  @PostMapping("/students")
  public ResponseEntity<StudentApiResponse> createStudent(@Valid @RequestBody StudentForm form) {
    logger.info("REST API: 学生登録開始: {}", form.getName());
    service.registerStudent(form);
    StudentApiResponse response = new StudentApiResponse("success", "学生を登録しました",
        form.getName());
    logger.info("REST API: 学生登録成功: {}", form.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 学生情報更新
   */
  @Operation(summary = "学生情報更新", description = "指定されたIDの学生情報を更新します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "入力内容エラー"),
      @ApiResponse(responseCode = "404", description = "更新対象が見つかりません")
  })
  @PutMapping("/students/{id}")
  public ResponseEntity<StudentApiResponse> updateStudent(@PathVariable int id,
      @Valid @RequestBody StudentForm form) {
    logger.info("REST API: 学生更新開始: ID={}", id);
    form.setId(id);
    service.updateStudent(form);
    StudentApiResponse response = new StudentApiResponse("success", "学生情報を更新しました",
        form.getName());
    logger.info("REST API: 学生更新成功: ID={}", id);
    return ResponseEntity.ok(response);
  }


  /**
   * 学生削除処理（ラジオボタン単一選択）
   */
  @Operation(summary = "学生削除", description = "指定されたIDの学生を論理削除します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "削除成功"),
      @ApiResponse(responseCode = "404", description = "削除対象が見つかりません")
  })
  @DeleteMapping("/students/{id}")
  public ResponseEntity<StudentApiResponse> deleteStudent(@PathVariable int id) {
    logger.info("REST API: 学生削除開始: ID={}", id);
    service.deleteStudent(id);
    StudentApiResponse response = new StudentApiResponse("success", "学生を削除しました",
        String.valueOf(id));
    logger.info("REST API: 学生削除成功: ID={}", id);
    return ResponseEntity.ok(response);
  }
}