package raisetech.StudentManagement.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  @MockBean
  private StudentConverter converter;

  // ========== ①正常系テスト ==========

  @Test
  void 学生一覧検索が実行できて空のリストが返ってくること() throws Exception {
    // 準備: 空のリストを返すように設定
    when(service.getStudents()).thenReturn(new ArrayList<>());
    when(service.getCourses()).thenReturn(new ArrayList<>());
    when(converter.toDetails(anyList(), anyList()))
        .thenReturn(new ArrayList<>());

    // 実行と検証
    mockMvc.perform(get("/api/students"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("success"))
        .andExpect(jsonPath("$.count").value(0))
        .andExpect(jsonPath("$.students").isArray())
        .andExpect(jsonPath("$.students").isEmpty());
  }

  // ========== ②異常系テスト(入力チェック) ==========

  /**
   * テスト: IDに数字以外を入れたら400エラーになるか
   *
   * 【仕様】
   * URLに「/api/students/abc」と入力 → IDが数字ではない
   * → 型変換エラー(MethodArgumentTypeMismatchException)が発生
   * → GlobalExceptionHandlerの型変換エラー専用ハンドラーが処理
   * → 400エラーを返す
   */
  @Test
  void 学生詳細取得でIDに数字以外を指定した場合_400エラーになること() throws Exception {
    // 実行と検証
    mockMvc.perform(get("/api/students/abc"))
        .andExpect(status().isBadRequest())  // 400エラー
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("入力形式が正しくありません"))
        .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
  }

  // ========== ③入力チェックテスト(JSON形式) ==========

  /**
   * テスト: POSTで必須項目(名前)が空だったら400エラーになるか
   *
   * 【説明】
   * JSONの「name」が空文字列 → @NotBlankが引っかかる → 400エラーを返す
   */
  @Test
  void 学生登録で名前が空の場合_400エラーになること() throws Exception {
    // 準備: 名前が空のJSON
    String json = """
        {
          "name": "",
          "kanaName": "テストタロウ",
          "email": "test@example.com",
          "area": "テスト市",
          "age": 20,
          "sex": "男性",
          "courseName": "Java入門",
          "courseStartAt": "2025-01-01",
          "courseEndAt": "2025-03-31"
        }
        """;

    // 実行と検証
    mockMvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())  // 400エラー
        .andExpect(jsonPath("$.status").value("error"))  // エラーステータス確認
        .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
        .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("name")));  // nameフィールドのエラー
  }

  /**
   * テスト: POSTで年齢が16歳未満だったら400エラーになるか
   *
   * 【説明】
   * 年齢が15歳 → @Min(value = 16)が引っかかる → 400エラーを返す
   */
  @Test
  void 学生登録で年齢が15歳の場合_400エラーになること() throws Exception {
    // 準備: 年齢が15歳のJSON
    String json = """
        {
          "name": "テスト太郎",
          "kanaName": "テストタロウ",
          "email": "test@example.com",
          "area": "テスト市",
          "age": 15,
          "sex": "男性",
          "courseName": "Java入門",
          "courseStartAt": "2025-01-01",
          "courseEndAt": "2025-03-31"
        }
        """;

    // 実行と検証
    mockMvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())  // 400エラー
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
        .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("age")));  // ageフィールドのエラー
  }

  /**
   * テスト: POSTでメールアドレスが不正だったら400エラーになるか
   *
   * 【説明】
   * メールが「test」(形式が違う) → @Emailと@Patternが引っかかる → 400エラーを返す
   */
  @Test
  void 学生登録でメールアドレスが不正な場合_400エラーになること() throws Exception {
    // 準備: メールアドレスが不正なJSON
    String json = """
        {
          "name": "テスト太郎",
          "kanaName": "テストタロウ",
          "email": "test",
          "area": "テスト市",
          "age": 20,
          "sex": "男性",
          "courseName": "Java入門",
          "courseStartAt": "2025-01-01",
          "courseEndAt": "2025-03-31"
        }
        """;

    // 実行と検証
    mockMvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())  // 400エラー
        .andExpect(jsonPath("$.status").value("error"))
        .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
        .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("email")));  // emailフィールドのエラー
  }
}