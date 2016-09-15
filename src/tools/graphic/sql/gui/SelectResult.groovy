package tools.graphic.sql.gui

/**
 * SELECT�̎��s���ʁi�f�[�^���擾����邽�тɘA�g����B�j
 */
class SelectResult {
    /**
     * ���s����
     */
    ResultCode statusCode
    /**
     * �G���[�ɂȂ�ꍇ�A�G���[���b�Z�[�W
     */
    String errorMessage

    /**
     * ���ڒ�`���擾���ꂽ�ꍇ�A���ڒ�`�ꗗ
     */
    List<FieldMeta> fieldMetaList
    /**
     * ���ڒl���擾���ꂽ�ꍇ�A�f�[�^�ꗗ
     */
    List<String> fieldList


    @Override
    public String toString() {
        return "SelectResult{" +
                "statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", fieldMetaList=" + fieldMetaList +
                ", fieldList=" + fieldList +
                '}';
    }
}
