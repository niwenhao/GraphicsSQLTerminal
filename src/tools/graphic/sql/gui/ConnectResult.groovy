package tools.graphic.sql.gui

/**
 * �ڑ�����̌���
 */
class ConnectResult {
    /**
     * �ڑ�����
     */
    ResultCode resultCode
    /**
     * �ڑ����s�����ꍇ�A�G���[���b�Z�[�W�B
     */
    String errorMessage
    /**
     * �ڑ����������ꍇ�A�擾�����e�[�u���ꗗ�B
     */
    List<String> tables

    @Override
    public String toString() {
        return "ConnectResult{resultCode=${resultCode}, errorMessage='${errorMessage}', tables=${tables}}";
    }
}
