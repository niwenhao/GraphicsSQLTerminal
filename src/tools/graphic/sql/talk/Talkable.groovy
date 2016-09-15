package tools.graphic.sql.talk

import tools.graphic.sql.net.Terminal

/**
 * �Θb�ł���I�u�W�F�N�g���`����B
 * Created by nwh on 2015/12/29.
 */
abstract class Talkable {
    /**
     * ���M�A��M�p�[��
     */
    Terminal terminal

    /**
     * ���M���b�Z�[�W����ւ���悤�ϐ�
     */
    Map variables

    /**
     * �����𐧌䂷��R���g���[��
     */
    TalkController talkable

    /**
     * �Θb�J�n����ꍇ�̑��M���b�Z�[�W
     */
    String prepareLines = null

    /**
     * ��M�������b�Z�[�W����������Talkable���X�g
     */
    List<Talkable> children = []

    /**
     * �eTalkable
     */
    Talkable parent

    /**
     * ��M�������b�Z�[�W�����Ă��邽�߂̃N���[�W���[
     */
    Closure processor

    /**
     * ���̃��b�Z�[�W���J�����gTalkable�ɂȂ�ꍇ�A�Θb����������
     * <pre>
     *      �g���N���X�Ŏ����\�ł����A�f�t�H�[���g�����̓p�����[�^��ۑ�������A#prepareLines�����s�ŕ����āA���M����B
     * </pre>
     * @param talkable
     * @param terminal
     * @param variables
     */
    void prepareTalk(TalkController talkable, Terminal terminal, Map variables) {
        this.talkable = talkable
        this.terminal = terminal
        this.variables = variables

        if (prepareLines != null) {
            String s = prepareLines
            List<String> l = s.split(/\n/)
            l.each { terminal.puts(it) }
        }
    }

    /**
     * �eTalkable���J�����g�ɂȂ鎞�_�A������Match��������
     */
    void prepareMatch() {
    }

    /**
     * ���������b�Z�[�W���}�b�`���闧��ɂȂ�ꍇ�A���b�Z�[�W���������郁�\�b�h
     *
     * @param messag    �������b�Z�[�W
     * @return      TalkController�͖߂�l�����āA�J�����gTalkable��ݒ肵�A���̓��������߂�B
     */
    abstract TalkResult talk(String message)
}
