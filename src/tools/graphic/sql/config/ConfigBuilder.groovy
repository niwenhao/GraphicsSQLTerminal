package tools.graphic.sql.config

import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.gui.UserInterface
import tools.graphic.sql.talk.builder.SendAndWaitTalkable
import tools.graphic.sql.talk.builder.talks.*
import tools.graphic.sql.talk.context.RegexMatchLinesTalkable
import tools.graphic.sql.talk.context.RootTalkable
import tools.graphic.sql.talk.context.mysql.MysqlRowMatcher
import tools.graphic.sql.talk.context.mysql.MysqlSelectTalkable
import tools.graphic.sql.talk.context.mysql.MysqlTitleMatcher
import tools.graphic.sql.talk.context.oracle.SqlPlusRowMatcher
import tools.graphic.sql.talk.context.oracle.SqlPlusSelectTalkable
import tools.graphic.sql.talk.context.oracle.SqlPlusTitleMatcher
import tools.graphic.sql.util.VariableUtils

/**
 * 設定ファイルとして存在するコンポーネットビルド
 *
 * Created by nwh on 2016/01/04.
 */
class ConfigBuilder extends BuilderSupport {
    /**
     * ビルド名とクラスのマッピング
     */
    static final Map<String, Class<Buildable>> buildables = [:]
    /**
     * buildables の初期化
     */
    static {
        buildables.root = RootTalkable
        buildables.database = DBConnection
        buildables.gst = GraphicSqlTerminal
        buildables.send = Send
        buildables.password = Password
        buildables.wait = Wait
        buildables.finish = EndPoint
        buildables.error = Failure
        buildables.raw = RawSend
        buildables.ui = UserInterface
        buildables.oracleSelect = SqlPlusSelectTalkable
        buildables.oracleTitle = SqlPlusTitleMatcher
        buildables.oracleRowdata = SqlPlusRowMatcher
        buildables.mysqlSelect = MysqlSelectTalkable
        buildables.mysqlTitle = MysqlTitleMatcher
        buildables.mysqlRowdata = MysqlRowMatcher
        buildables.regex = RegexMatchLinesTalkable
        buildables.talk = SendAndWaitTalkable
    }

    /**
     * ビルドのツリー関連をメンテする
     * 最後に、親コンポーネントにsetupChildメソッドをび出す。
     *
     * @param parent
     * @param child
     */
    @Override
    protected void setParent(Object parent, Object child) {
        Buildable p = (Buildable) parent
        Buildable c = (Buildable) child
        p.childrenBuildables << c
        c.parentBuildable = p
        p.setupChild(c)
    }

    /**
     * パラメータがないビルドコンポーネントを作成する。
     *
     * @param name
     * @return
     */
    @Override
    protected Object createNode(Object name) {
        return createNode(name, [:])
    }

    /**
     * バリューのみビルドコンポーネットを成する。
     * 実装上、バリューもプロパティにいれって処理単純化している。
     * @param name
     * @param value
     * @return
     */
    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, [value: value])
    }

    /**
     * プロパティを提供したビルドコンポーネット作成
     * 処理概要：
     * １．ビルド名称はappendAsでまる場合、appendAsをいて、ビルド名称して使う。
     * ２．プロパティのbuilderNameが定された場合、ビルド名称はbuilderNameで決め、コンポーネットクラスを探し出す。
     * ３．バリューが供された場合、バリューはコンポーネントのものとする。
     *
     * @param name
     * @param attributes
     * @return
     */
    @Override
    protected Object createNode(Object name, Map attributes) {
        Class<Buildable> cls = null
        Buildable comp = null
        if (name.startsWith("appendAs")) {
            name = name.substring(8, 9).toLowerCase() + name.substring(9)
            if (attributes.builderName) {
                cls = buildables[attributes.builderName]
            } else {
                comp = attributes.value
            }
        } else {
            cls = buildables[name]
        }
        if (cls == null && comp == null) {
            throw new RuntimeException("build <${name}> failed. attribute = ${attributes}")
        }

        if (comp == null) {
            comp = cls.newInstance()
        }
        comp.buildableName = name
        VariableUtils.applyAttribute(comp, attributes)
        comp.setupAttribute(attributes)
        return comp
    }

    /**
     * バリューとプロパティ両方提供されたビルドコンポーネント作成。
     * 処理概要
     * バリューをプロパティにマージしてから一般処理を行う。
     * @param name
     * @param attributes
     * @param value
     * @return
     */
    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        Map myattr = attributes.clone()
        myattr.value = value
        return createNode(name, myattr)
    }
}
