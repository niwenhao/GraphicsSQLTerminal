package tools.graphic.sql.talk.context.mysql

import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.talk.context.CommandTalkable

/**
 * Created by nwh on 2016/01/22.
 */
class MysqlSelectTalkable extends CommandTalkable {
    @Override
    void prepareTalk(TalkController talkable, Terminal terminal, Map variables) {
        this.talkable = talkable
        this.terminal = terminal
        this.variables = variables

        String sql = command.replaceAll(/\r?\n/, " ")
        sql = "     " + sql.trim()

        if (sql.endsWith(";")) {
            sql = sql[0 .. -2]
        }
        sql = sql.trim()
        String s = sql.toUpperCase()
        if (s =~ /^(.* )?(INSERT|DELETE|UPDATE|PURGE|DROP|ALTER|EXECUTE|CREATE|TRUNCATE|COMMIT|BEGIN|END)( .*)?$/) {
            String msg = "INSERT/DELETE/UPDATE/PURGE/DROP/ALTER/EXECUTE/CREATE/TRUNCATE/FOR UPDATE/COMMIT/BEGIN/END ‚È‚Ç‚ª‹ÖŽ~‚Å‚·....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }
        if (s =~ /^SELECT +\*/) {
            sql = "select '########' as \"%%%%%%%%\", t_t_t_t_t_t_t_t.* from (${sql}) t_t_t_t_t_t_t_t"
        } else if (sql.toUpperCase().startsWith("SELECT ")) {
            sql = "select '########' as \"%%%%%%%%\"," + sql.substring(6)
        } else {
            String msg = "SELECT‚µ‚©‹–‚µ‚Ü‚¹‚ñ....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }

        terminal.puts(sql + ";")
    }
}
