package tools.graphic.sql.net.telnet

import org.apache.commons.net.telnet.TelnetClient
import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal

/**
 * TerminalインタフェースのTelnet対応実装
 *
 * Created by nwh on 2015/12/29.
 */
class TelnetTerminal extends TelnetClient implements Terminal {
    /***
     * 接続状態
     */
    enum Status { CONECTED, WAITING, TERMINAZED }
    Status status

    /***
     * 文字列にするコーディング
     */
    String encoding = System.properties["file.encoding"]

    /***
     * バッファ
     */
    private byte[] buf = new byte[8192]

    /***
     * Telnet接続作成
     *
     * @param term      端末種類（VT100など）
     * @param host      ホスト名
     * @param port      ポート
     */
    public TelnetTerminal(String term, String host, int port) {
        super(term)
        try {
            connect(host, port)
            status = Status.CONECTED
        } catch(e) {
            status = Status.TERMINAZED
            throw new RuntimeException(e)
        }
    }

    /***
     * パスワードを送信する。
     *
     * @param pwd   パスワードテキスト
     * @param mask  表示されるテキスト
     */
    @Override
    void password(String pwd, String mask) {
        Log.logConsole.red("${mask}\n")
        this.outputStream << pwd << "\n"
        this.outputStream.flush()
        sleep(100)
    }

    /**
     * 生データ送信
     *
     * @param data 送信データ
     */
    void write(byte[] data) {
        Log.logConsole.red("${data}\n")
        this.outputStream.write(data)
        this.outputStream.flush()
    }

    /**
     * テキスト送信
     *
     * @param message   送信テキスト
     */
    void puts(String message) {
        Log.logConsole.red("${message}\n")
        synchronized (buf) {
            this.outputStream << message << "\n"
            this.outputStream.flush()
            sleep(100)
        }
    }

    /***
     * テキスト受信
     *
     * @return  受信テキスト（複数行、不完全行がありうる）
     */
    String gets() {
        Log.debug("gets")
        int len

        synchronized (buf) {
            status = Status.WAITING
            try {
                while((len = this.inputStream.read(buf)) <= 0) sleep 100
                String r = new String(buf, 0, len, encoding)
                Log.logConsole.blue("${r}")
                return r
            } finally {
                status = Status.CONECTED
            }
        }
    }

}
