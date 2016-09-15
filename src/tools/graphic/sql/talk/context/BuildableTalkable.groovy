package tools.graphic.sql.talk.context

import tools.graphic.sql.config.Buildable
import tools.graphic.sql.talk.Talkable

/**
 * TalkControllerでコントロールできるTalkableに設定ビルド機能を追加する。</br>
 * <pre>
 *     提供機能
 *     子ビルドコンポーネントからTalkableであるものを探し、Talkable#childrenに追加する。
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
