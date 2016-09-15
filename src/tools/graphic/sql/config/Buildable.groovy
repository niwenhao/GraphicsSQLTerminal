package tools.graphic.sql.config

import java.util.regex.Matcher

/**
 * ConfigBuilder�Ńr���h�ł���R���|�[�l���g���������K�v�ȋ@�\
 *
 * Created by nwh on 2016/01/07.
 */
trait Buildable {
    /**
     * �r���h�ł��Ȏq�R���|�[�l�b�g�ꗗ
     */
    List<Buildable> childrenBuildables = []
    /**
     * �r���h�I�Ȑe�R���|�[�l���g
     */
    Buildable parentBuildable
    /**
     * �r���h�I�Ȗ���
     */
    String buildableName

    /**
     * �q�R���|�[�l���g���ł���Ƃ��A�q�R���|�[�l���g�ݒ�
     *
     * @param child     �q�R���|�[�l���g
     */
    void setupChild(Buildable child) {

    }

    /**
     * �R���|�[�l���g�v���p�e�B�ݒ�
     *
     * @param attributes �v���p�e�B�L�[�E�o�����[
     */
    void setupAttribute(Map attributes) {

    }

    /**
     * �q�R���|�[�l���g�̎Q�Ƃ��擾���邽�߁A�v���p�e�B�̃V�~�����[�^�[
     *
     * @param name  �v���p�e�B���AbuildableName + "Buildable"�ŒP��R���|�[�l���g�𓾂ł��AbuildableName + "Buildables"�œ����ȃR���|�[�l�b�g�̃��X�g�𓾂���B
     *
     * @return  �擾�����R���|�[�l���g�܂��̓R���|�[�l���g���X�g
     */
    def propertyMissing(String name) {
        Matcher m = name =~ /^(.*)Buildable(s?)$/
        def p = null
        if (m.find()) {
            if (m.group(2) == "s") {
                p = this.childrenBuildables.grep {
                    it.buildableName == m.group(1)
                }
                if (p.size() == 0) {
                    p = null
                }
            } else {
                p = this.childrenBuildables.find {
                    it.buildableName == m.group(1)
                }
            }
        }

        if (p == null) {
            throw new MissingPropertyException("property ${name} has not found.")
        }
        return p
    }
}
