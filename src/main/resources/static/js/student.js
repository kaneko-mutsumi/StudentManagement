/**
 * 学生機能JavaScript（student.js）
 * 学生関連のページで使用される機能
 */

/**
 * コース終了日を自動計算する
 * コース名と開始日から適切な終了日を設定
 */
function calculateEndDate() {
  const courseName = document.getElementById('courseName');
  const courseStartAt = document.getElementById('courseStartAt');
  const courseEndAt = document.getElementById('courseEndAt');

  if (!courseName || !courseStartAt || !courseEndAt) {
    console.warn('必要な要素が見つかりません');
    return;
  }

  const courseNameValue = courseName.value;
  const startDateValue = courseStartAt.value;

  // どちらかが未入力の場合は処理しない
  if (!courseNameValue || !startDateValue) {
    return;
  }

  // コース別の期間設定
  const courseDurations = {
    'Java入門': 3,
    'Spring実践': 6,
    'Webアプリ開発': 8
  };

  const months = courseDurations[courseNameValue] || 6; // デフォルト6ヶ月

  // 終了日を計算
  const startDate = new Date(startDateValue);
  const endDate = new Date(startDate);
  endDate.setMonth(endDate.getMonth() + months);

  // フォームに設定
  courseEndAt.value = endDate.toISOString().split('T')[0];

  // 計算完了のフィードバック
  console.log(`終了日計算完了: ${courseNameValue} (${months}ヶ月) → ${courseEndAt.value}`);
}

/**
 * 学生情報のバリデーション
 * フォーム送信前の入力値チェック
 */
function validateStudentForm() {
  const form = document.querySelector('form');
  if (!form) return;

  form.addEventListener('submit', function(event) {
    let isValid = true;
    const errors = [];

    // 名前の検証
    const name = document.querySelector('input[name="name"]');
    if (name && name.value.trim().length < 2) {
      errors.push('名前は2文字以上で入力してください');
      isValid = false;
    }

    // 年齢の検証
    const age = document.querySelector('input[name="age"]');
    if (age) {
      const ageValue = parseInt(age.value);
      if (isNaN(ageValue) || ageValue < 16 || ageValue > 100) {
        errors.push('年齢は16歳以上100歳以下で入力してください');
        isValid = false;
      }
    }

    // メールアドレスの検証
    const email = document.querySelector('input[name="email"]');
    if (email) {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(email.value)) {
        errors.push('正しいメールアドレスの形式で入力してください');
        isValid = false;
      }
    }

    // コース開始日と終了日の検証
    const startDate = document.querySelector('input[name="courseStartAt"]');
    const endDate = document.querySelector('input[name="courseEndAt"]');
    if (startDate && endDate) {
      const start = new Date(startDate.value);
      const end = new Date(endDate.value);
      if (start >= end) {
        errors.push('終了日は開始日より後の日付を入力してください');
        isValid = false;
      }
    }

    // エラーがある場合は送信を中止
    if (!isValid) {
      event.preventDefault();
      showNotification('入力エラー:\n' + errors.join('\n'), 'error');
    }
  });
}

/**
 * 学生一覧でのフィルタリング機能
 * 名前やメールアドレスで学生を検索
 */
function initStudentFilter() {
  // 検索ボックスを動的に追加
  const container = document.querySelector('.container');
  const header = document.querySelector('.header');

  if (container && header) {
    const filterContainer = document.createElement('div');
    filterContainer.innerHTML = `
      <div style="margin-bottom: 20px; padding: 10px; background: #f8f9fa; border-radius: 4px;">
        <label for="studentFilter" style="font-weight: 500; margin-right: 10px;">学生検索:</label>
        <input type="text" id="studentFilter" placeholder="名前、メール、地域で検索..."
               style="padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 300px;">
        <button type="button" onclick="clearFilter()" class="btn btn-secondary"
                style="margin-left: 10px;">クリア</button>
      </div>
    `;

    header.insertAdjacentElement('afterend', filterContainer);

    // 検索機能の実装
    const filterInput = document.getElementById('studentFilter');
    if (filterInput) {
      filterInput.addEventListener('input', function() {
        filterStudents(this.value);
      });
    }
  }
}

/**
 * 学生一覧のフィルタリング実行
 * @param {string} filterText - 検索テキスト
 */
function filterStudents(filterText) {
  const rows = document.querySelectorAll('.student-table tbody tr');
  const searchText = filterText.toLowerCase();

  rows.forEach(function(row) {
    const cells = row.querySelectorAll('td');
    let found = false;

    // 名前、メール、地域の列をチェック（2, 5, 6列目）
    const searchColumns = [1, 4, 5]; // 名前、メール、地域
    searchColumns.forEach(function(columnIndex) {
      if (cells[columnIndex]) {
        const cellText = cells[columnIndex].textContent.toLowerCase();
        if (cellText.includes(searchText)) {
          found = true;
        }
      }
    });

    // 表示/非表示を切り替え
    row.style.display = found ? '' : 'none';
  });

  // 検索結果の件数表示
  updateFilterResults(filterText);
}

/**
 * フィルター結果の件数を表示
 * @param {string} filterText - 検索テキスト
 */
