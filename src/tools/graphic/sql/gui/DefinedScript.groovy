package tools.graphic.sql.gui

/**
 * 事前定義されるSQL
 */
class DefinedScript {
    /**
     * 共用SQL
     */
    Map<String, String> commonSqls
    /**
     * ログインユーザ専用SQL
     */
    Map<String, String> privatedSqls
}
