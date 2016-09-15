package tools.graphic.sql.gui

/**
 * 接続操作の結果
 */
class ConnectResult {
    /**
     * 接続結果
     */
    ResultCode resultCode
    /**
     * 接続失敗した場合、エラーメッセージ。
     */
    String errorMessage
    /**
     * 接続成功した場合、取得したテーブル一覧。
     */
    List<String> tables

    @Override
    public String toString() {
        return "ConnectResult{resultCode=${resultCode}, errorMessage='${errorMessage}', tables=${tables}}";
    }
}
