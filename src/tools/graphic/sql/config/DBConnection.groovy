package tools.graphic.sql.config
/**
 * データベース接続設定をまとめるビルドコンポーネント
 *
 * Created by nwh on 2016/01/05.
 */
class DBConnection implements Buildable {

    Closure tableGroupTransfer = { String tableName ->
        return "全テーブル"
    }

    Closure tableSelectSqlTransfer = { String tableName ->
        return "select * from ${tableName}"
    }
    /**
     * ログインユーザ名（Proxy）
     */
    String user

    /**
     * ログインパスワード（Proxy）
     */
    String name
}
