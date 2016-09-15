package tools.graphic.sql.gui

/**
 * ���[�U�C���^�t�F�[�X�ɒ񋟂���@�\�C���^�t�F�[�X
 *
 * Created by nwh on 2016/01/07.
 */
interface GuiController {
    /**
     * �f�[�^�x�[�X���̈ꗗ
     *
     * @return  ���̈ꗗ
     */
    List<String> getDatabaseNames()

    /**
     * �������ԃ��O�A�E�g�^�C���A�E�g
     *
     * @return      �^�C���A�E�g�a��
     */
    Integer getTimeoutSecond()

    /**
     * SQL���s�L�����Z���^�C���A�E�g
     *
     * @return      �^�C���A�E�g�a��
     */
    Integer getTransTimeoutSecond()

    /**
     * DB�ڑ�
     *
     * @param dbName    �f�[�^�x�[�X��
     * @param agwUid    �v���L�V�[���[�UID
     * @param agwPwd    �v���L�V�[�p�X���[�h
     * @return          �ڑ�����
     */
    ConnectResult connect(String dbName, String agwUid, String agwPwd)

    /**
     * DB�ڑ��ؒf
     *
     * @param dbName �f�[�^�x�[�X��
     */
    ConnectResult disconnect(String dbName)

    /**
     * ���O��`SQL���擾����B
     * @param sp    �͈͎w��i���p�A���[�U�j
     * @return  �L�[�ASQL�}�b�v
     */
    Map<String, String> getDefinedScript(SqlPrivilege sp)

    /**
     * SELECT�����s����B
     * @param dbName    �Ώۃf�[�^�x�[�X��
     * @param sql       ���sSQL
     * @param dataProcessClosure    �������ʂ��󂯎��Closure
     */
    void select(String dbName, String sql, Closure dataProcessClosure)

    /**
     * �C��SQL�����s����B
     * @param dbName    �Ώۃf�[�^�x�[�X��
     * @param sql       ���sSQL
     * @param dataProcessClosure    �������ʂ��󂯎��Closure
     */
    void execute(String dbName, String script, Closure resultProcessClosure)

    /**
     * SQL���s�r���ŃL�����Z������B
     *
     * @param dbName        �Ώۃf�[�^�x�[�X��
     */
    void cancel(String dbName)

    /**
     * ���O��`SQL�X�V
     *
     * @param sp        �͈͎w��i���p�A���[�U�j
     * @param name      ���O��`SQL�L�[
     * @param sql       ���O��`SQL�{��
     */
    void updateSql(SqlPrivilege sp, String name, String sql)

    /**
     * ���O��`SQL�ǉ�
     *
     * @param sp        �͈͎w��i���p�A���[�U�j
     * @param name      ���O��`SQL�L�[
     * @param sql       ���O��`SQL�{��
     */
    void appendSql(SqlPrivilege sp, String name, String sql)

    /**
     * ���O��`SQL�폜
     *
     * @param sp        �͈͎w��i���p�A���[�U�j
     * @param name      ���O��`SQL�L�[
     */
    void removeSql(SqlPrivilege sp, String name)

    Closure getTableGroupTransfer(String dbName)

    Closure getTableSelectSqlTransfer(String dbName)
	
	Map<String, String> getFieldNameMapping()

}
