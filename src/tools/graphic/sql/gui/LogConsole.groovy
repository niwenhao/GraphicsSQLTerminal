package tools.graphic.sql.gui

import javax.swing.JCheckBox
import javax.swing.JComponent
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle

/**
 * �����\�ȃ��O�o�̓R���|�[�l���g
 * JTextArea�Ɣ�ׁA�e�L�X�g�ҏW�A�R�s�[�A�C�x���g�����A���f����؂�̂ĂāA�s�������A�`�捂������ǉ������B
 *
 * Created by nwh on 2015/12/25.
 */
class LogConsole extends JComponent {
    class Line {
        String line
        Color color
    }
    /**
     * ���p�t�H���g
     */
    Font font
    /**
     * �o�͍s�̍���
     */
    int lineHeight
    /**
     * �R���g���[���̍L��
     */
    int width
    /**
     * ���O�\���ő�s���A0��菬�����ꍇ�A�S�\���ɂȂ�B
     */
    int maxLines

    //�Ō�̍s�͉��s����?
    private boolean returned = true

    /**
     * �s�o�b�t�@
     */
    List<Line>lines = new ArrayList<Line>()

    /**
     * ���O�o�͂��L������邩�̃`�F�b�N�{�b�N�X�B
     */
    JCheckBox enableLogCheckBox
    /**
     * �������O�̍Ō�̍s��ǐՂ��邩�̃`�F�b�N�{�b�N�X�B
     */
    JCheckBox traceTailCheckBox

    /**
     * �o�͂��ꂽ���O���N���A����B
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
     * ���O��ǉ�����B
     * @param s �ǉ����O�e�L�X�g�A�����s���\
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
     * ���O��ǉ�����B
     * @param s �ǉ����O�e�L�X�g�A�����s���\
     * @param c �e�L�X�g�F
     */
    public void writeLog(String s, Color c) {
        if (enableLogCheckBox?.selected) {
            synchronized (lines) {
                //�ύX�O�̍s�����L�^�i�ĕ`��͈͂��Z�o�p�j
                int befLCnt = lines.size()
                //�s��ǉ�����B
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
                //�ύX��̍s�����L�^�i�ĕ`��͈͂��Z�o�p�j
                int aftLCnt = lines.size()

                if (maxLines > 0 && maxLines < lines.size()) {
                    //�ő働�O�s�����������ꍇ�A�s���V�t�g����B�S��ʕ`����s���B
                    for (int idx = lines.size(); idx > maxLines; idx --) {
                        lines.remove(0)
                    }
                    repaint(100, 0, 0, width, maxLines * lineHeight)
                    this.preferredSize = new java.awt.Dimension(width, maxLines * lineHeight)
                } else {
                    //�ő働�O�s���������Ȃ��ꍇ�A�ǉ������͈͂̂ݍĕ`����s���B
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
     * �`�悷��B
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
