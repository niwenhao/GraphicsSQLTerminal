package tools.graphic.sql.talk.builder

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.talk.builder.talks.*
import tools.graphic.sql.talk.context.MatchLinesTalkable
import tools.graphic.sql.util.VariableUtils

import javax.swing.*

/**
 * talks.*のコンポーネントと共同動き、talks.*のコンポーネットによって定義された手続きで端末と対話する。
 *
 * Created by nwh on 2016/01/04.
 */
class SendAndWaitTalkable extends MatchLinesTalkable implements Buildable {
    /**
     * 対話手続きを定義するコンポーネント一覧
     */
    List talks = []

    /**
     * 対話対象の端末
     */
    Terminal terminal

    /**
     * 入れ替える変数
     */
    Map variables

    /**
     * すべての定義が終わった場合、この対話結果を返す。
     */
    TalkResult unlessResult = TalkResult.STOP

    /***
     * 現在対話するオブジェクトインデックス
     */
    int talkIdx

    /***
     * このコンポーネントの子コンポーネントを設定する。</br>
     * 処理内容</br>
     * 子コンポーネントはWait/Sendである場合、talksに追加する。
     *
     * @param child     子コンポーネント
     */
    @Override
    void setupChild(Buildable child) {
        super.setupChild(child)
        if (child instanceof Send || child instanceof Wait) {
            talks << child
        }
    }

    /**
     * Sendコンポーネントに従って、送信処理</br>
     * 処理概要</br>
     * コンポーネントの種類によって、端末オブジェクトのメソッドを呼び出す。
     *
     * @param send  送信内容
     */
    void send(Send send) {
        Log.debug("send: ${send}")

        if (send instanceof RawSend) {
            parent.terminal.write(send.bytes)
        } else {
            String s = VariableUtils.applyVariables(send.value, parent.variables)
            if (send instanceof Password) {
                parent.terminal.password(s, send.mask)
            } else {
                parent.terminal.puts(s)
            }
        }
    }

    /**
     * Waitコンポーネントに従え、テキストを待つ</br>
     * 処理内容</br>
     * waitで定義されたパターン使って、messageに適用して見る。マッチされた場合、wait種類によって処理を振り分ける。</br>
     * マッチされなかった場合、子ビルドコンポーネントからWaitを探し、同じくマッチする。</br>
     * 振り分けロジック</br>
     * 一般的なWaitの場合、TalkResult.CONTINUEを返す。</br>
     * EndPointの場合、定義されたresultを返す。</br>
     * Failureの場合、エラーメッセージを表示する。</br>
     *
     * @param wait
     * @param message
     * @return
     */
    TalkResult wait(Wait wait, String message) {
        Log.debug("wait: ${wait}, message: ${message}")

        def waitOne = { Wait w, String s ->
            TalkResult r = null
            if (s =~ w.value) {
                r = TalkResult.CONTINUE
                if (w instanceof EndPoint) {
                    r = w.result
                    if (w instanceof Failure) {
                        JOptionPane.showMessageDialog(null, w.error)
                    }
                }
            }
            return r
        }

        TalkResult r = null
        r = waitOne(wait, message)
        if (r) {
            Log.debug("wait result: ${r}")
            return r
        }
        wait.childrenBuildables.find {
            r = waitOne(it, message)
            r != null
        }
        Log.debug("wait result: ${r}")
        return r
    }

    /**
     * 行単位でマッチする。</br>
     * 処理概要</br>
     * １．現在インデックス(talkIdx)から沿ってすべての続いたSendを送信</br>
     * ２．Waitに対してテキスト待ち</br>
     * ３．後続のSendを送信</br>
     * ４．前述処理のいずれでも範囲が超えた場合、unlessResult</br>
     * @param message   処理テキスト
     * @return  処理結果 true:マッチあり、false:マッチ無し
     */
    @Override
    boolean match(String message) {
        Log.debug("message => ${message}")
        Log.debug("talkIdx => ${talkIdx}")
        TalkResult r = null
        def curr

        def sendAll = {
            while (talkIdx < talks.size() && (curr = talks[talkIdx]) instanceof Send) {
                send(curr)
                talkIdx++
            }
        }

        sendAll()
        if (talkIdx >= talks.size()) {
            this.matchedResult = unlessResult
            return true
        }
        if ((r = wait(curr, message))) {
            talkIdx++
            if (r != TalkResult.CONTINUE) {
                this.matchedResult = r
                return true
            } else {
                sendAll()
                if (talkIdx >= talks.size()) {
                    if (r == TalkResult.CONTINUE || r == TalkResult.BREAK) {
                        this.matchedResult = unlessResult
                    }
                }
            }
        }
        return false
    }

    @Override
    void prepareMatch() {
        talkIdx = 0
    }

}
