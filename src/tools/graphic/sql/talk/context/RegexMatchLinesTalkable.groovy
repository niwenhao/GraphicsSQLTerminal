package tools.graphic.sql.talk.context

import java.util.regex.Pattern

/**
 * 正規表現パターンに行テキストマッチを行う
 * <pre>
 *  本クラスは行ごとマッチするクラス（MatchLinesTalkable）をベースして、正規表現でマッチする機能を提供する。
 *  一般的な利用方法は設定ビルドコンポーネントとして生成され、ビルドでmatchedResultとpatternを設定し、結果的にpatternにマッチした場合、
 *  matchedResultを返すことになる。
 *  例：Select発行した場合、他Talkableでデータを取るが、一つのRegexMatchLinesTalkableでSQL>をマッチ、ある場合、SQL実行完了を意味する。
 * </pre>
 * Created by nwh on 2016/01/06.
 */
class RegexMatchLinesTalkable extends MatchLinesTalkable {
    /**
     * マッチする正規表現パターン
     */
    Pattern pattern

    /**
     * マッチする
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        if (line =~ pattern) {
            return true
        }
        return false
    }

}
