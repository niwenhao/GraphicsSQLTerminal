package tools.graphic.sql.util

import java.util.regex.Matcher

/**
 * 変数入れ替えヘルパー
 *
 * Created by nwh on 2016/01/04.
 */
class VariableUtils {
    /**
     * テキストから?{変数名}を探し、変数値を入れ替える
     *
     * @param src           元テキスト
     * @param variables     入れ替える 変数名 => 変数値 マップ
     * @return      入れ替えが済んだテキスト
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
     * プロパティ名 => プロパティ値 マップに従え、objのプロパティを設定する。
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
