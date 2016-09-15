package tools.graphic.sql.talk.context.oracle

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.Log
import tools.graphic.sql.gui.ResultCode
import tools.graphic.sql.gui.SelectResult

/**
 * SQLPLUS�ŕԂ��s���獀�ڃf�[�^�𒊏o����@�\</br>
 * �@�\�T�v</br>
 * �s�f�[�^����������B</br>
 * SqlPlusSelectTalkable����ѐݒ�t�@�C����SQLPLUS�ɑ΂���ݒ�ɂ���āA�I���N��SELECT�͉��L�t�H�[�}�b�g�Ńf�[�^��Ԃ��B</br>
 * "########<#>����1�f�[�^<#>����2�f�[�^<#>........<#>����N�f�[�^\r\n"</br>
 * �{�N���X��MatchLinesTalkable�̍s���ƃ}�b�`����@�\�𗘗p�A"########<#>"�ŊJ�n����s��I�o�A<#>�ŕ������A�f�[�^��ResultResult�ɓ����B</br>
 * ���̃f�[�^��GraphicSqlTerminal.controller.dataProcessClosure�ŏ������Ă��炤�B</br>
 *
 * Created by nwh on 2016/01/05.
 */
class SqlPlusRowMatcher extends SqlPlusMatchLinesTalkable {
    /**
     * ���ݏo�͍s�ԍ���ێ�����
     */
    int rownum

    /**
     * �}�b�`����̏�������
     */
    @Override
    void prepareMatch() {
        super.prepareMatch()
        rownum = 1
    }

    /**
     * �s���ƂɃ}�b�`����
     *
     * @param line
     * @return
     */
    @Override
    boolean match(String line) {
        Log.debug("line: ${line.toCharArray()}")
        if (line.startsWith("########<#>") && line.endsWith("\n")) {
            line = line.substring(11, line.size() - 1)
            List<String> items = line.split(/<#>/)
            SelectResult r = new SelectResult()
            r.fieldList = [rownum++]
            items.each {
                r.fieldList << it
            }
            r.statusCode = ResultCode.FETCHED_ROW
            GraphicSqlTerminal.controller.dataProcessClosure.call(r)
        }
        return false
    }
}
