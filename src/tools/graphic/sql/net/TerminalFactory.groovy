package tools.graphic.sql.net

/**
 * �ݒ�r���_�[�Ő����ł���[���t�@�N�g���[
 *
 * Created by nwh on 2016/01/18.
 */
abstract class TerminalFactory {
    /***
     * �[���I�u�W�F�N�g����
     *
     * @param hostname      �ڑ��z�X�g��
     * @param port          �ڑ��|�[�g
     * @return      �[���I�u�W�F�N�g
     */
    abstract Terminal createTerminal(String hostname, int port)
}
