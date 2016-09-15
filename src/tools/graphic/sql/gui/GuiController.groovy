package tools.graphic.sql.gui

/**
 * ユーザインタフェースに提供する機能インタフェース
 *
 * Created by nwh on 2016/01/07.
 */
interface GuiController {
    /**
     * データベース名称一覧
     *
     * @return  名称一覧
     */
    List<String> getDatabaseNames()

    /**
     * 無操作状態ログアウトタイムアウト
     *
     * @return      タイムアウト病数
     */
    Integer getTimeoutSecond()

    /**
     * SQL実行キャンセルタイムアウト
     *
     * @return      タイムアウト病数
     */
    Integer getTransTimeoutSecond()

    /**
     * DB接続
     *
     * @param dbName    データベース名
     * @param agwUid    プロキシーユーザID
     * @param agwPwd    プロキシーパスワード
     * @return          接続結果
     */
    ConnectResult connect(String dbName, String agwUid, String agwPwd)

    /**
     * DB接続切断
     *
     * @param dbName データベース名
     */
    ConnectResult disconnect(String dbName)

    /**
     * 事前定義SQLを取得する。
     * @param sp    範囲指定（共用、ユーザ）
     * @return  キー、SQLマップ
     */
    Map<String, String> getDefinedScript(SqlPrivilege sp)

    /**
     * SELECTを実行する。
     * @param dbName    対象データベース名
     * @param sql       実行SQL
     * @param dataProcessClosure    処理結果を受け取りClosure
     */
    void select(String dbName, String sql, Closure dataProcessClosure)

    /**
     * 任意SQLを実行する。
     * @param dbName    対象データベース名
     * @param sql       実行SQL
     * @param dataProcessClosure    処理結果を受け取りClosure
     */
    void execute(String dbName, String script, Closure resultProcessClosure)

    /**
     * SQL実行途中でキャンセルする。
     *
     * @param dbName        対象データベース名
     */
    void cancel(String dbName)

    /**
     * 事前定義SQL更新
     *
     * @param sp        範囲指定（共用、ユーザ）
     * @param name      事前定義SQLキー
     * @param sql       事前定義SQL本体
     */
    void updateSql(SqlPrivilege sp, String name, String sql)

    /**
     * 事前定義SQL追加
     *
     * @param sp        範囲指定（共用、ユーザ）
     * @param name      事前定義SQLキー
     * @param sql       事前定義SQL本体
     */
    void appendSql(SqlPrivilege sp, String name, String sql)

    /**
     * 事前定義SQL削除
     *
     * @param sp        範囲指定（共用、ユーザ）
     * @param name      事前定義SQLキー
     */
    void removeSql(SqlPrivilege sp, String name)

    Closure getTableGroupTransfer(String dbName)

    Closure getTableSelectSqlTransfer(String dbName)
	
	Map<String, String> getFieldNameMapping()

}
