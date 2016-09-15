package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.talk.TalkResult

/**
 * EndPointを拡張し、マッチされたらSendAndWaitTalkableよってメッセージボックスでエラーを表示する。
 *
 * Created by nwh on 2016/01/08.
 */

class Failure extends EndPoint {
    /***
     * エラーメッセージ
     */
    String error

    /***
     * デフォルトリザルトをTalkResult.ERRORにする。
     */
    public Failure() {
        this.result = TalkResult.ERROR
    }

    @Override
    public String toString() {
        return super.toString() + ", error: ${error}"
    }
}
