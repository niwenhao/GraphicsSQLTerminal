package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.config.Buildable

import java.util.regex.Pattern

/**
 * �ݒ�r���h�\��Wait�I�u�W�F�N�g
 * �{�N���X����і{�N���X���g������N���X��SendAndWaitTalkable�Ƌ����I�ɓ����A�[���ƑΘb����B
 * �{�N���X��value���}�b�`�����܂ő҂������A�}�b�`���ꂽ�ꍇ�ASendAndWaitTalkable�̃��W�b�N�ɂ����
 * ����#Wait�܂���Send�Œ�߂��鏈�����s���B
 * ��O�Ƃ��āA�{�N���X�̃T�u�r���h�R���|�[�l�b�g��EndPoint����`�\�A�T�u�r���h�R���|�[�l���g�Ƀ}�b�`�ł���ꍇ�A�������
 * �R���|�[�l���g�Œ�`���ꂽ�����Ŋ�������B
 *
 * Created by nwh on 2016/01/08.
 */
class Wait implements Buildable {
    /***
     * �}�b�`����p�^�[��
     */
    Pattern value

    @Override
    String toString() {
        return "${this.class.name} -> value:${value}"
    }
}
