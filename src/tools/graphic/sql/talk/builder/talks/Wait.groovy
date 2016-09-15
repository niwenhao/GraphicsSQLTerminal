package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.config.Buildable

import java.util.regex.Pattern

/**
 * 設定ビルド可能なWaitオブジェクト
 * 本クラスおよび本クラスを拡張するクラスはSendAndWaitTalkableと共同的に動く、端末と対話する。
 * 本クラスはvalueがマッチされるまで待ち続け、マッチされた場合、SendAndWaitTalkableのロジックによって
 * 次の#WaitまたはSendで定められる処理を行う。
 * 例外として、本クラスのサブビルドコンポーネットがEndPointが定義可能、サブビルドコンポーネントにマッチできる場合、そちらの
 * コンポーネントで定義された動きで完結する。
 *
 * Created by nwh on 2016/01/08.
 */
class Wait implements Buildable {
    /***
     * マッチするパターン
     */
    Pattern value

    @Override
    String toString() {
        return "${this.class.name} -> value:${value}"
    }
}
