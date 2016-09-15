package tools.graphic.sql.config

import tools.graphic.sql.gui.LogConsole

/**
 * ログ情報出力クラス
 *
 * Created by nwh on 2016/01/12.
 */
class Log {
    /**
     * デバッグ情報出力するかフラグ
     */
    static private boolean canDebug = false

    /**
     * 画面ログコンソール
     */
    static LogConsole logConsole

    /**
     * 起動時、システムプロパティ（tools.graphic.sql.DEBUG）を読み込み、デバッグモードを設定する。
     */
    static {
        String s = System.getProperty("tools.graphic.sql.DEBUG")
        if (s) canDebug = true
    }

    /**
     * デバッグ情報を出力する。
     *
     * @param msg   出力メッセージ。
     */
    static void debug(msg) {
        if (!canDebug) return
        // 最小の本プロジェクトコードを探す。
        StackTraceElement ste = Thread.currentThread().stackTrace[2 .. -2].find {
            !it.className.startsWith("tools.graphic.sql.config.Log") &&  it.className.startsWith("tools.graphic.sql") && it.lineNumber > 0
        }
        // データバグ情報を出力する。
        if (ste) {
            println "${ste.fileName}::${ste.lineNumber} >>> ${msg}"
        } else {
            println " >>> ${msg}"
        }
    }

    /**
     * お知らせ情報を出力する。
     *
     * @param msg
     */
    static void info(msg) {
        if (logConsole) {
            logConsole.black("${msg}")
        }
    }

    /**
     * エラー情報を出力する。
     *
     * @param msg
     */
    static void error(msg) {
        println "${msg}"
    }
}
