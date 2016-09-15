package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.talk.TalkResult

/**
 * EndPoint���g�����A�}�b�`���ꂽ��SendAndWaitTalkable����ă��b�Z�[�W�{�b�N�X�ŃG���[��\������B
 *
 * Created by nwh on 2016/01/08.
 */

class Failure extends EndPoint {
    /***
     * �G���[���b�Z�[�W
     */
    String error

    /***
     * �f�t�H���g���U���g��TalkResult.ERROR�ɂ���B
     */
    public Failure() {
        this.result = TalkResult.ERROR
    }

    @Override
    public String toString() {
        return super.toString() + ", error: ${error}"
    }
}
