package raisetech.StudentManagement.controller;

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

  @Autowired
  private StudentService service;

  @Autowired
  private StudentConverter converter;

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
    return "studentList";
  }

  /**
   * コース一覧表示
   */
  @GetMapping("/courseList")
  public String showCourseList(Model model) {
    logger.info("コース一覧画面表示");

    List<StudentCourse> courses = service.getCourses();
    model.addAttribute("courseList", courses);
    return "courseList";
  }

  /**
   * 新規登録画面表示
   */
  @GetMapping("/newStudent")
  public String showNewStudentForm(Model model) {
    logger.info("新規登録画面表示");
    model.addAttribute("studentForm", new StudentForm());
    return "newStudent";
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
      return "redirect:/studentList";
    } catch (Exception e) {
      logger.error("学生登録エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました");
      return "redirect:/newStudent";
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
      return "editStudent";
    } catch (Exception e) {
      logger.error("学生詳細取得エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "学生が見つかりません");
      return "redirect:/studentList";
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
      return "editStudent";
    }

    try {
      service.updateStudent(form);
      redirectAttributes.addFlashAttribute("successMessage", "学生情報を更新しました");
      return "redirect:/studentList";
    } catch (Exception e) {
      logger.error("学生更新エラー", e);
      redirectAttributes.addFlashAttribute("errorMessage", "更新に失敗しました");
      return "redirect:/studentList";
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

    return "redirect:/studentList";
  }
}