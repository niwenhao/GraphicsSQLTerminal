package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.Log
import tools.graphic.sql.gui.FieldMeta
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult
import tools.graphic.sql.talk.TalkResult

import java.util.regex.Matcher

/**
 * SQLPLUSで取得したタイトル行を処理し、項目名およびサイズを取得する。
 * <pre>
 * 処理概要
 * １．前提
 *      SQLPLUSで返す結果はから項目名とサイズが書きの行からモラル
 *      ?：%%%%%%%%<#>項目名1 <#>項目名2           <#>......<#>項目名n
 *      ?：--------<#>--------<#>------------------<#>......<#>-------------
 *      ?から項目名が取れる。
 *      ?から各項目のデータ文字数が取れる。
 * ２．処理
 *      １．マッチ前処理（prepareMatch）
 *          １．項目定義一覧をクリアする。
 *          ２．行番項目定義のみ追加する。
 *      ２．行ごとのマッチ（match）
 *          １．項目名がまだ定めてない場合、/^%%%%%%%%<#>.*\r\n/をマッチして見る
 *              マッチした場合、<#>で分けている項目名を取得する。
 *              項目名の文字数を項目の最小サイズとする。
 *              ※文字数はShift_jisでデコードしたバイト数に相当する。
 *          ２．項目サイズがまだ定めてない場合、/^--------<#>.*\r\n/をマッチして見る
 *              マッチした場合、<#>で分けている"-"文字数を最大サイズとする。
 *          ３．項目サイズも定めた場合、GraphicSqlTerminal.controller.dataProcessClosureに情報を渡す。
 *              ※このプログラムはUserInterfaceでセットされる。
 * </pre>
 *
 * Created by nwh on 2016/01/05.
 */
class SqlPlusTitleMatcher extends SqlPlusMatchLinesTalkable {

    /**
     * 項目一覧
     */
    List<FieldMeta> fmList = []

    /**
     * 処理結果
     */
    TalkResult result = TalkResult.FORWARD

    /**
     * 項目名を定義したかフラグ
     */
    private boolean hasFieldName

    /**
     * 項目サイズが定義したかフラグ
     */
    private boolean hasFieldSize

    /**
     * マッチの前処理
     */
    @Override
    void prepareMatch() {
        super.prepareMatch()
        fmList.clear()
        fmList << new FieldMeta(name: "NO", minLength: 2, maxLength: 8)
        hasFieldName = false
        hasFieldSize = false
    }

    /**
     * マッチ処理
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        Log.debug("line: ${line}")
        if (!hasFieldName && line =~ /^%%%%%%%%<#>(.*)\r?\n$/) {
            Matcher.lastMatcher.group(1).split(/<#>/).each {
                FieldMeta fm = new FieldMeta()
                fm.name = it
                fm.minLength = it.getBytes("SHIFT_JIS").length
                fmList << fm
            }
            hasFieldName = true
        }
        if (hasFieldName && !hasFieldSize && line =~ /^--------<#>(.*)\r?\n$/) {
            Matcher.lastMatcher.group(1).split(/<#>/).eachWithIndex { String s, int i ->
                i++
                fmList[i].maxLength = s.getBytes("Shift_JIS").size()
                if (fmList[i].minLength > fmList[i].maxLength) {
                    fmList[i].maxLength = fmList[i].minLength
                }
            }
            SelectResult r = new SelectResult()
            r.fieldMetaList = fmList
            r.statusCode = ResultCode.FETCHED_TITLE
            hasFieldSize = true
            GraphicSqlTerminal.controller.dataProcessClosure.call(r)
            return true
        }
        return false
    }
}
