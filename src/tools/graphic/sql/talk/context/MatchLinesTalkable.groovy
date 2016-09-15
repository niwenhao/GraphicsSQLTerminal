package tools.graphic.sql.talk.context

import tools.graphic.sql.talk.TalkResult

/**
 * �s���Ə��������{
 * <pre>
 *  TalkController/Talkable�M���b�Z�[�W���Ƃ̏������ł��邪�A���b�Z�[�W�������s�ł��Ȃ��̂ŁA��ʓI�ȍs���ƑΘb�ɂӂ��킵���Ȃ��B
 *  �{�N���X�͍s���ƑΘb�ɑΉ�����B
 *  ���s���Ƃł����A�p�X���[�h�Ȃǂ����܂��ꍇ�A���s�����Ȃ��̂ŁA�ŏI�̍s�̂݉��s�����ł���\�����Ή������B
 *  �����T�v
 *  �P�Dmessage���n����(talk)�Amessage��\n�ŕ����A���łɂ���s�ꗗ�ilines�j�ɒǉ�����B
 *      �s�ꗗ�̍Ō�͉��s�����̍s���l������B���̏ꍇ�A�Ō�̍s�ɐV�s��ǉ����邱�ƂɂȂ�B
 *  �Q�D�s�ꗗ����s���ƂɃ}�b�`����邩�𔻒f����imatch�j
 *      �}�b�`���ꂽ�ꍇ�A���̍s��processor�Œ�`�����v���O�����ɓn���A���O��`����matchedResult��Ԃ��B
 *      �����s���}�b�`�����ꍇ�A�}�b�`�����s���ƁAprocessor���Ăяo���B
 * </pre>
 * Created by nwh on 2015/12/29.
 */
abstract class MatchLinesTalkable extends BuildableTalkable {
    TalkResult matchedResult = TalkResult.CONTINUE
    List<String> lines = []

    /**
     * message�����s�ŕ������āAlines���X�g�ɒǉ�����B</br>
     * ��message�̍Ō�͉��s�ł͂Ȃ��ꍇ�A�����appendLines���Ăяo���Ƃ��Alines��lastElement�Ƀe�L�X�g�ǉ�����B
     * @param message
     */
    void appendLines(String message) {
        StringBuilder line
        if (lines.size() > 0 && ! lines.last().endsWith("\n")) {
            line = new StringBuilder(lines.pop())
        } else {
            line = new StringBuilder()
        }
        message.each {
            line << it
            if (it == '\n') {
                lines << line.toString()
                line = new StringBuilder()
            }
        }
        if(line.size()>0) {
            lines << line.toString()
        }
    }

    /**
     * �s�e�L�X�g�}�b�`
     * <pre>
     *  �{���\�b�h�͊g���N���X�Ɏ�������A�s�f�[�^�΂���}�b�`���ʂ����҂����B
     *  �{�N���X�̓����Ƃ��āA�s���Ɩ{���\�b�h���Ăяo���A�߂�l��true�̏ꍇ�A�߂�l��matchedResult�ɐݒ肵�Aprocessor�ɍs�e�L�X�g��n���A���̍s�̏������s���B
     *  ���}�b�`���Ă��A�����ɏI���ł͂Ȃ��A�㑱�̍s���}�b�`�ł��邩�̏������s���B�ł��̂ŁA�����s�Ƀ}�b�`������A�Ō�̃}�b�`���ʂ��L���A���ׂẴ}�b�`�����s��
     *    processor�ɏ��������B
     *  �{���\�b�h�̎����ł��A�n���ꂽ�s�e�L�X�g�����Ĕ��f���A�K�v�ł���΁A�Ó���matchedResult�ݒ肪�s���ׂ��B�������A�K�v�ł���΁Aprocessor�N���[�W���[�̐ݒ��
     *  ���ׂ��B
     * </pre>
     * @param line  �s�e�L�X�g�i���s�܂߁j
     * @return  �}�b�`����true:�}�b�`����
     */
    abstract boolean match(String line)

    /**
     * �{�N���X�̃��C������
     * <pre>
     *  �������e
     *  �P�D�󂯂��e�L�X�g���s���ƕ����āA�s�e�L�X�g���X�g�ɒǉ�����B�iappendLines�j
     *  �Q�D�s�e�L�X�g���X�g����A�s���Ə�������B
     *      �P�D�}�b�`���邩�𔻒�imatch�j
     *      �Q�D�}�b�`�����ꍇ�̏���
     *          �P�D�߂�l��matchedResult�̓��e�ɐݒ肷��B
     *          �Q�Dprocessor�Ŏw���Ă���N���[�W���[���Ăяo���B
     *  �R�D��������}�b�`���Ȃ��ꍇ�ATalkResult.CONTINUE��Ԃ��B
     * </pre>
     * @param message
     * @return
     */
    @Override
    TalkResult talk(String message) {
        TalkResult rst = TalkResult.CONTINUE
        synchronized (lines) {
            String s
            appendLines(message)
            while (lines.size() > 0) {
                s = lines.remove(0)
                if (match(s)) {
                    processor?.call(s)
                    rst = matchedResult
                }
            }
            if (!s.endsWith("\n")) {
                lines << s
            }
        }
        return rst
    }
}
