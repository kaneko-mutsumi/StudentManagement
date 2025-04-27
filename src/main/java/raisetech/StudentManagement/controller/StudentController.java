package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

@Controller
public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/studentList")
  public String getStudent(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> courses = service.searchStudentsCourseList();
    model.addAttribute("studentList", converter.convertStudentDetails(students, courses));
    return "studentList";
  }

  @GetMapping("/courseList")
  public String getCourseList(Model model) {
    List<StudentsCourses> coursesList = service.searchStudentsCourseList();
    model.addAttribute("courseList", coursesList);
    return "courseList";
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    model.addAttribute("studentDetail", new StudentDetail());
    model.addAttribute("courseOptions", List.of("Java入門", "Spring実践", "Webアプリ開発"));
    return "registerStudent";
  }


  @PostMapping("/registerStudent")
  public String registerStudent(
      @Valid @ModelAttribute StudentDetail studentDetail,
      BindingResult result,
      Model model
  ) {
    if (result.hasErrors()) {
      model.addAttribute("courseOptions", List.of("Java入門", "Spring実践", "Webアプリ開発"));
      return "registerStudent";
    }

    // 登録処理
    service.insertStudent(studentDetail.getStudent());
    service.insertCourse(
        studentDetail.getCourse(),
        studentDetail.getStudent().getId()
    );

    return "redirect:/studentList";
  }

}
