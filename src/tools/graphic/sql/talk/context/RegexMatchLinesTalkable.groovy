package tools.graphic.sql.talk.context

import java.util.regex.Pattern

/**
 * ���K�\���p�^�[���ɍs�e�L�X�g�}�b�`���s��
 * <pre>
 *  �{�N���X�͍s���ƃ}�b�`����N���X�iMatchLinesTalkable�j���x�[�X���āA���K�\���Ń}�b�`����@�\��񋟂���B
 *  ��ʓI�ȗ��p���@�͐ݒ�r���h�R���|�[�l���g�Ƃ��Đ�������A�r���h��matchedResult��pattern��ݒ肵�A���ʓI��pattern�Ƀ}�b�`�����ꍇ�A
 *  matchedResult��Ԃ����ƂɂȂ�B
 *  ��FSelect���s�����ꍇ�A��Talkable�Ńf�[�^����邪�A���RegexMatchLinesTalkable��SQL>���}�b�`�A����ꍇ�ASQL���s�������Ӗ�����B
 * </pre>
 * Created by nwh on 2016/01/06.
 */
class RegexMatchLinesTalkable extends MatchLinesTalkable {
    /**
     * �}�b�`���鐳�K�\���p�^�[��
     */
    Pattern pattern

    /**
     * �}�b�`����
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        if (line =~ pattern) {
            return true
        }
        return false
    }

}
