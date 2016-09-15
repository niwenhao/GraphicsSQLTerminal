package tools.graphic.sql.net

/**
 * 端末機能インタフェース
 * Created by nwh on 2015/12/29.
 */
interface Terminal {
    /**
     * テキスト送信
     *
     * @param message 送信テキスト
     */
    void puts(String message)

    /***
     * パスワードを送信する。
     *
     * @param pwd パスワードテキスト
     * @param mask 表示されるテキスト
     */
    void password(String pwd, String mask)

    /***
     * テキスト受信
     *
     * @return 受信テキスト（複数行、不完全行がありうる）
     */
    String gets()

    /**
     * 生データ送信
     *
     * @param data 送信データ
     */
    void write(byte[] data)
}
