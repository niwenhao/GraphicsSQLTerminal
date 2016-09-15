package tools.graphic.sql.net

/**
 * �[���@�\�C���^�t�F�[�X
 * Created by nwh on 2015/12/29.
 */
interface Terminal {
    /**
     * �e�L�X�g���M
     *
     * @param message ���M�e�L�X�g
     */
    void puts(String message)

    /***
     * �p�X���[�h�𑗐M����B
     *
     * @param pwd �p�X���[�h�e�L�X�g
     * @param mask �\�������e�L�X�g
     */
    void password(String pwd, String mask)

    /***
     * �e�L�X�g��M
     *
     * @return ��M�e�L�X�g�i�����s�A�s���S�s�����肤��j
     */
    String gets()

    /**
     * ���f�[�^���M
     *
     * @param data ���M�f�[�^
     */
    void write(byte[] data)
}
