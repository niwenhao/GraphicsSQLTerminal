package tools.graphic.sql.talk.builder.talks
/**
 * Send���g�����A�o�C�i���R�[�h�𑗐M�ł���B
 *
 * Created by nwh on 2016/01/08.
 */
class RawSend extends Send {
    /***
     * ���M����o�C�i���f�[�^
     */
    byte[] bytes

    /***
     * �ݒ�r���h��value�Ŏw�肳�ꂽ�ꍇ�Avalue��bytes�ɓ����B
     *
     * @param attributes        �ݒ�r���h�œn�����v���p�e�B
     */
    @Override
    void setupAttribute(Map attributes) {
        if (attributes.value) bytes = attributes.value
    }
}

