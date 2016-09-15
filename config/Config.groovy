import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.ConfigBuilder
import tools.graphic.sql.net.telnet.TelnetTerminalFactory

/**
 * Created by nwh on 2016/01/22.
 */
Class connectClass = configEngine.loadScriptByName("DBConnect.groovy")
Closure makeSqlplusCon = connectClass.createSqlPlusConnectClosure
Closure makeSysdbaCon = connectClass.createSqlPlusConnectAsSysdbaClosure
Closure makeMysqlCon = connectClass.createMysqlConnectClosure

GraphicSqlTerminal.controller = new ConfigBuilder().gst(
        timeoutSecond: 30000,								//	ログインして、操作しないの自動ログアウト時間（秒）
        //defaultAgwUser: "xxx",
        //defaultAgwPassword: "***",
        transTimeoutSecond: 30,								//	SQL実行タイムアウト初期値
        proxyHost: "192.177.237.211",						//	認証プロキシーホスト名
        proxyPort: 23,										//	認証プロキシーポート番号
        rawSqlEnabled: true,								//	Select以外のSQL文発行可能か（本番の場合：false）
		fieldNameMappingPath: new File("fieldname.csv")		//	項目のSQL名称と表示名称のマッピングCSV
) {
    ui()

    appendAsTermialFactory(new TelnetTerminalFactory())

    appendAsDatabase(makeSqlplusCon(name: "0101.本番DB", hostname: "172.106.205.157", osUid: "aplusr01", osPwd: "6RTYhi8T", oraUid: 'aplusr01', oraPwd: 'Bah2xX'))
    appendAsDatabase(makeSqlplusCon(name: "0102.本番BAT", hostname: "172.106.205.163", osUid: "aplusr01", osPwd: "6RTYhi8T", oraUid: 'aplusr01', oraPwd: 'Bah2xX'))
    appendAsDatabase(makeSqlplusCon(name: "0201.総合-1", hostname: "172.106.205.45", osUid: "aplusr11", osPwd: "Im7TzKdK", oraUid: 'aplusr11', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0202.総合-2", hostname: "172.106.205.45", osUid: "aplusr21", osPwd: "AgKKyJED", oraUid: 'aplusr21', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0203.総合-3", hostname: "172.106.205.45", osUid: "aplusr31", osPwd: "4QxkAt17", oraUid: 'aplusr31', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0204.総合-4", hostname: "172.106.205.45", osUid: "aplusr41", osPwd: "VKqUCqQW", oraUid: 'aplusr41', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0205.総合-5", hostname: "172.106.205.45", osUid: "aplusr51", osPwd: "qRH8rCtN", oraUid: 'aplusr51', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0301.占有-A", hostname: "172.106.205.45", osUid: "aplusra1", osPwd: "lNrXq9t5", oraUid: 'aplusra1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0302.占有-B", hostname: "172.106.205.45", osUid: "aplusrb1", osPwd: "qJaZ6AEd", oraUid: 'aplusrb1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0303.占有-C", hostname: "172.106.205.45", osUid: "aplusrc1", osPwd: "PQ55qnN2", oraUid: 'aplusrc1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0304.占有-D", hostname: "172.106.205.45", osUid: "aplusrd1", osPwd: "5ZwajBak", oraUid: 'aplusrd1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0305.占有-E", hostname: "172.106.205.45", osUid: "aplusre1", osPwd: "KtCUbJUb", oraUid: 'aplusre1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0306.占有-F", hostname: "172.106.205.45", osUid: "aplusrf1", osPwd: "aNyUXqsN", oraUid: 'aplusrf1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0307.占有-G", hostname: "172.106.205.45", osUid: "aplusrg1", osPwd: "MgZqXU5S", oraUid: 'aplusrg1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0308.占有-H", hostname: "172.106.205.45", osUid: "aplusrh1", osPwd: "ZiTcrfq6", oraUid: 'aplusrh1', oraPwd: 'ZWkXnQ'))
}
