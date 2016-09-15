package tools.graphic.sql.config

import tools.graphic.sql.gui.LogConsole

/**
 * ���O���o�̓N���X
 *
 * Created by nwh on 2016/01/12.
 */
class Log {
    /**
     * �f�o�b�O���o�͂��邩�t���O
     */
    static private boolean canDebug = false

    /**
     * ��ʃ��O�R���\�[��
     */
    static LogConsole logConsole

    /**
     * �N�����A�V�X�e���v���p�e�B�itools.graphic.sql.DEBUG�j��ǂݍ��݁A�f�o�b�O���[�h��ݒ肷��B
     */
    static {
        String s = System.getProperty("tools.graphic.sql.DEBUG")
        if (s) canDebug = true
    }

    /**
     * �f�o�b�O�����o�͂���B
     *
     * @param msg   �o�̓��b�Z�[�W�B
     */
    static void debug(msg) {
        if (!canDebug) return
        // �ŏ��̖{�v���W�F�N�g�R�[�h��T���B
        StackTraceElement ste = Thread.currentThread().stackTrace[2 .. -2].find {
            !it.className.startsWith("tools.graphic.sql.config.Log") &&  it.className.startsWith("tools.graphic.sql") && it.lineNumber > 0
        }
        // �f�[�^�o�O�����o�͂���B
        if (ste) {
            println "${ste.fileName}::${ste.lineNumber} >>> ${msg}"
        } else {
            println " >>> ${msg}"
        }
    }

    /**
     * ���m�点�����o�͂���B
     *
     * @param msg
     */
    static void info(msg) {
        if (logConsole) {
            logConsole.black("${msg}")
        }
    }

    /**
     * �G���[�����o�͂���B
     *
     * @param msg
     */
    static void error(msg) {
        println "${msg}"
    }
}
