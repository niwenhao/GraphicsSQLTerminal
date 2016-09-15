package tools.graphic.sql.talk.context

import tools.graphic.sql.talk.TalkResult

/**
 * メッセージ送信のみTalkable
 *
 * Created by nwh on 2016/01/07.
 */
class RootTalkable extends BuildableTalkable {
    @Override
    TalkResult talk(String message) {
        throw new RuntimeException("Can only used as a root")
    }
}
