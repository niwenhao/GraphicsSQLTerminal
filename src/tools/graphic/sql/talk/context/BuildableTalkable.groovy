package tools.graphic.sql.talk.context

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.talk.Talkable

/**
 * TalkController�ŃR���g���[���ł���Talkable�ɐݒ�r���h�@�\��ǉ�����B</br>
 * <pre>
 *     �񋟋@�\
 *     �q�r���h�R���|�[�l���g����Talkable�ł�����̂�T���ATalkable#children�ɒǉ�����B
 * </pre>
 *
 * Created by nwh on 2016/01/05.
 */
abstract class BuildableTalkable extends Talkable implements Buildable {
    @Override
    void setupChild(Buildable child) {
        if (child instanceof Talkable) {
            children << child
            child.parent = this
        }
    }
}
