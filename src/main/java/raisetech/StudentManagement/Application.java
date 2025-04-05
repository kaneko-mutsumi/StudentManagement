package raisetech.StudentManagement;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  private Map<String, String> students = new HashMap<>();

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  // 🔸 生徒を登録（または上書き）
  @PostMapping("/studentInfo")
  public String registerStudent(String name, String age) {
    students.put(name, age);
    return "登録しました：" + name + "（" + age + "歳）";
  }

  // Mapに保存されている全ての生徒の名前と年齢を一覧で返します
  @GetMapping("/students")
  public String getAllStudents() {
    if (students.isEmpty()) {
      return "登録されている生徒はいません。";
    }
    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String> entry : students.entrySet()) {
      result.append(entry.getKey()).append("：").append(entry.getValue()).append("歳\n");

    }
    return result.toString();
  }

  // 🔸 特定の生徒の情報を表示
  @GetMapping("/student")
  public String getStudent(String name) {
    if (students.containsKey(name)) {
      return "生徒：" + name + " さんの年齢は " + students.get(name) + " 歳です。";
    } else {
      return "指定された生徒「" + name + "」は登録されていません。";
    }
  }

  // 🔸 生徒の年齢を更新（発展）
  @PostMapping("/student/update")
  public String updateAge(String name, String age) {
    if (students.containsKey(name)) {
      students.put(name, age);
      return name + "の年齢を" + age + "歳に更新しました。";
    } else {
      return "指定された生徒「" + name + "」は登録されていません。";

    }
  }

  @PostMapping("/student/delete")
  public String deleteStudent(String name) {
    if (students.containsKey(name)) {
      students.remove(name);
      return name + "を削除しました。";
    } else {
      return "指定された生徒「" + name + "」は登録されていません。";
    }

  }

}

