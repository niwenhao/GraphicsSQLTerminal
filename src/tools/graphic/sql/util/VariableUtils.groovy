package tools.graphic.sql.util

import java.util.regex.Matcher

/**
 * �ϐ�����ւ��w���p�[
 *
 * Created by nwh on 2016/01/04.
 */
class VariableUtils {
    /**
     * �e�L�X�g����?{�ϐ���}��T���A�ϐ��l�����ւ���
     *
     * @param src           ���e�L�X�g
     * @param variables     ����ւ��� �ϐ��� => �ϐ��l �}�b�v
     * @return      ����ւ����ς񂾃e�L�X�g
     */
    static public String applyVariables(String src, Map variables) {
        try {
            Matcher mch = src =~ /\?\{[^\}]+\}/
            int idx = 0
            StringBuilder sb = new StringBuilder()
            while(mch.find()) {
                sb << src.substring(idx, mch.start())
                String v = variables."${mch.group()[2 .. -2]}"
                if (v) sb << v
                idx = mch.end()
            }
            sb << src.substring(idx)
            return sb.toString()
        } catch (e) {
            throw new RuntimeException(e)
        }
    }

    /**
     * �v���p�e�B�� => �v���p�e�B�l �}�b�v�ɏ]���Aobj�̃v���p�e�B��ݒ肷��B
     *
     * @param obj
     * @param attributes
     */
    static public void applyAttribute(Object obj, Map<String, Object> attributes) {
        attributes.each {String k, Object v ->
            MetaProperty m = obj.hasProperty(k)
            if (m && m.type.isInstance(v)) {
                m.setProperty(obj, v)
            }
        }
    }
}
