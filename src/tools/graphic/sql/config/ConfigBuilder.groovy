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
 * �ݒ�t�@�C���Ƃ��đ��݂���R���|�[�l�b�g�r���h
 *
 * Created by nwh on 2016/01/04.
 */
class ConfigBuilder extends BuilderSupport {
    /**
     * �r���h���ƃN���X�̃}�b�s���O
     */
    static final Map<String, Class<Buildable>> buildables = [:]
    /**
     * buildables �̏�����
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
     * �r���h�̃c���[�֘A�������e����
     * �Ō�ɁA�e�R���|�[�l���g��setupChild���\�b�h���яo���B
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
     * �p�����[�^���Ȃ��r���h�R���|�[�l���g���쐬����B
     *
     * @param name
     * @return
     */
    @Override
    protected Object createNode(Object name) {
        return createNode(name, [:])
    }

    /**
     * �o�����[�̂݃r���h�R���|�[�l�b�g�𐬂���B
     * ������A�o�����[���v���p�e�B�ɂ�����ď����P�������Ă���B
     * @param name
     * @param value
     * @return
     */
    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, [value: value])
    }

    /**
     * �v���p�e�B��񋟂����r���h�R���|�[�l�b�g�쐬
     * �����T�v�F
     * �P�D�r���h���̂�appendAs�ł܂�ꍇ�AappendAs�����āA�r���h���̂��Ďg���B
     * �Q�D�v���p�e�B��builderName���肳�ꂽ�ꍇ�A�r���h���̂�builderName�Ō��߁A�R���|�[�l�b�g�N���X��T���o���B
     * �R�D�o�����[�������ꂽ�ꍇ�A�o�����[�̓R���|�[�l���g�̂��̂Ƃ���B
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
     * �o�����[�ƃv���p�e�B�����񋟂��ꂽ�r���h�R���|�[�l���g�쐬�B
     * �����T�v
     * �o�����[���v���p�e�B�Ƀ}�[�W���Ă����ʏ������s���B
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
