package tools.graphic.sql.talk.context.mysql

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult

import java.util.regex.Matcher

/**
 * Created by nwh on 2016/01/22.
 */
class MysqlRowMatcher extends MysqlMatchLinesTalkable {
    private int rownum
    @Override
    void prepareMatch() {
        super.prepareMatch()
        rownum = 1
    }

    @Override
    boolean match(String line) {
        if(line =~ /\| ######## \| (.*) \| *\r?\n$/) {
            SelectResult ret = new SelectResult(statusCode: ResultCode.FETCHED_ROW, fieldList: [])

            Matcher.lastMatcher.group(1).split(/ \| /).each {
                ret.fieldList << it.trim()
            }
            GraphicSqlTerminal.controller.dataProcessClosure.call(ret)
        }
        return false
    }
}
