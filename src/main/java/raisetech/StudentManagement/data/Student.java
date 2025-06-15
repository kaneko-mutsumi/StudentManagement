package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private int id;
  private String name;
  private String kanaName;
  private String nickname;
  private String email;
  private String area;
  private int age;
  private String sex;
  private String remark;
  private Boolean deleted;

  public Student() {
  }

  // アクティブな学生かどうかを判定するメソッド
  public boolean isActive() {
    return deleted == null || !deleted;
  }

  @Override
  public String toString() {
    return "Student{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", kanaName='" + kanaName + '\'' +
        ", email='" + email + '\'' +
        ", age=" + age +
        '}';
  }
}