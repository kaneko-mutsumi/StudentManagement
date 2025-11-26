# 改善タスクリスト

## 📅 課題44完了後の改善予定

---

## 🔴 優先度：高（次回PR）

### 1. コメントの修正

**対象ファイル**:
- `StudentService.java` - 全メソッドのJavadoc
- `StudentRepository.java` - インターフェースコメント
- `StudentRepository.xml` - SQLコメント
- `StudentController.java` - APIドキュメント

**理由**: コードと実態が異なる箇所があり、将来の保守性に影響

**例**:
```java
// ❌ 現在
/**
 * 学生を削除
 */

// ✅ 修正後
/**
 * 学生を論理削除（deleted=1に設定）
 * 物理削除は行わず、データは保持される
 */
```

---

### 2. コース一覧に受講生名を表示

**変更内容**:
- `StudentRepository.xml`: `getAllCourses`にJOIN追加
- `StudentCourse.java`: `studentName`フィールド追加
- `courseList.html`: 受講生名列追加

**理由**: 受講生IDだけでは誰のコースか分かりにくい

**Before**:
```
コースID | 受講生ID | コース名
1       | 5       | Java入門
```

**After**:
```
コースID | 受講生名 | コース名 | 申込状況
1       | 山田太郎 | Java入門 | 受講中
```

---

### 3. マジックナンバーの定数化

**対象**: `StudentService.java`の`!= 1`等

**新規作成**: `DatabaseConstants.java`
```java
public static final int EXPECTED_SINGLE_UPDATE_COUNT = 1;
```

**理由**: 可読性と保守性の向上

---

### 4. マジックストリングの定数化

**対象**: `StudentViewController.java`の`"redirect:/studentList"`等

**新規作成**: `ViewNames.java`
```java
public static final String REDIRECT_STUDENT_LIST = "redirect:/studentList";
```

**理由**: タイポ防止と保守性向上

---

### 5. エラーハンドリングの改善

**変更内容**:
- `StudentViewController.java`のcatch句を具体的な例外に変更
    - `ResourceNotFoundException`
    - `DataIntegrityViolationException`

**理由**: エラー原因の特定が容易になる

---

### 6. DTO実装

**新規作成**: `StudentListResponse.java`

**変更**: `StudentController.java`のレスポンスをDTOに変更

**理由**: API設計の明確化、将来の拡張性向上

---

## 🟡 優先度：中

### 7. 「キャンセル」ステータスの追加

**ビジネス要件**:
- 「仮申込 or 本申込までは行ったが、受講開始前に取り消した」ケースへの対応
- 本人都合・定員オーバー等のキャンセル理由を記録
- 返金処理の根拠、キャンセル率の分析に活用

**変更内容**:
1. **DB変更**:
```sql
   ALTER TABLE enrollment_status 
   MODIFY COLUMN status ENUM('仮申込', '本申込', '受講中', '受講終了', 'キャンセル') 
   NOT NULL DEFAULT '仮申込';
```

2. **Javaコード**:
    - `StudentForm.java`: バリデーションパターンに追加
    - `EnrollmentStatusEnum.java`: 列挙型に`CANCELLED`追加（将来）

3. **HTML**:
    - `newStudent.html`, `edit.html`: 選択肢に追加
    - `studentList.html`: キャンセルステータスの表示

4. **テスト**:
    - `data.sql`: テストデータ追加
    - Repository/Serviceテストに追加

**注意点**:
- キャンセル後の復帰（受講再開）のビジネスルールを確認
- キャンセル日時の記録が必要か検討

---

### 8. SOLID原則の強化

#### 8-1. SRP（単一責任の原則）

**新規作成**: `StudentSearchService.java`

**変更**: `StudentService.java`から検索機能を分離

**理由**: 検索機能を独立させ、責任を明確化

---

#### 8-2. OCP（開放閉鎖の原則）

**新規作成**:
- `SearchCriteria.java`: 検索条件のカプセル化
- `EnrollmentStatusEnum.java`: ステータスの列挙型

**理由**: 新しい検索条件やステータス追加時に既存コードを変更しない

---

#### 8-3. ISP（インターフェース分離の原則）

**変更**: `StudentRepository`を3つに分割
- `StudentRepository.java`
- `CourseRepository.java`
- `EnrollmentStatusRepository.java`

**理由**: クライアントが不要なメソッドに依存しない

---

#### 8-4. DIP（依存性逆転の原則）

**新規作成**:
- `IStudentService.java`: Serviceのインターフェース
- `Converter.java`: Converterのインターフェース

**理由**: 上位モジュールが抽象に依存する

---

## 🟢 優先度：低（将来的に対応）

### 9. ステータス履歴の記録

**ビジネス要件**:
- 監査証跡（いつ誰が変更したか）
- トラブル時の調査
- 分析レポート（平均受講期間など）

**変更内容**:
1. **新規テーブル**: `enrollment_status_history`
```sql
   CREATE TABLE enrollment_status_history (
       id INT AUTO_INCREMENT PRIMARY KEY,
       course_id INT NOT NULL,
       status ENUM('仮申込', '本申込', '受講中', '受講終了', 'キャンセル') NOT NULL,
       changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
       changed_by VARCHAR(100),
       note TEXT,
       FOREIGN KEY (course_id) REFERENCES students_courses(id) ON DELETE CASCADE
   );
```

2. **Javaコード**:
    - `EnrollmentStatusHistory.java`: 新規Entity
    - Repository: 履歴CRUD追加
    - Service: ステータス変更時に履歴記録

3. **HTML**:
    - 編集画面に履歴表示セクション追加

4. **注意点**:
    - 将来のSpring Security導入時に`changed_by`を実装
    - 大量データ対応（インデックス設計）

---

### 10.  ページング機能

**変更内容**:
- `getStudentsPaged(offset, limit)` メソッド追加
- `countStudents()` メソッド追加
- REST APIに`page`/`size`パラメータ追加

**理由**: 大量データ対応（100万件以上）

最終更新: 2025-11-25
作成者: [kaneko mutumi]