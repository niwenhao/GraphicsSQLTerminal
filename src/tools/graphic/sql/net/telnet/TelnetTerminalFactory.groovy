package tools.graphic.sql.net.telnet

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.net.Terminal
import tools.graphic.sql.net.TerminalFactory

/**
 * �ݒ�r���_�[�Ő����ł���Telnet�[���t�@�N�g���[
 *
 * Created by nwh on 2016/01/18.
 */
class TelnetTerminalFactory extends  TerminalFactory implements Buildable {
    /***
     * �[���I�u�W�F�N�g����
     *
     * @param hostname      �ڑ��z�X�g��
     * @param port          �ڑ��|�[�g
     * @return      �[���I�u�W�F�N�g
     */
    @Override
    Terminal createTerminal(String hostname, int port) {
        return new TelnetTerminal("VT100", hostname, port)
    }
}
