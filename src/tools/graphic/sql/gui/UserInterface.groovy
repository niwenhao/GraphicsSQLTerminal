package tools.graphic.sql.gui

import groovy.swing.SwingBuilder
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl
import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.Buildable
import tools.graphic.sql.config.Log

import javax.swing.*
import javax.swing.table.TableColumn
import javax.swing.table.TableModel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreePath
import java.awt.*
import java.awt.event.ActionEvent
import java.util.List
import java.util.Timer
import java.util.regex.Matcher

/**
 * ユーザインタフェース
 *
 * Created by nwh on 2015/12/24.
 */
class UserInterface implements Buildable {

    //実行中のスレッド
    private Thread executingThread

    private List<String> sqlHistory = null
    private int sqlHistoryIndex = 0

    /**
     * 事前定義SQLツリーのテキスト
     */
    enum SqlAssistBranches {
        ROOT("SQLアシスタント"), TABLES("テーブル一覧"), COMMON_SQLS("共通SQL定義"), PRIVATED_SQLS("個人SQL定義")

        String description

        public SqlAssistBranches(s) {
            this.description = s
        }

        public String toString() {
            return description
        }
    }

    /**
     * ビルド
     */
    static SwingBuilder gbld = new SwingBuilder()

    /**
     * UserインタフェースLook&feel設定
     */
    static {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    }

    /**
     * 一般的なDefaultMutableTreeNodeを拡張し、userObjectのほか値をもつ
     * userObjectは名称になり、値は実のSQL文
     */
    public static class ValuedDefaultMutableTreeNode extends DefaultMutableTreeNode {
        def value = null

        ValuedDefaultMutableTreeNode(Object userObject, Object value) {
            super(userObject)
            this.value = value
        }
    }

    private JComboBox cmbDatabases
    private JTextField txtAgwUid
    private JTextField txtAgwPwd
    private JButton btnConnect
    private JCheckBox chkRawCommand
    private JButton btnExecute
    private JTextArea txtSql
    private JTabbedPane tabResultPane
    private JScrollPane scrDataPane
    private JTable tblDataView
    private JComboBox cmbLogLines
    private LogConsole logConsole
    private JScrollPane scrLogPane
    private JFrame mainFrame
    private JTree sqlAssistTree
    private JTextField txtTransTimeout
    private Map<String, String> embVarMap = [:]
    private Timer timeoutTimer = new Timer()
    private TimerTask resetTask = null
    private long resetTime = System.currentTimeMillis()

    File historyFile = null
    private void loadSqlHistory(String uid) {
        historyFile = new File("history" + File.separator + uid + ".gsqlplus.history")
        if (historyFile.exists()) {
            historyFile.withObjectInputStream {
                sqlHistory = it.readObject()
            }
        } else {
            sqlHistory = new ArrayList<String>()
        }

        sqlHistoryIndex = sqlHistory.size()
    }

    private void saveSqlHistory() {
        if (historyFile) {
            if (historyFile.exists()) {
                historyFile.delete()
            }
            historyFile.withObjectOutputStream {
                it.writeObject(sqlHistory)
            }
        }
    }

    private void appendHistory(String sql) {
        if (historyFile) {
            String lastSql = sqlHistory.size() > 0 ? sqlHistory.last() : null
            if (sql != lastSql) {
                sqlHistory = sqlHistory.grep { it != sql }
                sqlHistory << sql
                if (sqlHistory.size() > 1000) {
                    sqlHistory.removeAt(0)
                }
                saveSqlHistory()
            }

            sqlHistoryIndex = sqlHistory.size()
        }
    }

