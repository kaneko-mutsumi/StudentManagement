package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

/**
 * 学生情報に関するHTTPリクエストを処理するController
 * 単一責任の原則に従い、Webリクエストの処理のみを行う
 */
@Controller
public class StudentController {

  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
  private final StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 学生一覧画面を表示する
   */
  @GetMapping("/studentList")
  public String showStudentList(Model model) {
    try {
      List<StudentDetail> studentList = service.searchStudentList();
      model.addAttribute("studentList", studentList);
      return "studentList";
    } catch (Exception e) {
      logger.error("学生一覧取得でエラーが発生しました", e);
      model.addAttribute("errorMessage", "データの取得に失敗しました");
      return "error";
    }
  }

  /**
   * コース一覧画面を表示する
   */
  @GetMapping("/courseList")
  public String showCourseList(Model model) {
    try {
      List<StudentsCourses> coursesList = service.searchStudentsCourseList();
      model.addAttribute("courseList", coursesList);
      return "courseList";
    } catch (Exception e) {
      logger.error("コース一覧取得でエラーが発生しました", e);
      model.addAttribute("errorMessage", "データの取得に失敗しました");
      return "error";
    }
  }

  /**
   * 新規学生登録画面を表示する
   */
  @GetMapping("/newStudent")
  public String showNewStudentForm(Model model) {
    try {
      model.addAttribute("studentForm", new StudentForm());
      addCourseOptionsToModel(model);
      return "registerStudent";
    } catch (Exception e) {
      logger.error("新規登録画面表示でエラーが発生しました", e);
      return "redirect:/studentList";
    }
  }

  /**
   * 新規学生登録処理を実行する
   */
  @PostMapping("/registerStudent")
  public String registerStudent(
      @Valid @ModelAttribute("studentForm") StudentForm studentForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes) {

    logger.info("学生登録処理開始: {}", studentForm.getName());

    try {
      // バリデーションエラーがある場合
      if (result.hasErrors()) {
        logger.warn("バリデーションエラー: {}", result.getAllErrors());
        addCourseOptionsToModel(model);
        return "registerStudent";
      }

      // 登録処理
      Student studentEntity = studentForm.toStudentEntity();
      StudentsCourses courseEntity = studentForm.toCourseEntity();
      StudentDetail detail = new StudentDetail();
      detail.setStudent(studentEntity);
      detail.setStudentsCourse(courseEntity);

      service.registerStudentAndCourse(detail);

      logger.info("学生登録完了: ID={}, 名前={}", studentEntity.getId(), studentEntity.getName());
      redirectAttributes.addFlashAttribute("successMessage", "学生情報を登録しました。");
      return "redirect:/studentList";

    } catch (Exception e) {
      logger.error("学生登録でエラーが発生しました", e);
      redirectAttributes.addFlashAttribute("errorMessage", "登録処理中にエラーが発生しました。");
      return "redirect:/newStudent";
    }
  }

  /**
   * 編集画面を表示する
   */
  @GetMapping("/student/{id}/edit")
  public String showEditForm(@PathVariable int id, Model model) {
    try {
      StudentDetail studentDetail = service.getStudentDetailById(id);
      if (studentDetail == null) {
        logger.warn("指定された学生が見つかりません: ID={}", id);
        return "redirect:/studentList";
      }

      // 既存データをフォームに変換
      StudentForm studentForm = StudentForm.fromStudentDetail(studentDetail);

      // 画面に渡すデータを設定
      model.addAttribute("studentForm", studentForm);
      addCourseOptionsToModel(model);

      // 編集画面を表示
      return "edit";
    } catch (Exception e) {
      logger.error("編集画面表示でエラーが発生しました: ID={}", id, e);
      return "redirect:/studentList";
    }
  }

  /**
   * 更新処理を実行する
   */
  @PostMapping("/student/update")
  public String updateStudent(
      @Valid @ModelAttribute StudentForm studentForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes) {

    logger.info("学生更新処理開始: ID={}, 名前={}", studentForm.getId(), studentForm.getName());

    try {
      // 入力チェックでエラーがある場合は編集画面に戻る
      if (result.hasErrors()) {
        logger.warn("更新時バリデーションエラー: {}", result.getAllErrors());
        addCourseOptionsToModel(model);
        return "edit";
      }

      StudentDetail studentDetail = studentForm.toStudentDetail();
      service.updateStudent(studentDetail);

      logger.info("学生更新完了: ID={}", studentForm.getId());
      redirectAttributes.addFlashAttribute("successMessage", "学生情報を更新しました。");
      return "redirect:/studentList";

    } catch (Exception e) {
      logger.error("学生更新でエラーが発生しました: ID={}", studentForm.getId(), e);
      redirectAttributes.addFlashAttribute("errorMessage", "更新処理中にエラーが発生しました。");
      return "redirect:/student/" + studentForm.getId() + "/edit";
    }
  }

  /**
   * コース選択肢をモデルに追加する共通メソッド
   * DRY原則（Don't Repeat Yourself）に従い、重複コードを削除
   */
  private void addCourseOptionsToModel(Model model) {
    model.addAttribute("courseOptions",
        List.of("Java入門", "Spring実践", "Webアプリ開発"));
  }
}