package raisetech.StudentManagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

/**
 * 学生管理コントローラー
 */
@Controller
public class StudentController {

  @Autowired
  private StudentService service;

  @GetMapping("/studentList")
  public String studentList(Model model) {
    System.out.println("=== 学生一覧画面アクセス ===");
    model.addAttribute("studentList", service.getStudentList());
    return "studentList";
  }

  @GetMapping("/courseList")
  public String courseList(Model model) {
    model.addAttribute("courseList", service.getCourseList());
    return "courseList";
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    model.addAttribute("studentForm", new StudentForm());
    return "register";
  }

  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentForm form) {
    service.registerStudent(form);
    return "redirect:/studentList";
  }

  @GetMapping("/student/{id}/edit")
  public String editStudent(@PathVariable int id, Model model) {
    System.out.println("=== 編集画面コントローラー呼び出し: ID=" + id + " ===");
    try {
      StudentForm form = service.getStudentForm(id);
      System.out.println("=== サービスから戻った結果 ===");
      System.out.println("フォーム名前: " + form.getName());
      System.out.println("フォーム開始日: " + form.getCourseStartAt());
      System.out.println("フォーム終了日: " + form.getCourseEndAt());

      // 追加デバッグ
      if (form.getCourseStartAt() != null) {
        System.out.println("開始日の型: " + form.getCourseStartAt().getClass().getName());
      }
      if (form.getCourseEndAt() != null) {
        System.out.println("終了日の型: " + form.getCourseEndAt().getClass().getName());
      }

      model.addAttribute("studentForm", form);
      System.out.println("=== edit.htmlにリダイレクト ===");
      return "edit";
    } catch (Exception e) {
      System.out.println("=== エラー発生 ===");
      e.printStackTrace();
      return "error";
    }
  }

  @PostMapping("/student/update")
  public String updateStudent(@ModelAttribute StudentForm form) {
    service.updateStudent(form);
    return "redirect:/studentList";
  }

  @PostMapping("/student/cancel")
  public String cancelStudents(@RequestParam(value = "ids", required = false) List<Integer> ids) {
    if (ids != null) {
      service.cancelStudents(ids);
    }
    return "redirect:/studentList";
  }
}