    /**
     * 画面の初期表示時、事前定義SQLツリーを初期化する。
     *
     * @param tableList テーブル一覧
     */
    private void initSqlAssistTree(List<String> tableList) {
        DefaultMutableTreeNode root = sqlAssistTree.model.root
        root.removeAllChildren()

        //テーブル一覧を初期化する
        try {
            DefaultMutableTreeNode tableBranch = new DefaultMutableTreeNode(SqlAssistBranches.TABLES)
            root.add(tableBranch)
            String sys = null
            DefaultMutableTreeNode sysBranch = null

            Closure groupTransfer = GraphicSqlTerminal.controller.getTableGroupTransfer(cmbDatabases.selectedItem)

            tableList.each { String t ->
                String s = groupTransfer.call(t)
                if (sys != s) {
                    sys = s
                    sysBranch = new DefaultMutableTreeNode(sys)
                    tableBranch.add(sysBranch)
                }
                sysBranch.add(new DefaultMutableTreeNode(t))
            }
        } finally {
        }

        //事前定義SQLツリーを初期化するルーチン
        def initSqlBranch = { String sqlType ->
            DefaultMutableTreeNode sqlBranch = new DefaultMutableTreeNode(SqlAssistBranches."${sqlType}_SQLS")
            root.add(sqlBranch)

            Map<String, String> sqls = GraphicSqlTerminal.controller.getDefinedScript(SqlPrivilege."${sqlType}")

            sqls.keySet().sort().each {
                String sql = sqls."${it}"
                sqlBranch.add(new ValuedDefaultMutableTreeNode(it, sql))
            }
        }

        //共用SQLツリーを初期化する。
        initSqlBranch("COMMON")
        //ユーザSQLツリーを初期化する。
        initSqlBranch("PRIVATED")

        //事前定義SQLツリーを再表示
        sqlAssistTree.model.reload()
    }

    /**
     * 画面表示
     */
    public void show() {
        this.initializeUI()
        mainFrame.visible = true
    }

    /**
     * 事前定義SQLツリーをクリア
     */
    private void clearSqlAssistTree() {
        sqlAssistTree.model.root.removeAllChildren()
        sqlAssistTree.model.reload()
    }

    /**
     * データ表示テーブルコントロールの列を初期化する。
     *
     * @param fieldMetaList 項目定義一覧
     */
    private void initDataTableTitle(List<FieldMeta> fieldMetaList) {
        tblDataView.model.rowCount = 0
        tblDataView.model.columnCount = fieldMetaList.size()
		
		Map<String, String> fieldMap = GraphicSqlTerminal.controller.getFieldNameMapping()

        fieldMetaList.eachWithIndex { FieldMeta meta, int idx ->
            TableColumn tc = tblDataView.columnModel.getColumn(idx)
			String name = fieldMap?.get(meta.name.replaceAll(~/[ \t]*$/, ""))
			name = (name == null ? meta.name : name)
			int len = name.bytes.size()
            tc.headerValue = name
            tc.minWidth = meta.minLength * 9 + 10
            tc.maxWidth = (meta.maxLength > len ? meta.maxLength : len) * 9 + 10
            tc.preferredWidth = tc.minWidth
        }

        tblDataView.doLayout()
    }

    /**
     * データ表示テーブルコントロールの行を追加する。
     * @param fieldList 項目データ一覧
     */
    private void appendDataTableRow(List<String> fieldList) {
        tblDataView.model.addRow(fieldList.toArray())
    }

