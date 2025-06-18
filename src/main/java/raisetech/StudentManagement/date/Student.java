package raisetech.StudentManagement.date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

 private String id;
 private String fullname;
 private String furigana;
 private String nickName;
 private String email;
 private String municipality;
 private int age;
 private String gender;
 private String remark;
 private boolean isDeleted;

}
