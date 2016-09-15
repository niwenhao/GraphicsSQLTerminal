package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.talk.context.CommandTalkable
import tools.graphic.sql.util.VariableUtils

import javax.swing.JOptionPane

/**
 * SQLPLUS対応のSELECT文発行クラス</br>
 * <pre>
 * 処理概要
 * １．設定構成
 *     本クラス
 *         SqlPlusTitleMatcher      タイトル行をマッチング、SQL取得項目情報を処理プログラムに渡す。
 *         SqlPlusRawMatcher        行データ行をマッチング、SQLで取得したデータを抽出し、処理プログラムに渡す。
 *         RegexMatchLinesTalkable   SQLプラスのコマンドプロンプトをマッチし、SELECTの実行完了を検知する。
 * ２．処理
 *      １．呼び出すクラスはSQL文を本クラスインスタンスのcommandプロパティにセットする。
 *      ２．TalkControllerクラスで本クラスインスタンスを駆使し、prepareTalkメソッドを呼び出す。
 *          １．commandのSQL文の改行をスペースに変える。
 *          ２．連続のスペースを一つスペースに変える。
 *          ３．";"で完了した場合、";"を削除する。
 *          ４．DDL、DMLを検出し、エラーとする。
 *          ５．SQL編集
 *              １．"select * from ......"の場合、"select '########' as \"%%%%%%%%\", t_t_t_t_t_t_t_t.* from (select * from ......) t_t_t_t_t_t_t_t"に変える。
 *              ２．その他のSELECT文の場合、'########' as \"%%%%%%%%\"を1番目の項目に訂正する。
 *              ３．SELECTではない場合、エラー
 *          ６．SQL文最後に";"を付け、端末に送信する。
 * </pre>
 * Created by nwh on 2016/01/05.
 */
class SqlPlusSelectTalkable extends CommandTalkable {
    @Override
    void prepareTalk(TalkController talkable, Terminal terminal, Map variables) {
        this.talkable = talkable
        this.terminal = terminal
        this.variables = variables

        String orgSql = VariableUtils.applyVariables(command, variables)

        StringBuilder sqlBuf = new StringBuilder()
        List<String> cmds = new ArrayList<String>()

        orgSql.eachLine { String ln ->
            if (sqlBuf.size()> 0) {
                sqlBuf.append("\n").append(ln)
            } else {
                if (ln ==~ /^ */) {

                } else if(ln.toLowerCase().startsWith("+ column ")) {
                   cmds << ln.substring(2)
                } else {
                    sqlBuf.append(ln)
                }
            }
        }



        String sql = sqlBuf.toString().replaceAll(/\n/, " ")
        sql = sql.replaceAll(/\r/, " ")
        sql = "     " + sql.trim()

        if (sql.endsWith(";")) {
            sql = sql[0 .. -2]
        }
        sql = sql.trim()
        String s = sql.toUpperCase()
        if (s =~ /^(.* )?(INSERT|DELETE|UPDATE|PURGE|DROP|ALTER|EXECUTE|CREATE|TRUNCATE|COMMIT|BEGIN|END)( .*)?$/) {
            String msg = "INSERT/DELETE/UPDATE/PURGE/DROP/ALTER/EXECUTE/CREATE/TRUNCATE/FOR UPDATE/COMMIT/BEGIN/END などが禁止です....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }
        if (s =~ /^SELECT +\*/) {
            sql = "select '########' as \"%%%%%%%%\", t_t_t_t_t_t_t_t.* from (${sql}) t_t_t_t_t_t_t_t"
        } else if (sql.toUpperCase().startsWith("SELECT ")) {
            sql = "select '########' as \"%%%%%%%%\"," + sql.substring(6)
        } else {
            String msg = "SELECTしか許しません....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }

        cmds.each {cmd ->
            terminal.puts(cmd)
        }

        terminal.puts(sql + ";")
    }
}
