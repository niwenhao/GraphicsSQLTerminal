package tools.graphic.sql.net.telnet

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.net.Terminal
import tools.graphic.sql.net.TerminalFactory

/**
 * 設定ビルダーで生成できるTelnet端末ファクトリー
 *
 * Created by nwh on 2016/01/18.
 */
class TelnetTerminalFactory extends  TerminalFactory implements Buildable {
    /***
     * 端末オブジェクト生成
     *
     * @param hostname      接続ホスト名
     * @param port          接続ポート
     * @return      端末オブジェクト
     */
    @Override
    Terminal createTerminal(String hostname, int port) {
        return new TelnetTerminal("VT100", hostname, port)
    }
}
