package tools.graphic.sql.talk.context.mysql

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.gui.FieldMeta
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult

import java.util.regex.Matcher

/**
 * Created by nwh on 2016/01/22.
 */
class MysqlTitleMatcher extends MysqlMatchLinesTalkable {
    private boolean titleMatched

    @Override
    void prepareMatch() {
        super.prepareMatch()
        titleMatched = false
    }

    @Override
    boolean match(String line) {
        if (!titleMatched && line =~ /^\| %%%%%%%% \| (.*) \| *\r?\n$/) {
            String fieldLine = Matcher.lastMatcher.group(1)
            SelectResult rst = new SelectResult(statusCode: ResultCode.FETCHED_TITLE, fieldMetaList: [])
            fieldLine.split(/ \| /).each {
                FieldMeta fm = new FieldMeta(
                        name: it.trim(),
                        minLength: it.trim().getBytes(System.properties["file.encoding"]).size(),
                        maxLength: it.getBytes(System.properties["file.encoding"]).size()
                )

                rst.fieldMetaList << fm
            }
            titleMatched = true
            GraphicSqlTerminal.controller.dataProcessClosure.call(rst)
        }
        return false
    }
}
