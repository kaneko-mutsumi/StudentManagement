package raisetech.StudentManagement.controller.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.EnrollmentStatus;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.form.StudentForm;

/**
 * 学生とコース情報の変換処理
 */
@Component
public class StudentConverter {

  /**
   * 学生リストとコースリストからStudentDetailリストを生成
   */
  public List<StudentDetail> toDetails(List<Student> students, List<StudentCourse> courses) {
    if (students == null) {
      return Collections.emptyList();
    }

    Map<Integer, List<StudentCourse>> courseMap = courses != null
        ? courses.stream().collect(Collectors.groupingBy(StudentCourse::getStudentId))
        : Collections.emptyMap();

    return students.stream()
        .map(student -> createStudentDetail(student, courseMap.get(student.getId())))
        .collect(Collectors.toList());
  }

  /**
   * StudentDetailオブジェクトを生成
   */
  private StudentDetail createStudentDetail(Student student, List<StudentCourse> courses) {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentCourse(courses != null ? courses : new ArrayList<>());
    return detail;
  }


  /**
   * 学生情報とコース情報をフォーム形式に変換
   *
   * @param student 学生エンティティ
   * @param course  コースエンティティ（null可能）
   * @return StudentForm REST API応答用フォーム
   */
  public StudentForm toForm(Student student, StudentCourse course) {
    StudentForm form = new StudentForm();

    // 学生基本情報の設定
    form.setId(student.getId());
    form.setName(student.getName());
    form.setKanaName(student.getKanaName());
    form.setNickname(student.getNickname());
    form.setEmail(student.getEmail());
    form.setArea(student.getArea());
    form.setAge(student.getAge());
    form.setSex(student.getSex());
    form.setRemark(student.getRemark());

    // コース情報の設定（NULL安全処理）
    if (course != null) {
      form.setCourseId(course.getId());
      form.setCourseName(course.getCourseName());
      form.setCourseStartAt(course.getCourseStartAt());
      form.setCourseEndAt(course.getCourseEndAt());

      if (course.getEnrollmentStatus() != null) {
        form.setEnrollmentStatus(course.getEnrollmentStatus().getStatus());
      }
    }

    return form;
  }

  /**
   * フォーム情報を学生エンティティに変換
   *
   * @param form REST APIからの入力データ
   * @return Student データベース保存用エンティティ
   */
  public Student toStudent(StudentForm form) {
    Student student = new Student();

    // 新規登録時はIDがnull、更新時のみ設定
    if (form.getId() != null) {
      student.setId(form.getId());
    }

    // フォームからエンティティへデータをコピー
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

  /**
   * フォーム情報をコースエンティティに変換
   *
   * @param form REST APIからの入力データ
   * @return StudentCourse データベース保存用エンティティ
   */
  public StudentCourse toCourse(StudentForm form) {
    StudentCourse course = new StudentCourse();

    // 新規登録時はCourseIDがnull、更新時のみ設定
    if (form.getCourseId() != null) {
      course.setId(form.getCourseId());
    }

    // フォームからエンティティへデータをコピー
    course.setCourseName(form.getCourseName());
    course.setCourseStartAt(form.getCourseStartAt());
    course.setCourseEndAt(form.getCourseEndAt());

    if (form.getEnrollmentStatus() != null) {
      EnrollmentStatus status = new EnrollmentStatus();
      status.setStatus(form.getEnrollmentStatus());
      course.setEnrollmentStatus(status);
    }

    return course;
  }
}