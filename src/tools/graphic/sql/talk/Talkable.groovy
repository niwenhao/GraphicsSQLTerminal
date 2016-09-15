package tools.graphic.sql.talk

import tools.graphic.sql.net.Terminal

/**
 * 対話できるオブジェクトを定義する。
 * Created by nwh on 2015/12/29.
 */
abstract class Talkable {
    /**
     * 送信、受信用端末
     */
    Terminal terminal

    /**
     * 送信メッセージ入れ替えるよう変数
     */
    Map variables

    /**
     * 動きを制御するコントローラ
     */
    TalkController talkable

    /**
     * 対話開始する場合の送信メッセージ
     */
    String prepareLines = null

    /**
     * 受信したメッセージを応答するTalkableリスト
     */
    List<Talkable> children = []

    /**
     * 親Talkable
     */
    Talkable parent

    /**
     * 受信したメッセージを初夏するためのクロージャー
     */
    Closure processor

    /**
     * このメッセージがカレントTalkableになる場合、対話を準備する
     * <pre>
     *      拡張クラスで実装可能ですが、デフォールト実装はパレメータを保存した上、#prepareLinesを改行で分けて、送信する。
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
     * 親Talkableがカレントになる時点、自分がMatch準備する
     */
    void prepareMatch() {
    }

    /**
     * 自分がメッセージをマッチする立場になる場合、メッセージを処理するメソッド
     *
     * @param messag    処理メッセージ
     * @return      TalkControllerは戻り値を見て、カレントTalkableを設定し、次の動きが決める。
     */
    abstract TalkResult talk(String message)
}
