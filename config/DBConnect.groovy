import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.ConfigBuilder
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.net.ControlCode

/**
 * オラクルSQLPLUS会話手続き定義
 */
static Closure getCreateSqlPlusConnectClosure() {
    return { Map<String, Object> attr ->
        return (new ConfigBuilder()).database(name: attr.name,
                tableGroupTransfer: { String tableName ->
                    return tableName.substring(0, 2)
                }
        ) {
			//　ログインするときの手続き定義
            appendAsLogon(builderName: "root") {
                talk(
						//ログイン完了した場合、テーブル一覧を取得する。
                        prepareLines: "select '######' || table_name || '######' from cat where substr(table_name, 1, 2) not in ('AQ', 'QT') order by 1;"
                ) {
					//"Host name:"を受信できるまで待ち、接続先ホスト名を送信
                    wait(~/Host name:/)
                    send(attr.hostname)
					//"Username:"を受信できるまで待ち、認証プロキシユーザIDを送信
                    wait(~/Username:/)
                    send("?{AGW_UID}")
					//"Password:"を受信できるまで待ち、認証プロキシユーザパスワードを送信
                    wait(~/Password:/)
                    password("?{AGW_PWD}")
					//"login:"か"Username:"を受信できるまで待ち、"login:"を受信できたら、OSユーザIDを送信
					//"Username:"を受信できたら、エラーで終了する。
					wait(~/login:/) {
                        error(~/Username: *$/, [error: "Audit gateway login failed with ?{AGW_UID}"])
                    }
                    send(attr.osUid)
					//"Password:"を受信できるまで待ち、OSユーザパスワードを送信
                    wait(~/Password:/)
                    send(attr.osPwd)
					//コマンドPromptを待ち、しかし、"Password:"を受けたら、エラーで終了する。
                    wait(~/\]\$/) {
                        error(~/^ *Password: *$/, [error: "OS login failed with aplusr01"])
                    }
					//SQLPLUSを起動する。
                    send("sqlplus -L ${attr.oraUid}/${attr.oraPwd}" as String)
					//コマンドPromptを待ち、着たら、ページサイズを送信する。
                    wait(~/^ *SQL> *$/)
                    send("set pagesize 10000")
					//コマンドPromptを待ち、着たら、行サイズを送信する。
                    wait(~/^ *SQL> *$/)
                    send("set linesize 10000")
					//コマンドPromptを待ち、着たら、項目分割文字を送信する。
                    wait(~/^ *SQL> *$/)
                    send("set colsep '<#>'")
					//コマンドPromptを待ち、着たら、ログインできるとする、次は子処理（テーブル一覧取得）
                    finish(~/^ *SQL> *$/, [result: TalkResult.FORWARD])
					//ログイン完了後、テーブル一覧の登録処理
                    regex(
                            matchedResult: TalkResult.CONTINUE,
                            pattern: ~/^######.*######$/,
                            processor: { GraphicSqlTerminal.controller.tableNameList << it[6..-9] })
					//ログイン完了後、テーブル一覧取得も終わって、コマンドPromptが出るを待ち、本手続き完了
                    regex(matchedResult: TalkResult.STOP, pattern: ~/^ *SQL> *$/)
                }
            }
			//　ログアウトするときの手続き定義
			// 特に何もないexit...exit...exit...exit...exit...exit...exit...exit...exit...exit...exit...
            appendAsLogoff(
                    builderName: "root",
                    prepareLines: ""
            ) {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    wait(~/^ *SQL> *$/)
                    send("exit")
                    wait(~/\$/)
                    send("exit")
                    send("exit")
                    finish(~/Host name:/, [result: TalkResult.STOP])
                }
            }
			// オラクルSelectコマンドを発行するときの結果処理
            appendAsSelect(builderName: "oracleSelect") {
				// タイトル処理コントロールを設定
                appendAsTitle(builderName:  "oracleTitle")
				// データ行処理コントロールを設定
                appendAsRowdata(builderName: "oracleRowdata")
				// コマンドPromptが出ると、データ全量取得完了とされる。
                regex(matchedResult: TalkResult.STOP,
                        pattern: ~/^ *SQL> *$/)
            }
			//　実行キャンセル処理
            appendAsCancel(builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
					//　Ctrl+Cを送る。
                    raw(ControlCode.CTRL_C)
                }
            }
			//	Select以外のコマンド実行処理
            appendAsExecute(builderName: "root") {
                talk {
					// コマンドPromptが出ると、実行完了とされる。
                    finish(~/^ *SQL> *$/, [result: TalkResult.STOP])
                }
            }

			//	エラー終了の場合の後方付け
            appendAsReset(prepareLines: "aaa", builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    raw(ControlCode.CTRL_D)
                    raw(ControlCode.CTRL_D)
                }
            }
        }
    }
}

