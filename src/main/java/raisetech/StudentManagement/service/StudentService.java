package raisetech.StudentManagement.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 学生サービス
 */
@Service
public class StudentService {

  @Autowired
  private StudentRepository repository;

  public List<StudentDetail> getStudentList() {
    List<Student> students = repository.getActiveStudents();
    return students.stream().map(this::makeStudentDetail).toList();
  }

  public List<StudentsCourses> getCourseList() {
    System.out.println("=== コース一覧取得開始 ===");
    List<StudentsCourses> courses = repository.getAllCourses();
    System.out.println("取得したコース数: " + courses.size());

    for (StudentsCourses course : courses) {
      System.out.println("コースID: " + course.getId() +
          ", 受講生ID: " + course.getStudentId() +
          ", コース名: " + course.getCourseName());
    }
    System.out.println("=== コース一覧取得完了 ===");
    return courses;
  }

  public StudentForm getStudentForm(int id) {
    Student student = repository.getStudent(id);
    StudentsCourses course = repository.getCourse(id);
    return makeForm(student, course);
  }

  public void registerStudent(StudentForm form) {
    Student student = makeStudent(form);
    repository.saveStudent(student);

    StudentsCourses course = makeCourse(form);
    course.setStudentId(student.getId());
    repository.saveCourse(course);
  }

  public void updateStudent(StudentForm form) {
    Student student = makeStudent(form);
    StudentsCourses course = makeCourse(form);

    repository.updateStudent(student);
    repository.updateCourse(course);
  }

  public void cancelStudents(List<Integer> ids) {
    ids.forEach(repository::cancelStudent);
  }

  private StudentDetail makeStudentDetail(Student student) {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentsCourse(repository.getCourse(student.getId()));
    return detail;
  }

  private StudentForm makeForm(Student student, StudentsCourses course) {
    StudentForm form = new StudentForm();
    form.setId(student.getId());
    form.setName(student.getName());
    form.setKanaName(student.getKanaName());
    form.setNickname(student.getNickname());
    form.setEmail(student.getEmail());
    form.setArea(student.getArea());
    form.setAge(student.getAge());
    form.setSex(student.getSex());
    form.setRemark(student.getRemark());

    if (course != null) {
      form.setCourseId(course.getId());
      form.setCourseName(course.getCourseName());
      form.setCourseStartAt(course.getCourseStartAt());
      form.setCourseEndAt(course.getCourseEndAt());
    }
    return form;
  }

  private Student makeStudent(StudentForm form) {
    Student student = new Student();
    student.setId(form.getId());
    student.setName(form.getName());
    student.setKanaName(form.getKanaName());
    student.setNickname(form.getNickname());
    student.setEmail(form.getEmail());
    student.setArea(form.getArea());
    student.setAge(form.getAge());
    student.setSex(form.getSex());
    student.setRemark(form.getRemark());
    return student;
  }

  private StudentsCourses makeCourse(StudentForm form) {
    StudentsCourses course = new StudentsCourses();
    course.setId(form.getCourseId());
    course.setStudentId(form.getId());
    course.setCourseName(form.getCourseName());
    course.setCourseStartAt(form.getCourseStartAt());
    course.setCourseEndAt(form.getCourseEndAt());
    return course;
  }
}