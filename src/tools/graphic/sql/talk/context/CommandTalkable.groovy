package tools.graphic.sql.talk.context

import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.util.VariableUtils

/**
 * �R�}���h�𔭍s����Talkable
 * <pre>
 *  �P�D�T�^�I�ȗ��p�p�^�[��
 *      CommandTalkable
 *          RegexMatchLinesTalkable�Ȃǂ̃}�b�`
 *      ����F
 *      �P�DTalkController�őΘb�J�n���ACommandTalkable#command�Œ�`�����R�}���h�𑗐M����B
 *      �Q�D�R�}���h�̕Ԃ��s�ɑ΂��āA�q�r���h�R���|�[�l���g�Ń}�b�`���O�A��������B
 * </pre>
 * Created by nwh on 2016/01/05.
 */
class CommandTalkable extends BuildableTalkable {
    /**
     * �Θb�J�n�����M�R�}���h
     */
    String command = null

    /**
     * �Θb���J�n���鎞�_�s������
     *
     * @param talkable      �Θb���R���g���[������I�u�W�F�N�g
     * @param terminal      ���M�A��M�[��
     * @param variables     �e�L�X�g����ւ���ϐ��}�b�v
     */
    @Override
    void prepareTalk(TalkController talkable, Terminal terminal, Map variables) {
        if (command) {
            this.prepareLines = VariableUtils.applyVariables(command, variables)
        }
        super.prepareTalk(talkable, terminal, variables)
    }

    /**
     * ���̃R���|�[�l���g���}�b�`�ΏۂɂȂ�����A�����ɑ��M���A�ȉ��̓����͖{�R���|�[�l���g�̎q�R���|�[�l���g�Œ�`�����B
     * �ł��̂ŁA��������FORWARD����B
     * @param message
     * @return
     */
    @Override
    TalkResult talk(String message) {
        return TalkResult.FORWARD
    }
}
