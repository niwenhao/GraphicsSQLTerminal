package tools.graphic.sql

import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader
import com.esotericsoftware.yamlbeans.YamlWriter
import com.opencsv.CSVReader;

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.config.DBConnection
import tools.graphic.sql.config.Log
import tools.graphic.sql.gui.*
import tools.graphic.sql.talk.TalkController
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.talk.context.RootTalkable

/**
 * UserInterface��GuiController�Œ�߂��@�\��񋟂���
 *
 * Created by nwh on 2015/12/24.
 */
class GraphicSqlTerminal implements GuiController, Buildable {
    int version = 1
    /**
     * ���
     */
    UserInterface gui
    /**
     * �m�[����Ń��O�A�E�g����^�C���A�E�g
     */
    Integer timeoutSecond
    /**
     * SQL���s�^�C���A�E�g
     */
    Integer transTimeoutSecond
    /**
     * �v���L�V�T�[�o�z�X�g��/IP
     */
    String proxyHost
    /**
     * �v���L�V�|�[�g
     */
    Integer proxyPort

    /**
     * �f�t�H���g�F�؃��[�U
     */
    String defaultAgwUser = ""

    /**
     * �f�t�H���g�F�؃p�X���[�h
     */
    String defaultAgwPassword = ""

    /**
     * SELECT�R�}���h�̎擾�f�[�^�����N���[�W���[
     */
    Closure dataProcessClosure
    /**
     * �ڑ�����DB��`���
     */
    DBConnection dbConfig = null
	
	File fieldNameMappingPath = null
	
    /**
     * �Θb�R���g���[���[
     */
    TalkController talk = new TalkController()
    /**
     * �ڑ��f�[�^�̃e�[�u���ꗗ
     */
    List<String> tableNameList = []

    /**
     * SQL�����̂܂ܑ��M�@�\��V�s���邩�B
     */
    Boolean rawSqlEnabled = false;

    /**
     * �C���X�^���X��GuiInterface�ɓǂ܂�邽�߁A�O���[�o���C���X�^���X
     */
    static public GraphicSqlTerminal controller

    /**
     * predefined_sql.yaml���玖�O��`SQL��ǂݍ���<br/>
     * �Ⴄ�o�[�W�����Ԃ̍��ق��z������
     * @return
     */
    private Map<String, Map<String, String>> loadDefinedSql() {
        //�v���[��`����SQL�ꗗ���擾����B
        //SQL��`�t�@�C�����Ȃ���΁A���C����`�t�@�C����default_sqls�ŏ���������B
        YamlReader reader = null
        try {
            reader = new YamlReader(new FileReader('predefined_sql.yaml'))
            Map map = reader.read()
            switch(map.version) {
                case null:
                    if (true) {
                        List<String> removeKeys = []
                        map.version = 1
                        map.common = [ : ]
                        map.each { key, value ->
                            if (value instanceof String) {
                                removeKeys << key
                                map.common."${key}" = value
                            }
                        }

                        removeKeys.each {
                            map.remove(it)
                        }
                    }
                default:
                    return map
            }
        } catch (e) {
            return [common: [:]]
        } finally {
            if (reader) {
                reader.close()
            }
        }
    }

    /**
     * ���O��`SQL��predefined_sql.yaml�ɕۑ�����B
     * @param sqlMap    ���O��`SQL
     * @return
     */
    private saveDefinedSql(Map<String, Map<String, String>> sqlMap) {
        YamlWriter writer = null
        try {
            writer = new YamlWriter(new FileWriter("predefined_sql.yaml"))
            writer.write(sqlMap)
        } finally {
            if (writer) writer.close()
        }
    }

    /**
     * �f�[�^�x�[�X���ꗗ���擾����
     * @return  �f�[�^�x�[�X�����X�g
     */
    @Override
    List<String> getDatabaseNames() {
        this.databaseBuildables.map { it.name }
    }

    /**
     * DB�ɐڑ�����
     *
     * @param dbName    �f�[�^�x�[�X��
     * @param agwUid    ���O�C�����[�UID
     * @param agwPwd    ���O�C���p�X���[�h
     * @return      �ڑ����ʁi���Ƀe�[�u���ꗗ������܂��B�j
     */
    @Override
    ConnectResult connect(String dbName, String agwUid, String agwPwd) {
        tableNameList.clear()
        talk.terminal = this.termialFactoryBuildable.createTerminal(proxyHost, proxyPort)
        Map<String, String> variables = [AGW_UID: agwUid, AGW_PWD: agwPwd]

        dbConfig = this.databaseBuildables.find { it.name == dbName } as DBConnection
        dbConfig.user = agwUid
        TalkResult r = talk.talk(dbConfig.logonBuildable, variables)
        Log.debug("Talk result -> ${r}")

        ConnectResult result = new ConnectResult()
        if (r == TalkResult.ERROR) {
            talk.talk(dbConfig.resetBuildable, [:])
            result.resultCode = ResultCode.FAIL
        } else {
            result.resultCode = ResultCode.SUCCESS
            result.tables = tableNameList.clone()
        }
        return result
    }

