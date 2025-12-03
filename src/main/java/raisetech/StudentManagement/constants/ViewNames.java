package raisetech.StudentManagement.constants;

/**
 * ビュー名とリダイレクト先URLの定数クラス
 *
 * <p>マジックストリングを定数化してタイポを防ぎます。</p>
 */
public final class ViewNames {

  // インスタンス化防止
  private ViewNames() {
    throw new AssertionError("定数クラスはインスタンス化できません");
  }

  // ビュー名
  public static final String STUDENT_LIST = "studentList";
  public static final String COURSE_LIST = "courseList";
  public static final String NEW_STUDENT = "newStudent";
  public static final String EDIT_STUDENT = "editStudent";

  // リダイレクト先
  public static final String REDIRECT_STUDENT_LIST = "redirect:/studentList";
  public static final String REDIRECT_NEW_STUDENT = "redirect:/newStudent";
}