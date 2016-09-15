import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.ConfigBuilder
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.net.ControlCode

/**
 * �I���N��SQLPLUS��b�葱����`
 */
static Closure getCreateSqlPlusConnectClosure() {
    return { Map<String, Object> attr ->
        return (new ConfigBuilder()).database(name: attr.name,
                tableGroupTransfer: { String tableName ->
                    return tableName.substring(0, 2)
                }
        ) {
			//�@���O�C������Ƃ��̎葱����`
            appendAsLogon(builderName: "root") {
                talk(
						//���O�C�����������ꍇ�A�e�[�u���ꗗ���擾����B
                        prepareLines: "select '######' || table_name || '######' from cat where substr(table_name, 1, 2) not in ('AQ', 'QT') order by 1;"
                ) {
					//"Host name:"����M�ł���܂ő҂��A�ڑ���z�X�g���𑗐M
                    wait(~/Host name:/)
                    send(attr.hostname)
					//"Username:"����M�ł���܂ő҂��A�F�؃v���L�V���[�UID�𑗐M
                    wait(~/Username:/)
                    send("?{AGW_UID}")
					//"Password:"����M�ł���܂ő҂��A�F�؃v���L�V���[�U�p�X���[�h�𑗐M
                    wait(~/Password:/)
                    password("?{AGW_PWD}")
					//"login:"��"Username:"����M�ł���܂ő҂��A"login:"����M�ł�����AOS���[�UID�𑗐M
					//"Username:"����M�ł�����A�G���[�ŏI������B
					wait(~/login:/) {
                        error(~/Username: *$/, [error: "Audit gateway login failed with ?{AGW_UID}"])
                    }
                    send(attr.osUid)
					//"Password:"����M�ł���܂ő҂��AOS���[�U�p�X���[�h�𑗐M
                    wait(~/Password:/)
                    send(attr.osPwd)
					//�R�}���hPrompt��҂��A�������A"Password:"���󂯂���A�G���[�ŏI������B
                    wait(~/\]\$/) {
                        error(~/^ *Password: *$/, [error: "OS login failed with aplusr01"])
                    }
					//SQLPLUS���N������B
                    send("sqlplus -L ${attr.oraUid}/${attr.oraPwd}" as String)
					//�R�}���hPrompt��҂��A������A�y�[�W�T�C�Y�𑗐M����B
                    wait(~/^ *SQL> *$/)
                    send("set pagesize 10000")
					//�R�}���hPrompt��҂��A������A�s�T�C�Y�𑗐M����B
                    wait(~/^ *SQL> *$/)
                    send("set linesize 10000")
					//�R�}���hPrompt��҂��A������A���ڕ��������𑗐M����B
                    wait(~/^ *SQL> *$/)
                    send("set colsep '<#>'")
					//�R�}���hPrompt��҂��A������A���O�C���ł���Ƃ���A���͎q�����i�e�[�u���ꗗ�擾�j
                    finish(~/^ *SQL> *$/, [result: TalkResult.FORWARD])
					//���O�C��������A�e�[�u���ꗗ�̓o�^����
                    regex(
                            matchedResult: TalkResult.CONTINUE,
                            pattern: ~/^######.*######$/,
                            processor: { GraphicSqlTerminal.controller.tableNameList << it[6..-9] })
					//���O�C��������A�e�[�u���ꗗ�擾���I����āA�R�}���hPrompt���o���҂��A�{�葱������
                    regex(matchedResult: TalkResult.STOP, pattern: ~/^ *SQL> *$/)
                }
            }
			//�@���O�A�E�g����Ƃ��̎葱����`
			// ���ɉ����Ȃ�exit...exit...exit...exit...exit...exit...exit...exit...exit...exit...exit...
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
			// �I���N��Select�R�}���h�𔭍s����Ƃ��̌��ʏ���
            appendAsSelect(builderName: "oracleSelect") {
				// �^�C�g�������R���g���[����ݒ�
                appendAsTitle(builderName:  "oracleTitle")
				// �f�[�^�s�����R���g���[����ݒ�
                appendAsRowdata(builderName: "oracleRowdata")
				// �R�}���hPrompt���o��ƁA�f�[�^�S�ʎ擾�����Ƃ����B
                regex(matchedResult: TalkResult.STOP,
                        pattern: ~/^ *SQL> *$/)
            }
			//�@���s�L�����Z������
            appendAsCancel(builderName: "root") {
                talk(
                        unlessResult: TalkResult.STOP
                ) {
					//�@Ctrl+C�𑗂�B
                    raw(ControlCode.CTRL_C)
                }
            }
			//	Select�ȊO�̃R�}���h���s����
            appendAsExecute(builderName: "root") {
                talk {
					// �R�}���hPrompt���o��ƁA���s�����Ƃ����B
                    finish(~/^ *SQL> *$/, [result: TalkResult.STOP])
                }
            }

			//	�G���[�I���̏ꍇ�̌���t��
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