function updateFilterResults(filterText) {
  const visibleRows = document.querySelectorAll('.student-table tbody tr[style=""], .student-table tbody tr:not([style])');
  const totalRows = document.querySelectorAll('.student-table tbody tr');

  let resultElement = document.getElementById('filterResult');
  if (!resultElement) {
    resultElement = document.createElement('div');
    resultElement.id = 'filterResult';
    resultElement.style.cssText = 'margin: 10px 0; font-size: 14px; color: #666;';

    const filterContainer = document.querySelector('#studentFilter').parentNode.parentNode;
    filterContainer.appendChild(resultElement);
  }

  if (filterText.trim()) {
    resultElement.textContent = `検索結果: ${visibleRows.length}件 / 全${totalRows.length}件`;
  } else {
    resultElement.textContent = `全${totalRows.length}件`;
  }
}

/**
 * フィルターをクリア
 */
function clearFilter() {
  const filterInput = document.getElementById('studentFilter');
  if (filterInput) {
    filterInput.value = '';
    filterStudents('');
  }
}

/**
 * 学生情報の一括選択機能
 * 複数の学生を選択して一括操作
 */
function initBulkActions() {
  const table = document.querySelector('.student-table');
  if (!table) return;

  // ヘッダーにチェックボックスを追加
  const headerRow = table.querySelector('thead tr');
  if (headerRow) {
    const checkboxHeader = document.createElement('th');
    checkboxHeader.innerHTML = '<input type="checkbox" id="selectAll" onchange="toggleSelectAll()">';
    headerRow.insertBefore(checkboxHeader, headerRow.firstChild);
  }

  // 各行にチェックボックスを追加
  const bodyRows = table.querySelectorAll('tbody tr');
  bodyRows.forEach(function(row, index) {
    const studentId = row.querySelector('td').textContent;
    const checkboxCell = document.createElement('td');
    checkboxCell.innerHTML = `<input type="checkbox" class="student-checkbox" value="${studentId}">`;
    row.insertBefore(checkboxCell, row.firstChild);
  });

  // 一括操作ボタンを追加
  addBulkActionButtons();
}

/**
 * 全選択/全解除の切り替え
 */
function toggleSelectAll() {
  const selectAll = document.getElementById('selectAll');
  const checkboxes = document.querySelectorAll('.student-checkbox');

  checkboxes.forEach(function(checkbox) {
    checkbox.checked = selectAll.checked;
  });

  updateBulkActionButtons();
}

/**
 * 一括操作ボタンの追加
 */
function addBulkActionButtons() {
  const buttonGroup = document.querySelector('.button-group');
  if (buttonGroup) {
    const bulkActions = document.createElement('div');
    bulkActions.id = 'bulkActions';
    bulkActions.style.cssText = 'margin-top: 10px; display: none;';
    bulkActions.innerHTML = `
      <button type="button" class="btn btn-danger" onclick="bulkDelete()">選択した学生を削除</button>
      <button type="button" class="btn btn-secondary" onclick="clearSelection()">選択解除</button>
    `;

    buttonGroup.appendChild(bulkActions);

    // チェックボックスの変更を監視
    document.addEventListener('change', function(event) {
      if (event.target.classList.contains('student-checkbox')) {
        updateBulkActionButtons();
      }
    });
  }
}

/**
 * 一括操作ボタンの表示/非表示更新
 */
function updateBulkActionButtons() {
  const checkedBoxes = document.querySelectorAll('.student-checkbox:checked');
  const bulkActions = document.getElementById('bulkActions');

  if (bulkActions) {
    bulkActions.style.display = checkedBoxes.length > 0 ? 'block' : 'none';
  }
}

/**
 * 選択された学生の一括削除
 */
function bulkDelete() {
  const checkedBoxes = document.querySelectorAll('.student-checkbox:checked');
  const studentIds = Array.from(checkedBoxes).map(cb => cb.value);

  if (studentIds.length === 0) {
    showNotification('削除する学生を選択してください', 'warning');
    return;
  }

  showConfirmDialog(
    '一括削除確認',
    `選択した${studentIds.length}件の学生情報を削除してもよろしいですか？`,
    function() {
      // 実際の削除処理（本来はサーバーサイドで一括削除APIを作成）
      studentIds.forEach(function(studentId) {
        // 個別に削除リクエストを送信
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/student/${studentId}/delete`;
        document.body.appendChild(form);
        form.submit();
      });
    }
  );
}

/**
 * 選択解除
 */
function clearSelection() {
  const checkboxes = document.querySelectorAll('.student-checkbox, #selectAll');
  checkboxes.forEach(function(checkbox) {
    checkbox.checked = false;
  });
  updateBulkActionButtons();
}

/**
 * 学生機能の初期化
 */
function initStudentFeatures() {
  document.addEventListener('DOMContentLoaded', function() {
    // コース終了日計算のイベント設定
    const courseName = document.getElementById('courseName');
    const courseStartAt = document.getElementById('courseStartAt');

    if (courseName) {
      courseName.addEventListener('change', calculateEndDate);
    }

    if (courseStartAt) {
      courseStartAt.addEventListener('change', calculateEndDate);
    }

    // バリデーション設定
    validateStudentForm();

    // 学生一覧のページでのみフィルター機能を有効化
    if (document.querySelector('.student-table')) {
      initStudentFilter();
      // 一括操作機能（必要に応じて有効化）
      // initBulkActions();
    }
  });
}

// 学生機能の初期化実行
initStudentFeatures();