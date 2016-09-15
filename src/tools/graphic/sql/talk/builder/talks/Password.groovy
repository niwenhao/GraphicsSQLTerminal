package tools.graphic.sql.talk.builder.talks
/**
 * Sendを拡張し、パスワード送信する。
 * 送信する内容はログにmaskを出力する。
 *
 * Created by nwh on 2016/01/08.
 */
class Password extends Send {
    /***
     * パスワード保護用テイスト
     */
    String mask = "**************"
}