    /**
     * SQLのEmbed変数を入れ替える。
     *
     * @param sql 変更前のSQL
     * @return 変更後のSQL
     */
    private String replaceVariables(String sql) {
        String retSql = sql
        Matcher mch = (sql =~ /&([\w_]+)/)
        Map<String, String> curVars = [:]
        Map<String, JTextField> fields = [:]
        if (mch.find()) {
            mch.reset()
            while (mch.find()) {
                String k = mch.group().substring(1)
                String v = embVarMap[k] == null ? "" : embVarMap[k]
                curVars[k] = v
            }

            JDialog dialog = null
            dialog = gbld.dialog(title: "Embed Variables", modal: true, size: [400, 300], owner: mainFrame) {
                Action _actExecute = action(name: "実行") { ActionEvent av ->
                    fields.each { String k, JTextField field ->
                        curVars[k] = field.text
                    }
                    embVarMap = curVars
                    curVars.each { String k, String v ->
                        retSql = retSql.replaceAll(/&${k}/, v)
                    }
                    dialog.dispose()
                }
                Action _actCancel = action(name: "キャンセル") {
                    retSql = null
                    dialog.dispose()
                }
                borderLayout()
                panel(constraints: BorderLayout.CENTER) {
                    gridLayout(rows: curVars.size(), columns: 2)
                    curVars.each { String k, String v ->
                        label(text: k)
                        fields[k] = textField(text: v, columns: 40)
                    }
                }
                panel(constraints: BorderLayout.SOUTH) {
                    flowLayout(align: FlowLayout.CENTER)
                    button(action: _actExecute)
                    button(action: _actCancel)
                }
            }
            dialog.pack()
            dialog.visible = true
        }

        return retSql
    }

    /**
     * 事前定義SQLツリーにクリックした場合の処理プログラム
     */
    private Closure sqlAssistSelected = { ev ->
        if (ev.clickCount == 2) {
            TreePath path = sqlAssistTree.selectionModel.selectionPath
            if (path?.size() > 2) {
                Log.debug("Selected path -> ${path}")
                if (path.getPathComponent(1).userObject == SqlAssistBranches.TABLES && path.pathCount == 4) {
                    txtSql.text = GraphicSqlTerminal.controller.getTableSelectSqlTransfer(cmbDatabases.selectedItem).call(path.lastPathComponent.userObject)
                }
                if (path.getPathComponent(1).userObject == SqlAssistBranches.COMMON_SQLS && path.pathCount == 3) {
                    txtSql.text = path.lastPathComponent.value
                }
                if (path.getPathComponent(1).userObject == SqlAssistBranches.PRIVATED_SQLS && path.pathCount == 3) {
                    txtSql.text = path.lastPathComponent.value
                }
            }
        }
    }

    /**
     * 接続ボタンを押下した場合の処理プログラム
     */
    private Action actConnect = gbld.action(name: 'Connect') { ev ->
        Thread.start() {
            try {
                ev.source.enabled = false
                tabResultPane.selectedIndex = 1
                ev.source.enabled = false

                ConnectResult result = GraphicSqlTerminal.controller.connect(cmbDatabases.selectedItem, txtAgwUid.text, txtAgwPwd.text)

                Log.debug(result)

                if (result.resultCode == ResultCode.SUCCESS) {
                    loadSqlHistory(txtAgwUid.text)
                    initSqlAssistTree(result.tables)
                    ev.source.action = actDisconnect
                    btnExecute.enabled = true
                    txtAgwUid.editable = false
                    txtAgwPwd.editable = false
                    txtTransTimeout.text = GraphicSqlTerminal.controller.transTimeoutSecond
                    resetIdleTimer(true)
                }
            } finally {
                ev.source.enabled = true
            }
        }
    }

    /**
     * 切断ボタンを押下した場合の処理プログラム
     */
    private Action actDisconnect = gbld.action(name: 'Disconnect') { ev ->
        Thread.start() {
            historyFile = null
            try {
                tabResultPane.selectedIndex = 1
                ev.source.enabled = false

                ConnectResult result = GraphicSqlTerminal.controller.disconnect(cmbDatabases.selectedItem)
                if (result.resultCode == ResultCode.SUCCESS) {
                    clearSqlAssistTree()

                    tblDataView.model.rowCount = 0
                    txtAgwUid.text = GraphicSqlTerminal.controller.defaultAgwUser
                    txtAgwPwd.text = GraphicSqlTerminal.controller.defaultAgwPassword
                    ev.source.action = actConnect
                    btnExecute.enabled = false
                    txtAgwUid.editable = true
                    txtAgwPwd.editable = true
                    resetIdleTimer(false)
                }
            } finally {
                ev.source.enabled = true
            }
        }
    }

