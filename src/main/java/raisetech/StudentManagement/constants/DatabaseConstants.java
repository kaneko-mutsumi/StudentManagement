package raisetech.StudentManagement.constants;

/**
 * データベース操作用の定数クラス
 *
 * <p>マジックナンバーを定数化して可読性を向上させます。</p>
 */
public final class DatabaseConstants {

  // インスタンス化防止
  private DatabaseConstants() {
    throw new AssertionError("定数クラスはインスタンス化できません");
  }

  /**
   * データベース更新結果の期待値
   */
  public static final int EXPECTED_UPDATE_COUNT = 1;

  /**
   * 更新失敗を示す値
   */
  public static final int UPDATE_FAILED = 0;

  /**
   * 論理削除フラグ: 削除済み
   */
  public static final int DELETED_FLAG_TRUE = 1;

  /**
   * 論理削除フラグ: 有効
   */
  public static final int DELETED_FLAG_FALSE = 0;
}