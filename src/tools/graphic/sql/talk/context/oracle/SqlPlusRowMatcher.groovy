package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.Log
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult

/**
 * SQLPLUSで返す行から項目データを抽出する機能</br>
 * 機能概要</br>
 * 行データを処理する。</br>
 * SqlPlusSelectTalkableおよび設定ファイルのSQLPLUSに対する設定によって、オラクルSELECTは下記フォーマットでデータを返す。</br>
 * "########<#>項目1データ<#>項目2データ<#>........<#>項目Nデータ\r\n"</br>
 * 本クラスはMatchLinesTalkableの行ごとマッチする機能を利用、"########<#>"で開始する行を選出、<#>で分割し、データをResultResultに入れる。</br>
 * このデータをGraphicSqlTerminal.controller.dataProcessClosureで処理してもらう。</br>
 *
 * Created by nwh on 2016/01/05.
 */
class SqlPlusRowMatcher extends SqlPlusMatchLinesTalkable {
    /**
     * 現在出力行番号を保持する
     */
    int rownum

    /**
     * マッチするの準備処理
     */
    @Override
    void prepareMatch() {
        super.prepareMatch()
        rownum = 1
    }

    /**
     * 行ごとにマッチする
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        Log.debug("line: ${line.toCharArray()}")
        if (line.startsWith("########<#>") && line.endsWith("\n")) {
            line = line.substring(11, line.size() - 1)
            List<String> items = line.split(/<#>/)
            SelectResult r = new SelectResult()
            r.fieldList = [rownum++]
            items.each {
                r.fieldList << it
            }
            r.statusCode = ResultCode.FETCHED_ROW
            GraphicSqlTerminal.controller.dataProcessClosure.call(r)
        }
        return false
    }
}
