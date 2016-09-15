package tools.graphic.sql.gui

import javax.swing.JCheckBox
import javax.swing.JComponent
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle

/**
 * 高性能なログ出力コンポーネント
 * JTextAreaと比べ、テキスト編集、コピー、イベント応答、モデルを切り捨てて、行数制限、描画高速化を追加される。
 *
 * Created by nwh on 2015/12/25.
 */
class LogConsole extends JComponent {
    class Line {
        String line
        Color color
    }
    /**
     * 利用フォント
     */
    Font font
    /**
     * 出力行の高さ
     */
    int lineHeight
    /**
     * コントロールの広さ
     */
    int width
    /**
     * ログ表示最大行数、0より小さい場合、全表示になる。
     */
    int maxLines

    //最後の行は改行あり?
    private boolean returned = true

    /**
     * 行バッファ
     */
    List<Line>lines = new ArrayList<Line>()

    /**
     * ログ出力が有効されるかのチェックボックス。
     */
    JCheckBox enableLogCheckBox
    /**
     * いつもログの最後の行を追跡するかのチェックボックス。
     */
    JCheckBox traceTailCheckBox

    /**
     * 出力されたログをクリアする。
     */
    public void clear() {
        synchronized(lines) {
            lines.clear()
            returned = true

            this.preferredSize = new java.awt.Dimension(0, 0)
            revalidate()
        }
    }

    /**
     * ログを追加する。
     * @param s 追加ログテキスト、複数行が可能
     */
    public void writeLog(String s) {
        black(s)
    }

    public void red(String s) {
        writeLog(s, Color.RED)
    }

    public void blue(String s) {
        writeLog(s, Color.BLUE)
    }

    public void black(String s) {

        writeLog(s, Color.BLACK)
    }

    /**
     * ログを追加する。
     * @param s 追加ログテキスト、複数行が可能
     * @param c テキスト色
     */
    public void writeLog(String s, Color c) {
        if (enableLogCheckBox?.selected) {
            synchronized (lines) {
                //変更前の行数を記録（再描画範囲を算出用）
                int befLCnt = lines.size()
                //行を追加する。
                s.split(/\n/).each { l ->
                    if (returned) {
                        lines << new Line(line: l, color: c)
                    } else {
                        lines[-1].line = lines[-1].line + l
                        lines[-1].color = c
                        returned = true
                    }
                }
                returned = s.endsWith("\n")
                //変更後の行数を記録（再描画範囲を算出用）
                int aftLCnt = lines.size()

                if (maxLines > 0 && maxLines < lines.size()) {
                    //最大ログ行数が超えた場合、行をシフトする。全画面描画を行う。
                    for (int idx = lines.size(); idx > maxLines; idx --) {
                        lines.remove(0)
                    }
                    repaint(100, 0, 0, width, maxLines * lineHeight)
                    this.preferredSize = new java.awt.Dimension(width, maxLines * lineHeight)
                } else {
                    //最大ログ行数が超えない場合、追加した範囲のみ再描画を行う。
                    int bottom = aftLCnt * lineHeight
                    int befBottom = befLCnt * lineHeight

                    this.preferredSize = new java.awt.Dimension(width, bottom)
                    repaint(100, 0, befBottom, width, bottom-befBottom)
                }
                revalidate()
            }
            if (traceTailCheckBox?.selected) {
                scrollRectToVisible(new Rectangle(0, height - 1, 1, 1))
            }
        }
    }

    /**
     * 描画する。
     *
     * @param g
     */
    void paint(Graphics g) {
        synchronized (lines) {
            g.font = this.font
            g.color = Color.BLACK

            g.clearRect((int)g.clipBounds.x, (int)g.clipBounds.y, (int)g.clipBounds.width, (int)g.clipBounds.height)

            int begin = g.clipBounds.y / lineHeight - 1
            begin = begin < 0 ? 0 : begin
            int end = begin + g.clipBounds.height / lineHeight + 1
            end = end >= lines.size() ? lines.size()-1 : end

            int pos = (begin + 1)*lineHeight
            for (int idx = begin; idx <= end; idx++) {
                g.color = lines[idx].color
                g.drawString(lines[idx].line, 0, pos)
                pos += lineHeight
            }
        }
    }

}
