package tools.graphic.sql.talk.builder

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.config.Log
import tools.graphic.sql.net.Terminal
import tools.graphic.sql.talk.TalkResult
import tools.graphic.sql.talk.builder.talks.*
import tools.graphic.sql.talk.context.MatchLinesTalkable
import tools.graphic.sql.util.VariableUtils

import javax.swing.*

/**
 * talks.*�̃R���|�[�l���g�Ƌ��������Atalks.*�̃R���|�[�l�b�g�ɂ���Ē�`���ꂽ�葱���Œ[���ƑΘb����B
 *
 * Created by nwh on 2016/01/04.
 */
class SendAndWaitTalkable extends MatchLinesTalkable implements Buildable {
    /**
     * �Θb�葱�����`����R���|�[�l���g�ꗗ
     */
    List talks = []

    /**
     * �Θb�Ώۂ̒[��
     */
    Terminal terminal

    /**
     * ����ւ���ϐ�
     */
    Map variables

    /**
     * ���ׂĂ̒�`���I������ꍇ�A���̑Θb���ʂ�Ԃ��B
     */
    TalkResult unlessResult = TalkResult.STOP

    /***
     * ���ݑΘb����I�u�W�F�N�g�C���f�b�N�X
     */
    int talkIdx

    /***
     * ���̃R���|�[�l���g�̎q�R���|�[�l���g��ݒ肷��B</br>
     * �������e</br>
     * �q�R���|�[�l���g��Wait/Send�ł���ꍇ�Atalks�ɒǉ�����B
     *
     * @param child     �q�R���|�[�l���g
     */
    @Override
    void setupChild(Buildable child) {
        super.setupChild(child)
        if (child instanceof Send || child instanceof Wait) {
            talks << child
        }
    }

    /**
     * Send�R���|�[�l���g�ɏ]���āA���M����</br>
     * �����T�v</br>
     * �R���|�[�l���g�̎�ނɂ���āA�[���I�u�W�F�N�g�̃��\�b�h���Ăяo���B
     *
     * @param send  ���M���e
     */
    void send(Send send) {
        Log.debug("send: ${send}")

        if (send instanceof RawSend) {
            parent.terminal.write(send.bytes)
        } else {
            String s = VariableUtils.applyVariables(send.value, parent.variables)
            if (send instanceof Password) {
                parent.terminal.password(s, send.mask)
            } else {
                parent.terminal.puts(s)
            }
        }
    }

    /**
     * Wait�R���|�[�l���g�ɏ]���A�e�L�X�g��҂�</br>
     * �������e</br>
     * wait�Œ�`���ꂽ�p�^�[���g���āAmessage�ɓK�p���Č���B�}�b�`���ꂽ�ꍇ�Await��ނɂ���ď�����U�蕪����B</br>
     * �}�b�`����Ȃ������ꍇ�A�q�r���h�R���|�[�l���g����Wait��T���A�������}�b�`����B</br>
     * �U�蕪�����W�b�N</br>
     * ��ʓI��Wait�̏ꍇ�ATalkResult.CONTINUE��Ԃ��B</br>
     * EndPoint�̏ꍇ�A��`���ꂽresult��Ԃ��B</br>
     * Failure�̏ꍇ�A�G���[���b�Z�[�W��\������B</br>
     *
     * @param wait
     * @param message
     * @return
     */
    TalkResult wait(Wait wait, String message) {
        Log.debug("wait: ${wait}, message: ${message}")

        def waitOne = { Wait w, String s ->
            TalkResult r = null
            if (s =~ w.value) {
                r = TalkResult.CONTINUE
                if (w instanceof EndPoint) {
                    r = w.result
                    if (w instanceof Failure) {
                        JOptionPane.showMessageDialog(null, w.error)
                    }
                }
            }
            return r
        }

        TalkResult r = null
        r = waitOne(wait, message)
        if (r) {
            Log.debug("wait result: ${r}")
            return r
        }
        wait.childrenBuildables.find {
            r = waitOne(it, message)
            r != null
        }
        Log.debug("wait result: ${r}")
        return r
    }

    /**
     * �s�P�ʂŃ}�b�`����B</br>
     * �����T�v</br>
     * �P�D���݃C���f�b�N�X(talkIdx)���版���Ă��ׂĂ̑�����Send�𑗐M</br>
     * �Q�DWait�ɑ΂��ăe�L�X�g�҂�</br>
     * �R�D�㑱��Send�𑗐M</br>
     * �S�D�O�q�����̂�����ł��͈͂��������ꍇ�AunlessResult</br>
     * @param message   �����e�L�X�g
     * @return  �������� true:�}�b�`����Afalse:�}�b�`����
     */
    @Override
    boolean match(String message) {
        Log.debug("message => ${message}")
        Log.debug("talkIdx => ${talkIdx}")
        TalkResult r = null
        def curr

        def sendAll = {
            while (talkIdx < talks.size() && (curr = talks[talkIdx]) instanceof Send) {
                send(curr)
                talkIdx++
            }
        }

        sendAll()
        if (talkIdx >= talks.size()) {
            this.matchedResult = unlessResult
            return true
        }
        if ((r = wait(curr, message))) {
            talkIdx++
            if (r != TalkResult.CONTINUE) {
                this.matchedResult = r
                return true
            } else {
                sendAll()
                if (talkIdx >= talks.size()) {
                    if (r == TalkResult.CONTINUE || r == TalkResult.BREAK) {
                        this.matchedResult = unlessResult
                    }
                }
            }
        }
        return false
    }

    @Override
    void prepareMatch() {
        talkIdx = 0
    }

}
