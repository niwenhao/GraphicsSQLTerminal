package tools.graphic.sql.config
/**
 * �f�[�^�x�[�X�ڑ��ݒ���܂Ƃ߂�r���h�R���|�[�l���g
 *
 * Created by nwh on 2016/01/05.
 */
class DBConnection implements Buildable {

    Closure tableGroupTransfer = { String tableName ->
        return "�S�e�[�u��"
    }

    Closure tableSelectSqlTransfer = { String tableName ->
        return "select * from ${tableName}"
    }
    /**
     * ���O�C�����[�U���iProxy�j
     */
    String user

    /**
     * ���O�C���p�X���[�h�iProxy�j
     */
    String name
}