    /**
     * 実行ボタンを押下した場合の処理プログラム
     */
    private Action actExecute = gbld.action(name: '   Execute   ') { ev ->
        executingThread = Thread.start() {
            tabResultPane.selectedIndex = 1

            TimerTask transTimeoutTask = null

            try {
                // データ表示テーブルの内容を消す
                tblDataView.model.rowCount = 0
                String sql = txtSql.text

                appendHistory(sql)

                //SQLのembed変数を入れ替える。
                sql = replaceVariables(sql)

                //実行タイムアウトでタイムアウトタイマーを作成、起動する。
                transTimeoutTask = timeoutTimer.runAfter(Integer.parseInt(txtTransTimeout.text) * 1000) {
                    if (btnExecute.enabled) {
                        ActionEvent av = new ActionEvent(btnConnect, ActionEvent.ACTION_PERFORMED, "Disconnect")
                        actCancel.actionPerformed(av)
                        JOptionPane.showMessageDialog(null, "SQL実行はタイムアウトでキャンセルしました。")
                    }
                }

                //SQLを実行する。
                if (sql) {
                    if (chkRawCommand.selected) {
                        GraphicSqlTerminal.controller.execute(cmbDatabases.selectedItem, sql) { String line ->
                            println line
                        }
                    } else {
                        GraphicSqlTerminal.controller.select(cmbDatabases.selectedItem, sql) { SelectResult sr ->
                            Log.debug("sr: ${sr}")
                            switch (sr.statusCode) {
                                case ResultCode.FETCHED_TITLE:
                                    initDataTableTitle(sr.fieldMetaList)
                                    break
                                case ResultCode.FETCHED_ROW:
                                    appendDataTableRow(sr.fieldList)
                                    break
                                default:
                                    throw new RuntimeException("Unexpected status")
                            }
                        }
                    }
                }

                if (!chkRawCommand.selected) {
                    tblDataView.doLayout()
                    tabResultPane.selectedIndex = 0
                }
            } finally {
                ev.source.action = actExecute
                executingThread = null
                transTimeoutTask?.cancel()
            }
        }
        ev.source.action = actCancel
    }

    /***
     * キャンセルボタンを押下した場合の処理プログラム
     */
    private Action actCancel = gbld.action(name: 'Cancel') { ev ->
        tabResultPane.selectedIndex = 1

        Thread.start {
            GraphicSqlTerminal.controller.cancel(cmbDatabases.selectedItem)
            btnExecute.action = actExecute
        }
    }

