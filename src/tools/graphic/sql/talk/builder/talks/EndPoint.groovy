package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.talk.TalkResult

/**
 * Wait���g�����A�������ʂ�ݒ�ł���悤�ɂȂ�B
 * Created by nwh on 2016/01/08.
 */

class EndPoint extends Wait {
    /**
     * ��������
     */
    TalkResult result = TalkResult.STOP

    @Override
    String toString() {
        return super.toString() + ", result: ${result}"
    }
}
