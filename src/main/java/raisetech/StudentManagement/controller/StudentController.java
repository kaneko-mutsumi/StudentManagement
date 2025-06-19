package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.service.StudentService;

@Controller
public class StudentController {

  private final StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  @GetMapping("/studentList")
  public String showStudentList(Model model) {
    List<StudentDetail> studentList = service.searchStudentList();
    model.addAttribute("studentList", studentList);
    return "studentList";
  }

  @GetMapping("/courseList")
  public String showCourseList(Model model) {
    List<StudentsCourses> coursesList = service.searchStudentsCourseList();
    model.addAttribute("courseList", coursesList);
    return "courseList";
  }

  @GetMapping("/newStudent")
  public String showNewStudentForm(Model model) {
    model.addAttribute("studentForm", new StudentForm());
    addCourseOptionsToModel(model);
    return "registerStudent";
  }

  @PostMapping("/registerStudent")
  public String registerStudent(
      @Valid @ModelAttribute("studentForm") StudentForm studentForm,
      BindingResult result,
      Model model) {

    if (result.hasErrors()) {
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
    return "redirect:/studentList";
  }

  private void addCourseOptionsToModel(Model model) {
    model.addAttribute("courseOptions",
        List.of("Java入門", "Spring実践", "Webアプリ開発"));
  }
}