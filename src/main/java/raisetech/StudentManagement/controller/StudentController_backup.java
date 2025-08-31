package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

/**
 * 学生管理コントローラー
 */
@Controller
public class StudentController_backup {

  private static final Logger logger = LoggerFactory.getLogger(StudentController_backup.class);

  @Autowired
  private StudentService service;

  @Autowired
  private StudentConverter converter;

  /**
   * 学生一覧表示（N+1完全回避）
   */
  @GetMapping("/studentList")
  public String studentList(Model model) {
    try {
      logger.info("学生一覧画面アクセス");

      // 2クエリでデータ取得
      List<Student> students = service.getStudents();
      List<StudentsCourses> courses = service.getCourses();

      // Converterで結合（O(S + C)）
      List<StudentDetail> studentDetails = converter.toDetails(students, courses);

      model.addAttribute("studentList", studentDetails);
      logger.info("学生一覧表示完了: {}件", studentDetails.size());

      return "studentList";
    } catch (Exception e) {
      logger.error("学生一覧取得エラー", e);
      model.addAttribute("errorMessage", "学生一覧の取得に失敗しました。管理者にお問い合わせください。");
      model.addAttribute("studentList", new ArrayList<>());
      return "studentList";
    }
  }

  /**
   * コース一覧表示
   */
  @GetMapping("/courseList")
  public String courseList(Model model) {
    try {
      logger.info("コース一覧画面アクセス");
      List<StudentsCourses> courses = service.getCourses();
      model.addAttribute("courseList", courses);
      return "courseList";
    } catch (Exception e) {
      logger.error("コース一覧取得エラー", e);
      model.addAttribute("errorMessage", "コース一覧の取得に失敗しました。管理者にお問い合わせください。");
      model.addAttribute("courseList", new ArrayList<>());
      return "courseList";
    }
  }

  /**
   * 新規登録画面表示
   */
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    logger.info("新規登録画面アクセス");
    model.addAttribute("studentForm", new StudentForm());
    return "registerStudent";
  }

  /**
   * 学生新規登録処理
   */
  @PostMapping("/registerStudent")
  public String registerStudent(@Valid @ModelAttribute StudentForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    logger.info("学生登録処理開始: {}", form.getName());

    if (bindingResult.hasErrors()) {
      logger.warn("バリデーションエラー: {}", bindingResult.getAllErrors());
      return "registerStudent";
    }

    try {
      service.registerStudent(form);
      redirectAttributes.addFlashAttribute("successMessage", "学生を登録しました");
      logger.info("学生登録成功: {}", form.getName());
    } catch (Exception e) {
      logger.error("学生登録エラー: " + form.getName(), e);
      redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました");
    }

    return "redirect:/studentList";
  }

  /**
   * 学生編集画面表示
   */
  @GetMapping("/student/{id}/edit")
  public String editStudent(@PathVariable int id, Model model,
      RedirectAttributes redirectAttributes) {
    try {
      logger.info("編集画面アクセス: ID={}", id);
      StudentForm form = service.getStudentForm(id);
      model.addAttribute("studentForm", form);
      return "edit";
    } catch (Exception e) {
      logger.error("学生詳細取得エラー: ID=" + id, e);
      redirectAttributes.addFlashAttribute("errorMessage", "学生情報の取得に失敗しました");
      return "redirect:/studentList";
    }
  }

  /**
   * 学生情報更新処理
   */
  @PostMapping("/student/update")
  public String updateStudent(@Valid @ModelAttribute StudentForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    logger.info("学生更新処理開始: ID={}", form.getId());

    if (bindingResult.hasErrors()) {
      logger.warn("バリデーションエラー: ID={}", form.getId());
      return "edit";
    }

    try {
      service.updateStudent(form);
      redirectAttributes.addFlashAttribute("successMessage", "学生情報を更新しました");
      logger.info("学生更新成功: ID={}", form.getId());
    } catch (Exception e) {
      logger.error("学生更新エラー: ID=" + form.getId(), e);
      redirectAttributes.addFlashAttribute("errorMessage", "更新に失敗しました");
    }

    return "redirect:/studentList";
  }

  /**
   * 学生削除処理（ラジオボタン単一選択）
   */
  @PostMapping("/student/delete")
  public String deleteStudent(@RequestParam(value = "selectedId", required = false) Integer selectedId,
      RedirectAttributes redirectAttributes) {
    logger.info("学生削除処理開始: ID={}", selectedId);

    if (selectedId == null) {
      logger.warn("削除対象未選択");
      redirectAttributes.addFlashAttribute("errorMessage", "削除する学生を選択してください");
      return "redirect:/studentList";
    }

    try {
      service.deleteStudent(selectedId);
      redirectAttributes.addFlashAttribute("successMessage", "学生を削除しました");
      logger.info("学生削除成功: ID={}", selectedId);
    } catch (Exception e) {
      logger.error("学生削除エラー: ID=" + selectedId, e);
      redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました");
    }

    return "redirect:/studentList";
  }
}