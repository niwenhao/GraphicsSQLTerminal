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
 * UserInterfaceにGuiControllerで定めた機能を提供する
 *
 * Created by nwh on 2015/12/24.
 */
class GraphicSqlTerminal implements GuiController, Buildable {
    int version = 1
    /**
     * 画面
     */
    UserInterface gui
    /**
     * ノー操作でログアウトするタイムアウト
     */
    Integer timeoutSecond
    /**
     * SQL実行タイムアウト
     */
    Integer transTimeoutSecond
    /**
     * プロキシサーバホスト名/IP
     */
    String proxyHost
    /**
     * プロキシポート
     */
    Integer proxyPort

    /**
     * デフォルト認証ユーザ
     */
    String defaultAgwUser = ""

    /**
     * デフォルト認証パスワード
     */
    String defaultAgwPassword = ""

    /**
     * SELECTコマンドの取得データ処理クロージャー
     */
    Closure dataProcessClosure
    /**
     * 接続したDB定義情報
     */
    DBConnection dbConfig = null
	
	File fieldNameMappingPath = null
	
    /**
     * 対話コントローラー
     */
    TalkController talk = new TalkController()
    /**
     * 接続データのテーブル一覧
     */
    List<String> tableNameList = []

    /**
     * SQL文そのまま送信機能を遊行するか。
     */
    Boolean rawSqlEnabled = false;

    /**
     * インスタンスをGuiInterfaceに読まれるため、グローバルインスタンス
     */
    static public GraphicSqlTerminal controller

    /**
     * predefined_sql.yamlから事前定義SQLを読み込み<br/>
     * 違うバージョン間の差異を吸収する
     * @return
     */
    private Map<String, Map<String, String>> loadDefinedSql() {
        //プレー定義したSQL一覧を取得する。
        //SQL定義ファイルがなければ、メイン定義ファイルのdefault_sqlsで初期化する。
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
     * 事前定義SQLをpredefined_sql.yamlに保存する。
     * @param sqlMap    事前定義SQL
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
     * データベース名一覧を取得する
     * @return  データベース名リスト
     */
    @Override
    List<String> getDatabaseNames() {
        this.databaseBuildables.map { it.name }
    }

    /**
     * DBに接続する
     *
     * @param dbName    データベース名
     * @param agwUid    ログインユーザID
     * @param agwPwd    ログインパスワード
     * @return      接続結果（中にテーブル一覧があります。）
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
     * DB接続を切断する
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
     * 事前定義SQLを取得する
     *
     * @param sp    権限
     * @return      名称 => SQLのマップ
     */
    @Override
    Map<String, String> getDefinedScript(SqlPrivilege sp) {
        Map<String, Map<String, String>> definedSql = loadDefinedSql()

        return takeNamedSqls(sp, definedSql)
    }

    /**
     * SELECT文を取得する
     *
     * @param dbName
     * @param sql
     * @param dataProcessClosure    取得結果を処理するクロージャー 引数がSelectResult
     */
    @Override
    void select(String dbName, String sql, Closure dataProcessClosure) {
        this.dataProcessClosure = dataProcessClosure
        dbConfig.selectBuildable.command = sql
        talk.talk(dbConfig.selectBuildable, [:])
    }

    /**
     * 一般的なSQL文を実行する、現行では特に処理することがなく、LOGに出力するだけ
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
     * 処理をキャンセル
     *
     * @param dbName
     */
    @Override
    void cancel(String dbName) {
        talk.talk(dbConfig.cancelBuildable, [:])
    }

    /**
     * 事前定義SQLを取得する
     *
     * @param sp    権限（共用・個人用）
     * @param definedSql    ファイルからロードされたもの
     * @return  名称 => SQL のMap
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
     * 事前定義SQL更新
     *
     * @param sp    権限（共用・個人用）
     * @param name  名称
     * @param sql
     */
    @Override
    void updateSql(SqlPrivilege sp, String name, String sql) {
        Map<String, Map<String, String>> definedSql = loadDefinedSql()

        takeNamedSqls(sp, definedSql)."${name}" = sql

        saveDefinedSql(definedSql)
    }

    /**
     * 事前定義SQL追加
     *
     * @param sp    権限（共用・個人用）
     * @param name  名称
     * @param sql
     */
    @Override
    void appendSql(SqlPrivilege sp, String name, String sql) {
        updateSql(sp, name, sql)
    }

    /**
     * 事前定義SQL削除
     *
     * @param sp    権限（共用・個人用）
     * @param name  名称
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
     * 設定が終わった、Let's Go.........................................
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
