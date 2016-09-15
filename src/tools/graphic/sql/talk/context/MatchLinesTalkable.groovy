package tools.graphic.sql.talk.context

import tools.graphic.sql.talk.TalkResult

/**
 * 行ごと処理を実施
 * <pre>
 *  TalkController/Talkable信メッセージごとの処理ができるが、メッセージが複数行でもなれるので、一般的な行ごと対話にふさわしくない。
 *  本クラスは行ごと対話に対応する。
 *  ※行ごとですが、パスワードなどを求まれる場合、改行がこないので、最終の行のみ改行無しである可能性も対応した。
 *  処理概要
 *  １．messageが渡され(talk)、messageを\nで分割、すでにある行一覧（lines）に追加する。
 *      行一覧の最後は改行無しの行が考えられる。その場合、最後の行に新行を追加することになる。
 *  ２．行一覧から行ごとにマッチされるかを判断する（match）
 *      マッチされた場合、この行をprocessorで定義したプログラムに渡し、事前定義したmatchedResultを返す。
 *      複数行がマッチした場合、マッチした行ごと、processorを呼び出す。
 * </pre>
 * Created by nwh on 2015/12/29.
 */
abstract class MatchLinesTalkable extends BuildableTalkable {
    TalkResult matchedResult = TalkResult.CONTINUE
    List<String> lines = []

    /**
     * messageを改行で分割して、linesリストに追加する。</br>
     * ※messageの最後は改行ではない場合、次回のappendLinesを呼び出すとき、linesのlastElementにテキスト追加する。
     * @param message
     */
    void appendLines(String message) {
        StringBuilder line
        if (lines.size() > 0 && ! lines.last().endsWith("\n")) {
            line = new StringBuilder(lines.pop())
        } else {
            line = new StringBuilder()
        }
        message.each {
            line << it
            if (it == '\n') {
                lines << line.toString()
                line = new StringBuilder()
            }
        }
        if(line.size()>0) {
            lines << line.toString()
        }
    }

    /**
     * 行テキストマッチ
     * <pre>
     *  本メソッドは拡張クラスに実装され、行データ対するマッチ結果を期待される。
     *  本クラスの動きとして、行ごと本メソッドを呼び出し、戻り値はtrueの場合、戻り値をmatchedResultに設定し、processorに行テキストを渡し、次の行の処理を行う。
     *  ※マッチしても、即時に終了ではなく、後続の行がマッチできるかの処理を行う。ですので、複数行にマッチしたら、最後のマッチ結果が有効、すべてのマッチした行も
     *    processorに処理される。
     *  本メソッドの実装でも、渡された行テキストを見て判断し、必要であれば、妥当なmatchedResult設定が行うべき。もちろん、必要であれば、processorクロージャーの設定も
     *  やるべき。
     * </pre>
     * @param line  行テキスト（改行含め）
     * @return  マッチ結果true:マッチした
     */
    abstract boolean match(String line)

    /**
     * 本クラスのメイン処理
     * <pre>
     *  処理内容
     *  １．受けたテキストを行ごと分けて、行テキストリストに追加する。（appendLines）
     *  ２．行テキストリストから、行ごと処理する。
     *      １．マッチするかを判定（match）
     *      ２．マッチした場合の処理
     *          １．戻り値をmatchedResultの内容に設定する。
     *          ２．processorで指しているクロージャーを呼び出す。
     *  ３．いずれもマッチしない場合、TalkResult.CONTINUEを返す。
     * </pre>
     * @param message
     * @return
     */
    @Override
    TalkResult talk(String message) {
        TalkResult rst = TalkResult.CONTINUE
        synchronized (lines) {
            String s
            appendLines(message)
            while (lines.size() > 0) {
                s = lines.remove(0)
                if (match(s)) {
                    processor?.call(s)
                    rst = matchedResult
                }
            }
            if (!s.endsWith("\n")) {
                lines << s
            }
        }
        return rst
    }
}
