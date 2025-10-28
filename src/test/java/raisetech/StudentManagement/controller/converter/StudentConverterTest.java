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
  void 空のリストを渡すと空のリストが返ること() {
    List<Student> students = new ArrayList<>();
    List<StudentCourse> courses = new ArrayList<>();

    List<StudentDetail> result = converter.toDetails(students, courses);

    assertEquals(0, result.size());
  }

  @Test
  void 学生1人を渡すと学生1人が返ってくること() {
    Student student = new Student();
    student.setId(35);
    student.setName("テスト太郎");

    List<Student> students = new ArrayList<>();
    students.add(student);

    List<StudentCourse> courses = new ArrayList<>();

    List<StudentDetail> result = converter.toDetails(students, courses);

    assertEquals(1, result.size());
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
  void 学生2人にコースを振り分けて変換できること() {
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
  }

  @Test
  void コースがnullでも空リストが設定されること() {
    Student student = new Student();
    student.setId(40);
    student.setName("テストさん");

    List<Student> students = new ArrayList<>();
    students.add(student);

    List<StudentCourse> courses = null;

    List<StudentDetail> result = converter.toDetails(students, courses);

    assertEquals(1, result.size());
    assertNotNull(result.get(0).getStudentCourse());
    assertEquals(0, result.get(0).getStudentCourse().size());
  }
}