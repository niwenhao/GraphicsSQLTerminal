package tools.graphic.sql.talk.builder.talks

import tools.graphic.sql.talk.TalkResult

/**
 * Wait‚ğŠg’£‚µAŠ®—¹Œ‹‰Ê‚ğİ’è‚Å‚«‚é‚æ‚¤‚É‚È‚éB
 * Created by nwh on 2016/01/08.
 */

class EndPoint extends Wait {
    /**
     * Š®—¹Œ‹‰Ê
     */
    TalkResult result = TalkResult.STOP

    @Override
    String toString() {
        return super.toString() + ", result: ${result}"
    }
}
