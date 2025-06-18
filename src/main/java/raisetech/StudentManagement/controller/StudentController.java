package raisetech.StudentManagement.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import raisetech.StudentManagement.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
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
  public String showStudentList(Model model) {
    List<StudentDetail> studentList = service.searchStudentList();
    model.addAttribute("studentList", studentList);

    model.addAttribute("studentForm", new StudentForm());
    addCourseOptionsToModel(model);

    return "studentList";
  }

  @GetMapping("/student/{id}")
  public String showStudentDetail(@PathVariable int id, Model model) {
    // 指定されたIDの受講生詳細情報を取得
    StudentDetail studentDetail = service.getStudentDetailById(id);

    // 受講生が見つからない場合はエラーページを表示
    if (studentDetail == null) {
      model.addAttribute("errorMessage", "指定された受講生は見つかりませんでした。");
      return "error";
    }

    // 画面に渡すデータを設定
    model.addAttribute("studentDetail", studentDetail);

    // studentDetail.htmlを表示
    return "studentDetail";
  }

  // 編集画面を表示するメソッド
  @GetMapping("/student/{id}/edit")
  public String showEditForm(@PathVariable int id, Model model) {
    System.out.println("=== 編集画面表示開始 ===");
    System.out.println("学生ID: " + id);

    // 指定されたIDの受講生詳細情報を取得
    StudentDetail studentDetail = service.getStudentDetailById(id);

    // 受講生が見つからない場合はエラーページを表示
    if (studentDetail == null) {
      System.out.println("❌ 学生が見つかりません: ID = " + id);
      model.addAttribute("errorMessage", "指定された受講生は見つかりませんでした。");
      return "error";
    }

    // 🔍 データベースから取得したデータを確認
    System.out.println("📄 取得した学生情報: " + studentDetail.getStudent().getName());
    if (studentDetail.getStudentsCourse() != null) {
      StudentsCourses course = studentDetail.getStudentsCourse();
      System.out.println("📄 取得したコース情報:");
      System.out.println("   コースID: " + course.getId());
      System.out.println("   コース名: " + course.getCourseName());
      System.out.println("   開始日: " + course.getCourseStartAt());
      System.out.println("   終了日: " + course.getCourseEndAt());
    } else {
      System.out.println("📄 コース情報: なし");
    }

    // 取得したデータをフォーム用に変換
    StudentForm studentForm = StudentForm.fromStudentDetail(studentDetail);

    // 🔍 変換後のフォームデータを確認
    System.out.println("🔄 変換後のフォームデータ:");
    System.out.println("   学生ID: " + studentForm.getId());
    System.out.println("   コースID: " + studentForm.getCourseId());
    System.out.println("   コース名: " + studentForm.getCourseName());
    System.out.println("   開始日: " + studentForm.getCourseStartAt());
    System.out.println("   終了日: " + studentForm.getCourseEndAt());

    // 画面に渡すデータを設定
    model.addAttribute("studentForm", studentForm);
    addCourseOptionsToModel(model);

    System.out.println("=== 編集画面表示完了 ===");

    // updateStudent.htmlを表示
    return "updateStudent";
  }

  // 更新処理を行うメソッド
  @PostMapping("/student/update")
  public String updateStudent(
      @Valid @ModelAttribute("studentForm") StudentForm studentForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes) {

    // ユーザーが終了日を空にした場合、開始日とコース名から自動計算
    if (studentForm.getCourseEndAt() == null &&
        studentForm.getCourseStartAt() != null &&
        studentForm.getCourseName() != null) {

      // 自動計算して設定
      studentForm.autoSetEndDate();

      // ユーザーに自動補完されたことを通知（ログ出力）
      System.out.println("💡 終了日が自動補完されました: " + studentForm.getCourseEndAt());
    }

    // バリデーションエラーがある場合は更新画面を再表示
    if (result.hasErrors()) {
      addCourseOptionsToModel(model);
      return "updateStudent";
    }

    // フォームデータを詳細情報に変換
    StudentDetail studentDetail = studentForm.toStudentDetail();

    // データベースの情報を更新
    service.updateStudent(studentDetail);

    // 成功メッセージを設定
    redirectAttributes.addFlashAttribute("successMessage", "受講生情報を更新しました。");

    // 更新した受講生の詳細画面にリダイレクト
    return "redirect:/student/" + studentForm.getId();
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

    // ユーザーが終了日を空にした場合、開始日とコース名から自動計算
    if (studentForm.getCourseEndAt() == null &&
        studentForm.getCourseStartAt() != null &&
        studentForm.getCourseName() != null) {

      // 自動計算して設定
      studentForm.autoSetEndDate();

      // ユーザーに自動補完されたことを通知（ログ出力）
      System.out.println(
          "💡 [新規登録] 終了日が自動補完されました: " + studentForm.getCourseEndAt());
    }

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
=======




}