    /**
     * DB�ڑ���ؒf����
     *
     * @param dbName
     * @return
     */
    @Override
    ConnectResult disconnect(String dbName) {
        talk.talk(dbConfig.logoffBuildable, [:])
        return new ConnectResult(resultCode: ResultCode.SUCCESS)
    }

    /**
     * ���O��`SQL���擾����
     *
     * @param sp    ����
     * @return      ���� => SQL�̃}�b�v
     */
    @Override
    Map<String, String> getDefinedScript(SqlPrivilege sp) {
        Map<String, Map<String, String>> definedSql = loadDefinedSql()

        return takeNamedSqls(sp, definedSql)
    }

    /**
     * SELECT�����擾����
     *
     * @param dbName
     * @param sql
     * @param dataProcessClosure    �擾���ʂ���������N���[�W���[ ������SelectResult
     */
    @Override
    void select(String dbName, String sql, Closure dataProcessClosure) {
        this.dataProcessClosure = dataProcessClosure
        dbConfig.selectBuildable.command = sql
        talk.talk(dbConfig.selectBuildable, [:])
    }

    /**
     * ��ʓI��SQL�������s����A���s�ł͓��ɏ������邱�Ƃ��Ȃ��ALOG�ɏo�͂��邾��
     *
     * @param dbName
     * @param script
     * @param resultProcessClosure
     */
    @Override
    void execute(String dbName, String script, Closure resultProcessClosure) {
        RootTalkable rtc = dbConfig.executeBuildable
        rtc?.prepareLines = script
        talk.talk(rtc, [:])
    }

    /**
     * �������L�����Z��
     *
     * @param dbName
     */
    @Override
    void cancel(String dbName) {
        talk.talk(dbConfig.cancelBuildable, [:])
    }

    /**
     * ���O��`SQL���擾����
     *
     * @param sp    �����i���p�E�l�p�j
     * @param definedSql    �t�@�C�����烍�[�h���ꂽ����
     * @return  ���� => SQL ��Map
     */
    private Map<String, String> takeNamedSqls(SqlPrivilege sp, Map<String, Map<String, String>> definedSql) {
        Map<String, String> sqls = null
        switch (sp) {
            case SqlPrivilege.COMMON:
                sqls = definedSql.common
                break
            case SqlPrivilege.PRIVATED:
                sqls = definedSql."${dbConfig.user}"
                if (sqls == null) {
                    sqls = [:]
                    definedSql."${dbConfig.user}" = sqls
                }
                break
            default:
                throw new RuntimeException("Invalid value ${sp}")
        }
        return sqls
    }

    /**
     * ���O��`SQL�X�V
     *
     * @param sp    �����i���p�E�l�p�j
     * @param name  ����
     * @param sql
     */
    @Override
    void updateSql(SqlPrivilege sp, String name, String sql) {
        Map<String, Map<String, String>> definedSql = loadDefinedSql()

        takeNamedSqls(sp, definedSql)."${name}" = sql

        saveDefinedSql(definedSql)
    }

    /**
     * ���O��`SQL�ǉ�
     *
     * @param sp    �����i���p�E�l�p�j
     * @param name  ����
     * @param sql
     */
    @Override
    void appendSql(SqlPrivilege sp, String name, String sql) {
        updateSql(sp, name, sql)
    }

    /**
     * ���O��`SQL�폜
     *
     * @param sp    �����i���p�E�l�p�j
     * @param name  ����
     */
    @Override
    void removeSql(SqlPrivilege sp, String name) {
        Map<String, Map<String, String>> definedSql = loadDefinedSql()

        takeNamedSqls(sp, definedSql).remove(name)

        Log.debug("definedSql removed -> ${definedSql}")

        saveDefinedSql(definedSql)
    }

    @Override
    Closure getTableGroupTransfer(String dbName) {
        return dbConfig.tableGroupTransfer
    }

    @Override
    Closure getTableSelectSqlTransfer(String dbName) {
        return dbConfig.tableSelectSqlTransfer
    }
	/**
     * �ݒ肪�I������ALet's Go.........................................
     */
    void start() {
        this.uiBuildable.show()
    }
	
	private Map<String, String> fieldNameMapping = null;

	@Override
	Map<String, String> getFieldNameMapping() {
		CSVReader csvReader = null

		synchronized (this) {
			if (fieldNameMapping == null) {

				Map<String, String> map = new HashMap<String, String>()
				try {
					csvReader = new CSVReader(new FileReader(fieldNameMappingPath))
					String[] line
					while((line = csvReader.readNext()) != null) {
						map[line[0]] = line[1]
					}

				} catch(Exception e) {
					
				} finally {
					if (csvReader != null) csvReader.close()
				}

				fieldNameMapping = map
			}
		}
		return fieldNameMapping
	}
}