static Closure getCreateSqlPlusConnectAsSysdbaClosure() {
    return { Map<String, Object> attr ->
        return (new ConfigBuilder()).database(name: attr.name,
                tableGroupTransfer: { String tableName ->
                    return tableName.substring(0, 2)
                }
        ) {
            appendAsLogon(builderName: "root") {
                talk(
                        prepareLines: "select '######' || table_name || '######' from cat where substr(table_name, 1, 2) not in ('AQ', 'QT') order by 1;"
                ) {
                    wait(~/Host name:/)
                    send(attr.hostname)
                    wait(~/Username:/)
                    send("?{AGW_UID}")
                    wait(~/Password:/)
                    password("?{AGW_PWD}")
                    wait(~/login:/) {
                        error(~/Username: *$/, [error: "Audit gateway login failed with ?{AGW_UID}"])
                    }
                    send(attr.osUid)
                    wait(~/Password:/)
                    send(attr.osPwd)
                    wait(~/\]\$/) {
                        error(~/^ *Password: *$/, [error: "OS login failed with aplusr01"])
                    }
                    send("ORACLE_SID=${attr.oraUid} sqlplus -L / as sysdba" as String)
                    wait(~/^ *SQL> *$/)
                    send("set pagesize 10000")
                    wait(~/^ *SQL> *$/)
                    send("set linesize 10000")
                    wait(~/^ *SQL> *$/)
                    send("set colsep '<#>'")
                    finish(~/^ *SQL> *$/, [result: TalkResult.FORWARD])
                    regex(
                            matchedResult: TalkResult.CONTINUE,
                            pattern: ~/^######.*######$/,
                            processor: { GraphicSqlTerminal.controller.tableNameList << it[6..-9] })
                    regex(matchedResult: TalkResult.STOP, pattern: ~/^ *SQL> *$/)
                }
            }
            appendAsLogoff(
                    builderName: "root",
                    prepareLines: ""
            ) {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    wait(~/^ *SQL> *$/)
                    send("exit")
                    wait(~/\$/)
                    send("exit")
                    send("exit")
                    finish(~/Host name:/, [result: TalkResult.STOP])
                }
            }
            appendAsSelect(builderName: "oracleSelect") {
                appendAsTitle(builderName:  "oracleTitle")
                appendAsRowdata(builderName: "oracleRowdata")
                regex(matchedResult: TalkResult.STOP,
                        pattern: ~/^ *SQL> *$/)
            }
            appendAsCancel(builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    raw(ControlCode.CTRL_C)
                }
            }

            appendAsExecute(builderName: "root") {
                talk {
                    finish(~/^ *SQL> *$/, [result: TalkResult.STOP])
                }
            }

            appendAsReset(prepareLines: "aaa", builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    raw(ControlCode.CTRL_D)
                    raw(ControlCode.CTRL_D)
                }
            }
        }
    }
}

static Closure getCreateMysqlConnectClosure() {
    return { Map<String, Object> attr ->
        return (new ConfigBuilder()).database(name: attr.name) {
            appendAsLogon(builderName: "root") {
                talk(
                        prepareLines: "show tables;"
                ) {
                    wait(~/Host name:/)
                    send(attr.hostname)
                    wait(~/Username:/)
                    send("?{AGW_UID}")
                    wait(~/Password:/)
                    password("?{AGW_PWD}")
                    wait(~/login:/) {
                        error(~/Username: *$/, [error: "Audit gateway login failed with ?{AGW_UID}"])
                    }
                    send(attr.osUid)
                    wait(~/Password:/)
                    send(attr.osPwd)
                    wait(~/\]\$/) {
                        error(~/^ *Password: *$/, [error: "OS login failed with aplusr01"])
                    }
                    send("LANG=ja_JP.SJIS mysql -u root redmine")
                    finish(~/MariaDB \[redmine\]> *$/, [result: TalkResult.FORWARD])
                    regex(
                            matchedResult: TalkResult.CONTINUE,
                            pattern: ~/\|.*\|\r?\n$/,
                            processor: {
                                String f = it.replaceAll(/^.*\| +([^ ]+) *\|\r?\n$/, '$1')
                                if (!f.startsWith("Tables_in_")) {
                                    GraphicSqlTerminal.controller.tableNameList << f
                                }
                            }
                    )
                    regex(matchedResult: TalkResult.STOP, pattern: ~/MariaDB \[redmine\]> *$/)
                }
            }
            appendAsLogoff(
                    builderName: "root",
                    prepareLines: ""
            ) {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    wait(~/^.*MariaDB \[redmine\]> *$/)
                    send("exit")
                    wait(~/\$/)
                    send("exit")
                    send("exit")
                    finish(~/Host name:/, [result: TalkResult.STOP])
                }
            }
            appendAsSelect(builderName: "mysqlSelect") {
                appendAsTitle(builderName:  "mysqlTitle")
                appendAsRowdata(builderName: "mysqlRowdata")
                regex(matchedResult: TalkResult.STOP,
                        pattern: ~/^.*MariaDB \[redmine\]> *$/)
            }
            appendAsCancel(builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    raw(ControlCode.CTRL_C)
                }
            }

            appendAsExecute(builderName: "root") {
                talk {
                    finish(~/^.*MariaDB \[redmine\]> *$/, [result: TalkResult.STOP])
                }
            }

            appendAsReset(prepareLines: "aaa", builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
                    raw(ControlCode.CTRL_D)
                    raw(ControlCode.CTRL_D)
                }
            }
        }
    }
}


