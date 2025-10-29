package raisetech.StudentManagement.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

class StudentConverterTest {

  private StudentConverter converter;

  @BeforeEach
  void setUp() {
    converter = new StudentConverter();
  }

  @Test
  @DisplayName("空のリストを渡すと空のリストが返ること")
  void convertEmptyLists() {
    List<Student> students = new ArrayList<>();
    List<StudentCourse> courses = new ArrayList<>();

    List<StudentDetail> result = converter.toDetails(students, courses);

    // サイズ検証
    assertEquals(0, result.size());
    // 空リストなので中身の検証は不要
  }

  @Test
  @DisplayName("学生1人を渡すと学生1人が返ってくること")
  void convertSingleStudent() {
    Student student = new Student();
    student.setId(35);
    student.setName("テスト太郎");

    List<Student> students = new ArrayList<>();
    students.add(student);

    List<StudentCourse> courses = new ArrayList<>();

    List<StudentDetail> result = converter.toDetails(students, courses);

    // サイズ検証
    assertEquals(1, result.size());

    // 中身の検証を追加
    StudentDetail detail = result.get(0);
    assertEquals(35, detail.getStudent().getId());
    assertEquals("テスト太郎", detail.getStudent().getName());
    assertEquals(0, detail.getStudentCourse().size());
  }

  @Test
  @DisplayName("学生1人にコース2個を紐づけて変換できること")
  void convertStudentWithTwoCourses() {
    Student student = new Student();
    student.setId(35);
    student.setName("テスト太郎");

    List<Student> students = new ArrayList<>();
    students.add(student);

    StudentCourse course1 = new StudentCourse();
    course1.setId(1);
    course1.setStudentId(35);
    course1.setCourseName("Javaコース");

    StudentCourse course2 = new StudentCourse();
    course2.setId(2);
    course2.setStudentId(35);
    course2.setCourseName("Springコース");

    List<StudentCourse> courses = new ArrayList<>();
    courses.add(course1);
    courses.add(course2);

    List<StudentDetail> result = converter.toDetails(students, courses);

    // サイズ検証
    assertEquals(1, result.size());
    assertEquals(2, result.get(0).getStudentCourse().size());

    // 中身の検証
    StudentDetail detail = result.get(0);
    assertEquals(35, detail.getStudent().getId());
    assertEquals("テスト太郎", detail.getStudent().getName());

    List<StudentCourse> resultCourses = detail.getStudentCourse();
    assertEquals("Javaコース", resultCourses.get(0).getCourseName());
    assertEquals("Springコース", resultCourses.get(1).getCourseName());
  }

  @Test
  @DisplayName("学生2人にコースを振り分けて変換できること")
  void convertTwoStudentsWithCourses() {
    Student student1 = new Student();
    student1.setId(36);
    student1.setName("テスト子");

    Student student2 = new Student();
    student2.setId(37);
    student2.setName("テスト代");

    List<Student> students = new ArrayList<>();
    students.add(student1);
    students.add(student2);

    StudentCourse course1 = new StudentCourse();
    course1.setId(1);
    course1.setStudentId(36);
    course1.setCourseName("Javaコース");

    StudentCourse course2 = new StudentCourse();
    course2.setId(2);
    course2.setStudentId(36);
    course2.setCourseName("Springコース");

    StudentCourse course3 = new StudentCourse();
    course3.setId(3);
    course3.setStudentId(37);
    course3.setCourseName("Web開発コース");

    List<StudentCourse> courses = new ArrayList<>();
    courses.add(course1);
    courses.add(course2);
    courses.add(course3);

    List<StudentDetail> result = converter.toDetails(students, courses);

    assertEquals(2, result.size());
    assertEquals(2, result.get(0).getStudentCourse().size());
    assertEquals(1, result.get(1).getStudentCourse().size());

    // 中身の検証を追加
    StudentDetail detail1 = result.get(0);
    assertEquals(36, detail1.getStudent().getId());
    assertEquals("テスト子", detail1.getStudent().getName());
    assertEquals("Javaコース", detail1.getStudentCourse().get(0).getCourseName());
    assertEquals("Springコース", detail1.getStudentCourse().get(1).getCourseName());

    StudentDetail detail2 = result.get(1);
    assertEquals(37, detail2.getStudent().getId());
    assertEquals("テスト代", detail2.getStudent().getName());
    assertEquals("Web開発コース", detail2.getStudentCourse().get(0).getCourseName());
  }

  @Test
  @DisplayName("コースがnullでも空リストが設定されること")
  void convertWithNullCourses() {
    Student student = new Student();
    student.setId(40);
    student.setName("テストさん");

    List<Student> students = new ArrayList<>();
    students.add(student);

    List<StudentCourse> courses = null;

    List<StudentDetail> result = converter.toDetails(students, courses);

    // サイズ検証
    assertEquals(1, result.size());

    // 中身の検証
    StudentDetail detail = result.get(0);
    assertEquals(40, detail.getStudent().getId());
    assertEquals("テストさん", detail.getStudent().getName());
    assertNotNull(detail.getStudentCourse());
    assertEquals(0, detail.getStudentCourse().size());
  }

  @Test
  @DisplayName("学生リストがnullでも空リストが返ること")
  void convertWithNullStudents() {
    List<Student> students = null;
    List<StudentCourse> courses = new ArrayList<>();

    List<StudentDetail> result = converter.toDetails(students, courses);

    // サイズ検証
    assertNotNull(result);
    assertEquals(0, result.size());
  }
}