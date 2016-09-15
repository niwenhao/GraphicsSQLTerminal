package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.Log
import tools.graphic.sql.gui.FieldMeta
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult
import tools.graphic.sql.talk.TalkResult

import java.util.regex.Matcher

/**
 * SQLPLUS�Ŏ擾�����^�C�g���s���������A���ږ�����уT�C�Y���擾����B
 * <pre>
 * �����T�v
 * �P�D�O��
 *      SQLPLUS�ŕԂ����ʂ͂��獀�ږ��ƃT�C�Y�������̍s���烂����
 *      ?�F%%%%%%%%<#>���ږ�1 <#>���ږ�2           <#>......<#>���ږ�n
 *      ?�F--------<#>--------<#>------------------<#>......<#>-------------
 *      ?���獀�ږ�������B
 *      ?����e���ڂ̃f�[�^������������B
 * �Q�D����
 *      �P�D�}�b�`�O�����iprepareMatch�j
 *          �P�D���ڒ�`�ꗗ���N���A����B
 *          �Q�D�s�ԍ��ڒ�`�̂ݒǉ�����B
 *      �Q�D�s���Ƃ̃}�b�`�imatch�j
 *          �P�D���ږ����܂���߂ĂȂ��ꍇ�A/^%%%%%%%%<#>.*\r\n/���}�b�`���Č���
 *              �}�b�`�����ꍇ�A<#>�ŕ����Ă��鍀�ږ����擾����B
 *              ���ږ��̕����������ڂ̍ŏ��T�C�Y�Ƃ���B
 *              ����������Shift_jis�Ńf�R�[�h�����o�C�g���ɑ�������B
 *          �Q�D���ڃT�C�Y���܂���߂ĂȂ��ꍇ�A/^--------<#>.*\r\n/���}�b�`���Č���
 *              �}�b�`�����ꍇ�A<#>�ŕ����Ă���"-"���������ő�T�C�Y�Ƃ���B
 *          �R�D���ڃT�C�Y����߂��ꍇ�AGraphicSqlTerminal.controller.dataProcessClosure�ɏ���n���B
 *              �����̃v���O������UserInterface�ŃZ�b�g�����B
 * </pre>
 *
 * Created by nwh on 2016/01/05.
 */
class SqlPlusTitleMatcher extends SqlPlusMatchLinesTalkable {

    /**
     * ���ڈꗗ
     */
    List<FieldMeta> fmList = []

    /**
     * ��������
     */
    TalkResult result = TalkResult.FORWARD

    /**
     * ���ږ����`�������t���O
     */
    private boolean hasFieldName

    /**
     * ���ڃT�C�Y����`�������t���O
     */
    private boolean hasFieldSize

    /**
     * �}�b�`�̑O����
     */
    @Override
    void prepareMatch() {
        super.prepareMatch()
        fmList.clear()
        fmList << new FieldMeta(name: "NO", minLength: 2, maxLength: 8)
        hasFieldName = false
        hasFieldSize = false
    }

    /**
     * �}�b�`����
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        Log.debug("line: ${line}")
        if (!hasFieldName && line =~ /^%%%%%%%%<#>(.*)\r?\n$/) {
            Matcher.lastMatcher.group(1).split(/<#>/).each {
                FieldMeta fm = new FieldMeta()
                fm.name = it
                fm.minLength = it.getBytes("SHIFT_JIS").length
                fmList << fm
            }
            hasFieldName = true
        }
        if (hasFieldName && !hasFieldSize && line =~ /^--------<#>(.*)\r?\n$/) {
            Matcher.lastMatcher.group(1).split(/<#>/).eachWithIndex { String s, int i ->
                i++
                fmList[i].maxLength = s.getBytes("Shift_JIS").size()
                if (fmList[i].minLength > fmList[i].maxLength) {
                    fmList[i].maxLength = fmList[i].minLength
                }
            }
            SelectResult r = new SelectResult()
            r.fieldMetaList = fmList
            r.statusCode = ResultCode.FETCHED_TITLE
            hasFieldSize = true
            GraphicSqlTerminal.controller.dataProcessClosure.call(r)
            return true
        }
        return false
    }
}
