package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.config.Buildable

/**
 * �ݒ�r���h�\��Send�I�u�W�F�N�g
 * �{�N���X����і{�N���X���g������N���X��SendAndWaitTalkable�Ƌ����I�ɓ����A�[���ƑΘb����B
 * �{�N���X��value�Œ�`���ꂽ�e�L�X�g��[���ɑ��M����B���M���I����Ă���SendAndWaitTalkable�̃��W�b�N�ɂ����
 * ����Wait�܂���Send�Œ�߂��鏈�����s���B
 *
 * Created by nwh on 2016/01/08.
 */
class Send implements Buildable {
    /***
     * ���M�e�L�X�g
     */
    String value

    @Override
    String toString() {
        return "${this.class.name} -> value:${value}"
    }
}