    /***
     * 取得データをCSVに保存する。
     */
    private Action actSaveRowsToCSV = gbld.action(name: "Save as CSV file") {
        JFileChooser chooser = gbld.fileChooser(dialogTitle: "Select a csv",
                fileSelectionMode: JFileChooser.FILES_ONLY,
                multiSelectionEnabled: false)
        if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File csv = chooser.selectedFile

            csv.withPrintWriter() { writer ->
                writer << "\"" << tblDataView.columnModel.getColumn(0).headerValue.replaceAll(/\"/, "\"") << "\""
                for (int c = 1; c < tblDataView.model.columnCount; c++) {
                    writer << ",\"" << tblDataView.columnModel.getColumn(c).headerValue.replaceAll(/\"/, "\"") << "\""
                }
                writer << "\r\n"

                for (int r = 0; r < tblDataView.model.rowCount; r++) {
                    writer << "\"" << tblDataView.model.getValueAt(r, 0) << "\""
                    if (tblDataView.model.columnCount > 1) {
                        for (int c = 1; c < tblDataView.model.columnCount; c++) {
                            writer << ",\"" << tblDataView.model.getValueAt(r, c).replaceAll(/\"/, "\"") << "\""
                        }
                    }
                    writer << "\r\n"
                }
            }
        }

    }

    /***
     * ログテキストをふぃあるに保存する。
     */
    private Action actSaveLog = gbld.action(name: "Save log") {
        JFileChooser chooser = gbld.fileChooser(dialogTitle: "Select a csv",
                fileSelectionMode: JFileChooser.FILES_ONLY,
                multiSelectionEnabled: false)
        if (chooser.showOpenDialog(this.mainFrame) == JFileChooser.APPROVE_OPTION) {
            File log = chooser.selectedFile

            log.withPrintWriter() { writer ->
                Log.logConsole.lines.each { l ->
                    writer.println(l)
                }
            }
        }
    }

    /***
     * 事前定義SQLを削除する。
     *
     * @param path 選択されたノードのパス
     */
    private void removePredefinedSql(TreePath path) {
        Log.debug("""
removePredefinedSql
    path: ${path}
""")
        DefaultMutableTreeNode branch = path.parentPath.lastPathComponent
        ValuedDefaultMutableTreeNode leaf = path.lastPathComponent
        if (branch.userObject == SqlAssistBranches.COMMON_SQLS) {
            GraphicSqlTerminal.controller.removeSql(SqlPrivilege.COMMON, leaf.userObject)
            branch.remove(leaf)
        } else {
            GraphicSqlTerminal.controller.removeSql(SqlPrivilege.PRIVATED, leaf.userObject)
            branch.remove(leaf)
        }
        sqlAssistTree.model.reload()
        sqlAssistTree.selectionPath = new TreePath(path.parentPath)
        JOptionPane.showMessageDialog(mainFrame, "事前定義SQLを削除しました。")
    }

    /***
     * 事前定義SQLを追加する
     *
     * @param path 選択されたノードのパス
     * @param sql 追加SQL
     */
    private void updatePredefinedSql(TreePath path, String sql) {
        Log.debug("""
updatePredefinedSql
    path: ${path}
    sql:  ${sql}
""")
        DefaultMutableTreeNode branch = path.parentPath.lastPathComponent
        ValuedDefaultMutableTreeNode leaf = path.lastPathComponent
        if (branch.userObject == SqlAssistBranches.COMMON_SQLS) {
            GraphicSqlTerminal.controller.updateSql(SqlPrivilege.COMMON, leaf.userObject, sql)
            leaf.value = sql
        } else {
            GraphicSqlTerminal.controller.updateSql(SqlPrivilege.PRIVATED, leaf.userObject, sql)
            leaf.value = sql
        }
        sqlAssistTree.selectionPath = new TreePath(path)
        sqlAssistTree.model.reload()
        JOptionPane.showMessageDialog(mainFrame, "事前定義SQLを更新しました。")
    }

    /***
     * 事前定義SQLを更新する
     *
     * @param path 選択されたノードのパス
     * @param sql 更新SQL
     */
    private void appendPredefinedSql(TreePath path, String sql) {
        Log.debug("""
appendPredefinedSql
    path: ${path}
    sql:  ${sql}
""")
        String name = JOptionPane.showInputDialog(null, "保存するSQL名を入力してください。", "")
        DefaultMutableTreeNode branch = path.lastPathComponent
        ValuedDefaultMutableTreeNode leaf = new ValuedDefaultMutableTreeNode(name, sql)
        branch.add(leaf)
        if (branch.userObject == SqlAssistBranches.COMMON_SQLS) {
            GraphicSqlTerminal.controller.appendSql(SqlPrivilege.COMMON, name, sql)
        } else {
            GraphicSqlTerminal.controller.appendSql(SqlPrivilege.PRIVATED, name, sql)
        }
        sqlAssistTree.model.reload()
        sqlAssistTree.selectionPath = new TreePath(path)
        JOptionPane.showMessageDialog(mainFrame, "事前定義SQLを追加しました。")
    }

    /***
     * 事前定義SQLツリーのSQLノードのコンテキストメニュー
     */
    private JPopupMenu sqlAssistPopupForLeaf = gbld.popupMenu() {
        menuItem action(name: '事前定義SQL削除') { ev ->
            removePredefinedSql(sqlAssistTree.selectionPath)
        }
        menuItem action(name: '事前定義SQL保存') { ev ->
            updatePredefinedSql(sqlAssistTree.selectionPath, txtSql.text)
        }
    }
    /***
     * 事前定義SQLツリーのSQLブランチのコンテキストメニュー
     */
    private JPopupMenu sqlAssistPopupForBranch = gbld.popupMenu() {
        menuItem action(name: '事前定義SQL新規保存') { ev ->
            appendPredefinedSql(sqlAssistTree.selectionPath, txtSql.text)
        }
    }

    /***
     * マウス、またはキーボード操作で無操作タイムアウトタイマーをリセットする。
     *
     * @param restart 再スタートするか、しない場合、タイマーを停止するのみ
     */
    void resetIdleTimer(restart) {
        if (resetTask) {
            resetTask.cancel()
            resetTask = null
        }
        if (restart) {
            resetTask = timeoutTimer.runAfter(GraphicSqlTerminal.controller.timeoutSecond * 1000) {
                if (btnExecute.enabled) {
                    ActionEvent av = new ActionEvent(btnConnect, ActionEvent.ACTION_PERFORMED, "Disconnect")
                    actDisconnect.actionPerformed(av)
                }
            }
            resetTime = System.currentTimeMillis()
        }
    }

    /***
     * コンポーネットおよびサブコンポーネットのマウスとキーボードイベントハンドルを設定する。
     *
     * @param comp
     */
    void appendResetEvents(comp) {
        final Closure resetHandler = { ev ->
            //Log.debug("mouseMoved/keyPressed in ${ev.source}")
            if (resetTask) {
                if (System.currentTimeMillis() - resetTime > 1000) {
                    resetIdleTimer(true)
                }
            }
        }

        try {
            comp.mouseMoved = resetHandler
            comp.keyPressed = resetHandler
        } catch (e) {
        }

        if (comp instanceof Container) {
            comp.components.each { appendResetEvents(it) }
        }
    }

    /***
     * 画面コンポーネントを作成する。
     */
    private void initializeUI() {
        JFrame.metaClass.orgsql = ""
        gbld.edt {
            //メインウインドウ
            mainFrame = frame(title: 'Oracleシェル', size: [1500, 900], show: false, defaultCloseOperation: JFrame.EXIT_ON_CLOSE, font: UsedFonts.NORMAL) {
                borderLayout()
                //接続関連パネル
                panel(constraints: BorderLayout.NORTH) {
                    gridLayout(rows: 3, cols: 1)
                    //タイトル
                    panel() {
                        flowLayout(alignment: FlowLayout.LEFT)
                        label(text: '本番端末室内 オラクルシェル', font: UsedFonts.TITLE)
                    }
                    //接続先選択パネル
                    panel() {
                        flowLayout(alignment: FlowLayout.LEFT)
                        label(text: '接続先')
                        cmbDatabases = comboBox(model: new DefaultComboBoxModel<String>(GraphicSqlTerminal.controller.databaseNames.sort().toArray(new String[GraphicSqlTerminal.controller.databaseNames.size()])),
                                preferredSize: [400, 25], font: UsedFonts.BOLD)
                    }
                    //ログインパネル
                    panel() {
                        flowLayout(alignment: FlowLayout.LEFT)
                        label(text: 'AGW UID')
                        txtAgwUid = textField(columns: 20, text: GraphicSqlTerminal.controller.defaultAgwUser)
                        label(text: 'AGW Password')
                        txtAgwPwd = passwordField(columns: 20, text: GraphicSqlTerminal.controller.defaultAgwPassword)
                        btnConnect = button(action: actConnect)
                        chkRawCommand = checkBox(text: "コマンドそのまま(PL/SQLなど)",
                                selected: false,
                                visible: GraphicSqlTerminal.controller.rawSqlEnabled)
                        label(text: '実行タイムアウト')
                        txtTransTimeout = textField(columns: 8)
                        label(text: " ")
                        btnExecute = button(action: actExecute, enabled: false)
                        label(text: " ")
                        button(text: "SQLフォーマット", actionPerformed: { ev ->
                            String s = txtSql.text
                            ArrayList<String> cmds = new ArrayList<String>()
                            StringBuilder sb = new StringBuilder()
                            s.eachLine { ln ->
                                if (sb.size() > 0) {
                                    sb.append(ln).append("\n")
                                } else {
                                    if (ln.startsWith("+ ")) {
                                        cmds << ln
                                    } else {
                                        sb.append(ln).append("\n")
                                    }
                                }
                            }
                            StringBuilder sql = new StringBuilder()
                            if (cmds.size() > 0) {
                                sql.append(cmds.join("\n")).append("\n")
                            }
                            //sql.append(new BasicFormatterImpl().format(sb.toString()).replace("\n    ", "\n").substring(1))
                            sql.append(new BasicFormatterImpl().format(sb.toString()).replace("\n    ", "\n").substring(1))
                            txtSql.text = sql.toString()
                        })
                        button(text: "<", actionPerformed: { ev ->
                            if (sqlHistoryIndex > 0) {
                                sqlHistoryIndex --
                                txtSql.text = sqlHistory[sqlHistoryIndex]
                            }
                        })
                        button(text: ">", actionPerformed: { ev ->
                            if (sqlHistoryIndex < sqlHistory.size() - 1) {
                                sqlHistoryIndex ++
                                txtSql.text = sqlHistory[sqlHistoryIndex]
                            }
                        })
                        button(text:"？", actionPerformed: { ev ->
                            "explorer doc\\README.html".execute()
                        })
                    }

                }
                splitPane(orientation: JSplitPane.HORIZONTAL_SPLIT, dividerLocation: 300) {
                    scrollPane() {
                        sqlAssistTree = tree(mouseClicked: sqlAssistSelected,
                                mouseReleased: { ev ->
                                    if (btnExecute.enabled && ev.popupTrigger) {
                                        ev.component.selectionPath = ev.component.getPathForLocation(ev.x, ev.y)
                                        def path = ev.component.selectionPath.path
                                        if (path.length > 1 && path[1].userObject != SqlAssistBranches.TABLES) {
                                            if (path.length == 3) {
                                                sqlAssistPopupForLeaf.show(ev.component, ev.x, ev.y)
                                            } else {
                                                sqlAssistPopupForBranch.show(ev.component, ev.x, ev.y)
                                            }
                                        }
                                    }
                                })
                        ((DefaultMutableTreeNode) sqlAssistTree.model.root).removeAllChildren()
                        ((DefaultMutableTreeNode) sqlAssistTree.model.root).userObject = SqlAssistBranches.ROOT
                        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer()
                        renderer.openIcon = new ImageIcon(Thread.currentThread().contextClassLoader.getResource("open_branch.png"))
                        renderer.closedIcon = new ImageIcon(Thread.currentThread().contextClassLoader.getResource("closed_branch.png"))
                        renderer.leafIcon = new ImageIcon(Thread.currentThread().contextClassLoader.getResource("leaf.png"))
                        sqlAssistTree.setRowHeight(21)
                        sqlAssistTree.cellRenderer = renderer
                        sqlAssistTree.model.reload()
                    }
                    //メインパネル、このパネルは上下二つで分ける
                    splitPane(orientation: JSplitPane.VERTICAL_SPLIT, dividerLocation: 300) {
                        //SQL入力パネル
                        panel() {
                            borderLayout()
                            scrollPane(constraints: BorderLayout.CENTER) {
                                //SQL入力エリア
                                txtSql = textArea(constraints: BorderLayout.CENTER)
                            }
                        }

                        //実行結果表示パネル
                        tabResultPane = tabbedPane() {
                            //データ表示パネル
                            panel(name: "Grid View") {
                                borderLayout()
                                scrDataPane = scrollPane() {
                                    tblDataView = table(autoResizeMode: JTable.AUTO_RESIZE_OFF, cellSelectionEnabled: true)
                                }

                                panel(constraints: BorderLayout.SOUTH) {
                                    flowLayout(alignment: FlowLayout.LEFT)
                                    button(action: actSaveRowsToCSV)
                                    button(text: "New Window", actionPerformed: { ev ->
                                        JTable tbl = null
                                        gbld.edt {
                                            String s = sqlHistory.last()

                                            s = s.replaceAll(/\r?\n/, " ")
                                            s = s.replaceAll(/\t/, " ")
                                            s = s.replaceAll(/ +/, " ")
                                            JFrame fm = frame(title: s, size: [640, 480], show: true, defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE, font: UsedFonts.NORMAL) {
                                                borderLayout()

                                                panel(constraints: BorderLayout.NORTH) {
                                                    flowLayout(alignment: FlowLayout.RIGHT)
                                                    button(text: "Re execute", actionPerformed: { e ->
                                                        txtSql.text = e.source.rootPane.parent.orgsql
                                                    })
                                                }
                                                
                                                scrollPane() {
                                                    tbl = table(autoResizeMode: JTable.AUTO_RESIZE_OFF, cellSelectionEnabled: true)
                                                }
                                            }
                                            fm.orgsql = sqlHistory.last()
                                        }

                                        tbl.model.rowCount = tblDataView.model.rowCount
                                        tbl.model.columnCount = tblDataView.model.columnCount
                                        for (int i = 0; i < tbl.model.columnCount; i ++) {
                                            TableColumn tc = tbl.columnModel.getColumn(i)
                                            TableColumn otc = tblDataView.columnModel.getColumn(i)
                                            tc.headerValue = otc.headerValue
                                            tc.minWidth = otc.minWidth
                                            tc.maxWidth = otc.maxWidth
                                            tc.preferredWidth = otc.preferredWidth
                                        }

                                        tbl.doLayout()
                                        TableModel tm = tbl.model
                                        TableModel otm = tblDataView.model

                                        for(int r = 0; r < tm.rowCount; r ++) {
                                            for(int c = 0; c < tm.columnCount; c ++) {
                                                tm.setValueAt(otm.getValueAt(r, c), r, c)
                                            }
                                        }
                                    })
                                }
                            }
                            panel(name: "Console View") {
                                borderLayout()
                                panel(constraints: BorderLayout.SOUTH) {
                                    logConsole = new LogConsole(lineHeight: 15,
                                            width: 1800,
                                            maxLines: 1000,
                                            font: UsedFonts.NORMAL
                                    )
                                    Log.logConsole = logConsole
                                    flowLayout(alignment: FlowLayout.LEFT)
                                    logConsole.enableLogCheckBox = checkBox(text: "Console output", selected: true)
                                    logConsole.traceTailCheckBox = checkBox(text: "Trace tail of log", selected: true)
                                    button(text: 'Clear console',
                                            actionPerformed: { ev -> logConsole.clear(); Log.info("Log cleared .........................\n") })
                                    label(text: '    ログ行数(後ろから)')
                                    cmbLogLines = comboBox(
                                            editable: false,
                                            preferredSize: new java.awt.Dimension(200, 25),
                                            model: new DefaultComboBoxModel<String>(new Vector(['すべて', '100', '1000', '5000', '10000', '100000'])),
                                            selectedIndex: 2,
                                            actionPerformed: { e ->
                                                String k = cmbLogLines.selectedItem
                                                if (k == 'すべて') {
                                                    logConsole.maxLines = -1
                                                } else {
                                                    logConsole.maxLines = Integer.valueOf(k)
                                                }
                                            }
                                    )
                                    button(action: actSaveLog)
                                }
                                //ログ表示パネル
                                scrLogPane = scrollPane(viewportView: logConsole)
                            }
                        }

                    }
                }
            }
        }
        appendResetEvents(mainFrame)
    }
}

