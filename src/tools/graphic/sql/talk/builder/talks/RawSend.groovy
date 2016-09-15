package tools.graphic.sql.talk.builder.talks
/**
 * Sendを拡張し、バイナリコードを送信できる。
 *
 * Created by nwh on 2016/01/08.
 */
class RawSend extends Send {
    /***
     * 送信するバイナリデータ
     */
    byte[] bytes

    /***
     * 設定ビルドがvalueで指定された場合、valueをbytesに入れる。
     *
     * @param attributes        設定ビルドで渡されるプロパティ
     */
    @Override
    void setupAttribute(Map attributes) {
        if (attributes.value) bytes = attributes.value
    }
}

