package tools.graphic.sql.talk

import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal


/**
 *
 * <pre>
 *      下記のパターンになる。
 *      Talkable1
 *          Talkable11
 *          Talkable12
 *      TalkControll#talk(Talkable1)を呼び出したら、下記の処理になる。
 *      １．Talkable1のprepareTalkを呼び出す
 *      ２．Talkable11、Talkable12のprepareMatchを呼び出す
 *      ３．Terminal#getsで取得したテキストをTalkable11、Talkable12のTalkable#talkに渡す。
 *          戻り値によって振り分ける。
 *      まとめ
 *          Talkable1がカレントTalkableであり、初期送信をする（あれば）、受信したメッセージはカレントTalkableの
 *          children（Talkable11、Talkable12）たちに飲まれて、戻り値で次のカレントTalkableを決まり、カレントTalkableが変わったら
 *          また、初期送信をする。
 * </pre>
 * Created by nwh on 2015/12/29.
 */
class TalkController {
    /**
     * 送受信端末
     */
    Terminal terminal

    /**
     * 対話するメソッド
     *
     * @param root  一番目のTalkable
     * @param variables 送信内容入れ替え変数マップ
     * @return      最後のTalkResult
     */
    TalkResult talk(Talkable root, Map variables) {
        String message
        Talkable curr = root
        boolean contTalk = true
        TalkResult rst

        try {
            root.prepareTalk(this, terminal, variables)
            root.children.each { it.prepareMatch() }

            while(contTalk && (message = terminal.gets()) != null) {
                Log.debug("message => ${message}")
                boolean contContext = true
                curr.children.each { Talkable ctx ->
                    if (contContext) {
                        rst = ctx.talk(message)
                        Log.debug("Context(${ctx} -> ${rst}")
                        switch (rst) {
                            case TalkResult.BACKWORD:
                                curr = curr.parent
                                curr.prepareTalk(this, terminal, variables)
                                curr.children.each { it.prepareMatch() }
                                break
                            case TalkResult.FORWARD:
                                curr = ctx
                                curr.prepareTalk(this, terminal, variables)
                                curr.children.each { it.prepareMatch() }
                                break
                            case TalkResult.BREAK:
                                contContext = false
                                break
                            case TalkResult.STOP:
                            case TalkResult.ERROR:
                                contContext = false
                                contTalk = false
                                break
                        }
                    }
                }
            }
            return rst
        } catch(e) {
            throw new RuntimeException(e)
        }
    }
}