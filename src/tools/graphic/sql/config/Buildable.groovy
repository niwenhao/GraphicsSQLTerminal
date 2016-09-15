package tools.graphic.sql.config

import java.util.regex.Matcher

/**
 * ConfigBuilderでビルドできるコンポーネントが実装が必要な機能
 *
 * Created by nwh on 2016/01/07.
 */
trait Buildable {
    /**
     * ビルドできな子コンポーネット一覧
     */
    List<Buildable> childrenBuildables = []
    /**
     * ビルド的な親コンポーネント
     */
    Buildable parentBuildable
    /**
     * ビルド的な名称
     */
    String buildableName

    /**
     * 子コンポーネントができるとき、子コンポーネント設定
     *
     * @param child     子コンポーネント
     */
    void setupChild(Buildable child) {

    }

    /**
     * コンポーネントプロパティ設定
     *
     * @param attributes プロパティキー・バリュー
     */
    void setupAttribute(Map attributes) {

    }

    /**
     * 子コンポーネントの参照を取得するため、プロパティのシミュレーター
     *
     * @param name  プロパティ名、buildableName + "Buildable"で単一コンポーネントを得でき、buildableName + "Buildables"で同名なコンポーネットのリストを得する。
     *
     * @return  取得したコンポーネントまたはコンポーネントリスト
     */
    def propertyMissing(String name) {
        Matcher m = name =~ /^(.*)Buildable(s?)$/
        def p = null
        if (m.find()) {
            if (m.group(2) == "s") {
                p = this.childrenBuildables.grep {
                    it.buildableName == m.group(1)
                }
                if (p.size() == 0) {
                    p = null
                }
            } else {
                p = this.childrenBuildables.find {
                    it.buildableName == m.group(1)
                }
            }
        }

        if (p == null) {
            throw new MissingPropertyException("property ${name} has not found.")
        }
        return p
    }
}
