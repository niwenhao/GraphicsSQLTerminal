package tools.graphic.sql.gui

/**
 * SELECTの実行結果（データが取得されるたびに連携する。）
 */
class SelectResult {
    /**
     * 実行結果
     */
    ResultCode statusCode
    /**
     * エラーになる場合、エラーメッセージ
     */
    String errorMessage

    /**
     * 項目定義が取得された場合、項目定義一覧
     */
    List<FieldMeta> fieldMetaList
    /**
     * 項目値が取得された場合、データ一覧
     */
    List<String> fieldList


    @Override
    public String toString() {
        return "SelectResult{" +
                "statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", fieldMetaList=" + fieldMetaList +
                ", fieldList=" + fieldList +
                '}';
    }
}
