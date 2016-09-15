package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.config.Buildable

/**
 * 設定ビルド可能なSendオブジェクト
 * 本クラスおよび本クラスを拡張するクラスはSendAndWaitTalkableと共同的に動く、端末と対話する。
 * 本クラスはvalueで定義されたテキストを端末に送信する。送信が終わってからSendAndWaitTalkableのロジックによって
 * 次のWaitまたはSendで定められる処理を行う。
 *
 * Created by nwh on 2016/01/08.
 */
class Send implements Buildable {
    /***
     * 送信テキスト
     */
    String value

    @Override
    String toString() {
        return "${this.class.name} -> value:${value}"
    }
}
