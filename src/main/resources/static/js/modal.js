/**
 * モーダル機能JavaScript（modal.js）
 * 確認ダイアログなどのモーダル表示機能
 */

/**
 * 削除確認モーダルを表示する
 * @param {number} studentId - 削除対象の学生ID
 * @param {string} studentName - 削除対象の学生名
 */
function showDeleteModal(studentId, studentName) {
  const modal = document.getElementById('deleteModal');
  const message = document.getElementById('deleteMessage');
  const form = document.getElementById('deleteForm');

  if (!modal || !message || !form) {
    console.error('モーダル要素が見つかりません');
    return;
  }

  // メッセージを設定
  message.textContent = `「${studentName}」の情報を削除してもよろしいですか？`;

  // フォームのアクションを設定
  form.action = `/student/${studentId}/delete`;

  // モーダルを表示
  modal.style.display = 'block';

  // アクセシビリティ: フォーカスをモーダル内に移動
  modal.focus();
}

/**
 * 削除確認モーダルを非表示にする
 */
function hideDeleteModal() {
  const modal = document.getElementById('deleteModal');
  if (modal) {
    modal.style.display = 'none';
  }
}

/**
 * キャンセル確認モーダルを表示する
 */
function showCancelModal() {
  const modal = document.getElementById('cancelModal');
  if (modal) {
    modal.style.display = 'block';
    modal.focus();
  } else {
    console.error('キャンセルモーダル要素が見つかりません');
  }
}

/**
 * キャンセル確認モーダルを非表示にする
 */
function hideCancelModal() {
  const modal = document.getElementById('cancelModal');
  if (modal) {
    modal.style.display = 'none';
  }
}

/**
 * 汎用確認ダイアログ
 * @param {string} title - ダイアログのタイトル
 * @param {string} message - 確認メッセージ
 * @param {Function} onConfirm - 確認時のコールバック
 * @param {Function} onCancel - キャンセル時のコールバック（省略可）
 */
function showConfirmDialog(title, message, onConfirm, onCancel) {
  // 動的にモーダルを作成
  const modal = document.createElement('div');
  modal.className = 'modal';
  modal.innerHTML = `
    <div class="modal-content">
      <h3 class="modal-title">${title}</h3>
      <p class="modal-text">${message}</p>
      <div class="modal-buttons">
        <button type="button" class="btn btn-cancel" onclick="hideConfirmDialog()">キャンセル</button>
        <button type="button" class="btn btn-confirm" onclick="confirmAction()">確認</button>
      </div>
    </div>
  `;

  // ボディに追加
  document.body.appendChild(modal);
  modal.style.display = 'block';

  // グローバル関数として一時的に定義
  window.hideConfirmDialog = function() {
    document.body.removeChild(modal);
    if (onCancel) onCancel();
  };

  window.confirmAction = function() {
    document.body.removeChild(modal);
    if (onConfirm) onConfirm();
  };
}

/**
 * フォームリセット確認
 */
function resetForm() {
  showConfirmDialog(
    'フォームリセット',
    '入力中の内容がすべてクリアされますが、よろしいですか？',
    function() {
      // 確認時の処理
      const form = document.querySelector('form');
      if (form) {
        form.reset();

        // 手動で特定フィールドもクリア
        const courseEndAt = document.getElementById('courseEndAt');
        if (courseEndAt) {
          courseEndAt.value = '';
        }

        // 成功メッセージ表示
        showNotification('フォームをリセットしました。', 'success');
      }
    }
  );
}

/**
 * 通知メッセージを表示
 * @param {string} message - 表示するメッセージ
 * @param {string} type - メッセージタイプ（success, error, info）
 */
function showNotification(message, type = 'info') {
  const notification = document.createElement('div');
  notification.className = `message ${type}-message`;
  notification.textContent = message;
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 2000;
    max-width: 300px;
    animation: slideInRight 0.3s ease;
  `;

  document.body.appendChild(notification);

  // 3秒後に自動削除
  setTimeout(function() {
    notification.style.opacity = '0';
    setTimeout(function() {
      if (notification.parentNode) {
        document.body.removeChild(notification);
      }
    }, 300);
  }, 3000);
}

/**
 * モーダル初期化
 * ページ読み込み時にイベントリスナーを設定
 */
function initModalFeatures() {
  document.addEventListener('DOMContentLoaded', function() {
    // モーダル外クリックで閉じる機能
    document.addEventListener('click', function(event) {
      const modals = document.querySelectorAll('.modal');
      modals.forEach(function(modal) {
        if (event.target === modal) {
          modal.style.display = 'none';
        }
      });
    });

    // ESCキーでモーダルを閉じる機能
    document.addEventListener('keydown', function(event) {
      if (event.key === 'Escape') {
        const visibleModals = document.querySelectorAll('.modal[style*="block"]');
        visibleModals.forEach(function(modal) {
          modal.style.display = 'none';
        });
      }
    });
  });
}

// 初期化実行
initModalFeatures();