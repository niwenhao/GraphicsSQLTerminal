package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.talk.context.CommandTalkable
import tools.graphic.sql.util.VariableUtils

import javax.swing.JOptionPane

/**
 * SQLPLUS�Ή���SELECT�����s�N���X</br>
 * <pre>
 * �����T�v
 * �P�D�ݒ�\��
 *     �{�N���X
 *         SqlPlusTitleMatcher      �^�C�g���s���}�b�`���O�ASQL�擾���ڏ��������v���O�����ɓn���B
 *         SqlPlusRawMatcher        �s�f�[�^�s���}�b�`���O�ASQL�Ŏ擾�����f�[�^�𒊏o���A�����v���O�����ɓn���B
 *         RegexMatchLinesTalkable   SQL�v���X�̃R�}���h�v�����v�g���}�b�`���ASELECT�̎��s���������m����B
 * �Q�D����
 *      �P�D�Ăяo���N���X��SQL����{�N���X�C���X�^���X��command�v���p�e�B�ɃZ�b�g����B
 *      �Q�DTalkController�N���X�Ŗ{�N���X�C���X�^���X����g���AprepareTalk���\�b�h���Ăяo���B
 *          �P�Dcommand��SQL���̉��s���X�y�[�X�ɕς���B
 *          �Q�D�A���̃X�y�[�X����X�y�[�X�ɕς���B
 *          �R�D";"�Ŋ��������ꍇ�A";"���폜����B
 *          �S�DDDL�ADML�����o���A�G���[�Ƃ���B
 *          �T�DSQL�ҏW
 *              �P�D"select * from ......"�̏ꍇ�A"select '########' as \"%%%%%%%%\", t_t_t_t_t_t_t_t.* from (select * from ......) t_t_t_t_t_t_t_t"�ɕς���B
 *              �Q�D���̑���SELECT���̏ꍇ�A'########' as \"%%%%%%%%\"��1�Ԗڂ̍��ڂɒ�������B
 *              �R�DSELECT�ł͂Ȃ��ꍇ�A�G���[
 *          �U�DSQL���Ō��";"��t���A�[���ɑ��M����B
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
            String msg = "INSERT/DELETE/UPDATE/PURGE/DROP/ALTER/EXECUTE/CREATE/TRUNCATE/FOR UPDATE/COMMIT/BEGIN/END �Ȃǂ��֎~�ł�....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }
        if (s =~ /^SELECT +\*/) {
            sql = "select '########' as \"%%%%%%%%\", t_t_t_t_t_t_t_t.* from (${sql}) t_t_t_t_t_t_t_t"
        } else if (sql.toUpperCase().startsWith("SELECT ")) {
            sql = "select '########' as \"%%%%%%%%\"," + sql.substring(6)
        } else {
            String msg = "SELECT���������܂���....\n${command}\n"
            JOptionPane.showMessageDialog(null, msg)
            throw new RuntimeException(msg)
        }

        cmds.each {cmd ->
            terminal.puts(cmd)
        }

        terminal.puts(sql + ";")
    }
}
