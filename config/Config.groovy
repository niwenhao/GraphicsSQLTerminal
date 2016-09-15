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
        timeoutSecond: 30000,								//	���O�C�����āA���삵�Ȃ��̎������O�A�E�g���ԁi�b�j
        //defaultAgwUser: "xxx",
        //defaultAgwPassword: "***",
        transTimeoutSecond: 30,								//	SQL���s�^�C���A�E�g�����l
        proxyHost: "192.177.237.211",						//	�F�؃v���L�V�[�z�X�g��
        proxyPort: 23,										//	�F�؃v���L�V�[�|�[�g�ԍ�
        rawSqlEnabled: true,								//	Select�ȊO��SQL�����s�\���i�{�Ԃ̏ꍇ�Ffalse�j
		fieldNameMappingPath: new File("fieldname.csv")		//	���ڂ�SQL���̂ƕ\�����̂̃}�b�s���OCSV
) {
    ui()

    appendAsTermialFactory(new TelnetTerminalFactory())

    appendAsDatabase(makeSqlplusCon(name: "0101.�{��DB", hostname: "172.106.205.157", osUid: "aplusr01", osPwd: "6RTYhi8T", oraUid: 'aplusr01', oraPwd: 'Bah2xX'))
    appendAsDatabase(makeSqlplusCon(name: "0102.�{��BAT", hostname: "172.106.205.163", osUid: "aplusr01", osPwd: "6RTYhi8T", oraUid: 'aplusr01', oraPwd: 'Bah2xX'))
    appendAsDatabase(makeSqlplusCon(name: "0201.����-1", hostname: "172.106.205.45", osUid: "aplusr11", osPwd: "Im7TzKdK", oraUid: 'aplusr11', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0202.����-2", hostname: "172.106.205.45", osUid: "aplusr21", osPwd: "AgKKyJED", oraUid: 'aplusr21', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0203.����-3", hostname: "172.106.205.45", osUid: "aplusr31", osPwd: "4QxkAt17", oraUid: 'aplusr31', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0204.����-4", hostname: "172.106.205.45", osUid: "aplusr41", osPwd: "VKqUCqQW", oraUid: 'aplusr41', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0205.����-5", hostname: "172.106.205.45", osUid: "aplusr51", osPwd: "qRH8rCtN", oraUid: 'aplusr51', oraPwd: 'W8fLyS'))
    appendAsDatabase(makeSqlplusCon(name: "0301.��L-A", hostname: "172.106.205.45", osUid: "aplusra1", osPwd: "lNrXq9t5", oraUid: 'aplusra1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0302.��L-B", hostname: "172.106.205.45", osUid: "aplusrb1", osPwd: "qJaZ6AEd", oraUid: 'aplusrb1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0303.��L-C", hostname: "172.106.205.45", osUid: "aplusrc1", osPwd: "PQ55qnN2", oraUid: 'aplusrc1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0304.��L-D", hostname: "172.106.205.45", osUid: "aplusrd1", osPwd: "5ZwajBak", oraUid: 'aplusrd1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0305.��L-E", hostname: "172.106.205.45", osUid: "aplusre1", osPwd: "KtCUbJUb", oraUid: 'aplusre1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0306.��L-F", hostname: "172.106.205.45", osUid: "aplusrf1", osPwd: "aNyUXqsN", oraUid: 'aplusrf1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0307.��L-G", hostname: "172.106.205.45", osUid: "aplusrg1", osPwd: "MgZqXU5S", oraUid: 'aplusrg1', oraPwd: 'ZWkXnQ'))
    appendAsDatabase(makeSqlplusCon(name: "0308.��L-H", hostname: "172.106.205.45", osUid: "aplusrh1", osPwd: "ZiTcrfq6", oraUid: 'aplusrh1', oraPwd: 'ZWkXnQ'))
}
