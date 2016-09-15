package tools.graphic.sql.talk

import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal


/**
 *
 * <pre>
 *      ���L�̃p�^�[���ɂȂ�B
 *      Talkable1
 *          Talkable11
 *          Talkable12
 *      TalkControll#talk(Talkable1)���Ăяo������A���L�̏����ɂȂ�B
 *      �P�DTalkable1��prepareTalk���Ăяo��
 *      �Q�DTalkable11�ATalkable12��prepareMatch���Ăяo��
 *      �R�DTerminal#gets�Ŏ擾�����e�L�X�g��Talkable11�ATalkable12��Talkable#talk�ɓn���B
 *          �߂�l�ɂ���ĐU�蕪����B
 *      �܂Ƃ�
 *          Talkable1���J�����gTalkable�ł���A�������M������i����΁j�A��M�������b�Z�[�W�̓J�����gTalkable��
 *          children�iTalkable11�ATalkable12�j�����Ɉ��܂�āA�߂�l�Ŏ��̃J�����gTalkable�����܂�A�J�����gTalkable���ς������
 *          �܂��A�������M������B
 * </pre>
 * Created by nwh on 2015/12/29.
 */
class TalkController {
    /**
     * ����M�[��
     */
    Terminal terminal

    /**
     * �Θb���郁�\�b�h
     *
     * @param root  ��Ԗڂ�Talkable
     * @param variables ���M���e����ւ��ϐ��}�b�v
     * @return      �Ō��TalkResult
     */
    TalkResult talk(Talkable root, Map variables) {
        String message
        Talkable curr = root
        boolean contTalk = true
        TalkResult rst

        try {
            root.prepareTalk(this, terminal, variables)
            root.children.each { it.prepareMatch() }

            while(contTalk && (message = terminal.gets()) != null) {
                Log.debug("message => ${message}")
                boolean contContext = true
                curr.children.each { Talkable ctx ->
                    if (contContext) {
                        rst = ctx.talk(message)
                        Log.debug("Context(${ctx} -> ${rst}")
                        switch (rst) {
                            case TalkResult.BACKWORD:
                                curr = curr.parent
                                curr.prepareTalk(this, terminal, variables)
                                curr.children.each { it.prepareMatch() }
                                break
                            case TalkResult.FORWARD:
                                curr = ctx
                                curr.prepareTalk(this, terminal, variables)
                                curr.children.each { it.prepareMatch() }
                                break
                            case TalkResult.BREAK:
                                contContext = false
                                break
                            case TalkResult.STOP:
                            case TalkResult.ERROR:
                                contContext = false
                                contTalk = false
                                break
                        }
                    }
                }
            }
            return rst
        } catch(e) {
            throw new RuntimeException(e)
        }
    }
}