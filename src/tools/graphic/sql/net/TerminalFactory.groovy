package tools.graphic.sql.net

/**
 * 設定ビルダーで生成できる端末ファクトリー
 *
 * Created by nwh on 2016/01/18.
 */
abstract class TerminalFactory {
    /***
     * 端末オブジェクト生成
     *
     * @param hostname      接続ホスト名
     * @param port          接続ポート
     * @return      端末オブジェクト
     */
    abstract Terminal createTerminal(String hostname, int port)
}
