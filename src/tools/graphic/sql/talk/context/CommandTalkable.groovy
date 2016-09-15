package tools.graphic.sql.talk.context

import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.util.VariableUtils

/**
 * コマンドを発行するTalkable
 * <pre>
 *  １．典型的な利用パターン
 *      CommandTalkable
 *          RegexMatchLinesTalkableなどのマッチ
 *      流れ：
 *      １．TalkControllerで対話開始時、CommandTalkable#commandで定義したコマンドを送信する。
 *      ２．コマンドの返す行に対して、子ビルドコンポーネントでマッチング、処理する。
 * </pre>
 * Created by nwh on 2016/01/05.
 */
class CommandTalkable extends BuildableTalkable {
    /**
     * 対話開始時送信コマンド
     */
    String command = null

    /**
     * 対話を開始する時点行う操作
     *
     * @param talkable      対話をコントロールするオブジェクト
     * @param terminal      送信、受信端末
     * @param variables     テキスト入れ替える変数マップ
     */
    @Override
    void prepareTalk(TalkController talkable, Terminal terminal, Map variables) {
        if (command) {
            this.prepareLines = VariableUtils.applyVariables(command, variables)
        }
        super.prepareTalk(talkable, terminal, variables)
    }

    /**
     * このコンポーネントがマッチ対象になったら、即時に送信し、以下の動きは本コンポーネントの子コンポーネントで定義される。
     * ですので、無条件にFORWARDする。
     * @param message
     * @return
     */
    @Override
    TalkResult talk(String message) {
        return TalkResult.FORWARD
    }
}
