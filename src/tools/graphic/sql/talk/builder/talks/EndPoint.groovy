package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.talk.TalkResult

/**
 * Waitを拡張し、完了結果を設定できるようになる。
 * Created by nwh on 2016/01/08.
 */

class EndPoint extends Wait {
    /**
     * 完了結果
     */
    TalkResult result = TalkResult.STOP

    @Override
    String toString() {
        return super.toString() + ", result: ${result}"
    }
}
