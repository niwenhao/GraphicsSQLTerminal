import tools.graphic.sql.GraphicSqlTerminal
import tools.graphic.sql.config.ConfigBuilder
import tools.graphic.sql.net.telnet.TelnetTerminalFactory

List.metaClass.map = { Closure p ->
    List r = []
    delegate.each {
        r << p(it)
    }

    return r
}

Map.metaClass.map = { Closure p ->
    Map r = [:]
    delegate.each { k, v ->
        r[k] = p(it)
    }

    return r
}

Object.metaClass.configEngine = new GroovyScriptEngine(["config/"] as String[], Thread.currentThread().contextClassLoader)
configEngine.config.sourceEncoding = System.properties["file.encoding"]

Class configScript = configEngine.loadScriptByName("Config.groovy")
configScript.newInstance().run()

GraphicSqlTerminal.controller.start()
