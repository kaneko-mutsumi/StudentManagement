package raisetech.StudentManagement.controller;

import static raisetech.StudentManagement.constants.ViewNames.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

/**
 * HTMLを返すController（Thymeleaf用）
 */
@Controller
public class StudentViewController {

  private static final Logger logger = LoggerFactory.getLogger(StudentViewController.class);

  private final StudentService service;
  private final StudentConverter converter;

  public StudentViewController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  /**
   * 学生一覧表示
   */
  @GetMapping("/studentList")
  public String showStudentList(Model model) {
    logger.info("学生一覧画面表示");

    List<Student> students = service.getStudents();
    List<StudentCourse> courses = service.getCourses();
    List<StudentDetail> studentDetails = converter.toDetails(students, courses);

    model.addAttribute("studentList", studentDetails);
    return STUDENT_LIST;  }

  /**
   * コース一覧表示
   */
  @GetMapping("/courseList")
  public String showCourseList(Model model) {
    logger.info("コース一覧画面表示");

    List<StudentCourse> courses = service.getCourses();
    model.addAttribute("courseList", courses);
    return COURSE_LIST;  }

  /**
   * 新規登録画面表示
   */
  @GetMapping("/newStudent")
  public String showNewStudentForm(Model model) {
    logger.info("新規登録画面表示");
    model.addAttribute("studentForm", new StudentForm());
    return NEW_STUDENT;
  }

  /**
   * 新規登録処理
   */
  @PostMapping("/registerStudent")
  public String registerStudent(@Validated @ModelAttribute StudentForm form,
      BindingResult result,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return "newStudent";
    }

    try {
      service.registerStudent(form);
      redirectAttributes.addFlashAttribute("successMessage", "学生を登録しました");
      return REDIRECT_STUDENT_LIST;
    } catch (Exception e) {
      logger.error("学生登録エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました");
      return REDIRECT_NEW_STUDENT;
    }
  }

  /**
   * 編集画面表示
   */
  @GetMapping("/student/{id}/edit")
  public String showEditForm(@PathVariable int id, Model model,
      RedirectAttributes redirectAttributes) {

    try {
      StudentForm form = service.getStudentForm(id);
      model.addAttribute("studentForm", form);
      return EDIT_STUDENT;
    } catch (Exception e) {
      logger.error("学生詳細取得エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "学生が見つかりません");
      return REDIRECT_STUDENT_LIST;
    }
  }

  /**
   * 更新処理
   */
  @PostMapping("/student/update")
  public String updateStudent(@Validated @ModelAttribute StudentForm form,
      BindingResult result,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return EDIT_STUDENT;
    }

    try {
      service.updateStudent(form);
      redirectAttributes.addFlashAttribute("successMessage", "学生情報を更新しました");
      return REDIRECT_STUDENT_LIST;
    } catch (Exception e) {
      logger.error("学生更新エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "更新に失敗しました");
      return REDIRECT_STUDENT_LIST;
    }
  }

  /**
   * 削除処理
   */
  @PostMapping("/student/delete")
  public String deleteStudent(@RequestParam int selectedId,
      RedirectAttributes redirectAttributes) {

    try {
      service.deleteStudent(selectedId);
      redirectAttributes.addFlashAttribute("successMessage", "学生を削除しました");
    } catch (Exception e) {
      logger.error("学生削除エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました");
    }

    return REDIRECT_STUDENT_LIST;
  }

  /**
   * 学生検索
   * @param name 名前（部分一致）
   * @param area 地域（部分一致）
   * @param courseName コース名（完全一致）
   * @param enrollmentStatus 申込状況（完全一致）
   * @param model ビューに渡すデータ
   * @return 学生一覧画面
   */
  @GetMapping("/students/search")
  public String searchStudents(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String area,
      @RequestParam(required = false) String courseName,
      @RequestParam(required = false) String enrollmentStatus,
      Model model) {

    logger.info("学生検索リクエスト: name={}, area={}, courseName={}, status={}",
        name, area, courseName, enrollmentStatus);

    try {
      // 全て空の場合は全件表示
      List<StudentDetail> studentDetails;
      if ((name == null || name.isEmpty()) &&
          (area == null || area.isEmpty()) &&
          (courseName == null || courseName.isEmpty()) &&
          (enrollmentStatus == null || enrollmentStatus.isEmpty())) {

        logger.info("検索条件なし - 全件表示");
        List<Student> students = service.getStudents();
        List<StudentCourse> courses = service.getCourses();
        studentDetails = converter.toDetails(students, courses);
      } else {
        logger.info("検索条件あり - 絞り込み検索");
        studentDetails = service.searchStudents(name, area, courseName, enrollmentStatus);
      }

      model.addAttribute("studentList", studentDetails);
      model.addAttribute("searchName", name);
      model.addAttribute("searchArea", area);
      model.addAttribute("searchCourseName", courseName);
      model.addAttribute("searchEnrollmentStatus", enrollmentStatus);

      logger.info("検索結果: {}件", studentDetails.size());

      return STUDENT_LIST;

    } catch (Exception e) {
      logger.error("学生検索でエラー発生", e);
      model.addAttribute("errorMessage", "検索に失敗しました: " + e.getMessage());
      return STUDENT_LIST;
    }
  }
}