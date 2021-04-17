package application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import utils.FxDebug
import utils.ThreadPool

class Main : Application() {
    override fun start(primaryStage: Stage) {
        try {
            ThreadPool.initialize(100)
            val root =
                FXMLLoader.load<AnchorPane>(javaClass.getResource("/main.fxml"))
            val mainScene = Scene(root, 600.0, 400.0)
            mainScene.stylesheets.add(javaClass.getResource("/css/application.css")
                .toExternalForm())
            primaryStage.title = "Ray Simulator"
            primaryStage.scene = mainScene
            primaryStage.isMaximized = true
            primaryStage.show()
            FxDebug.indicatePoint(Point2D(0.0, 0.0), root)
            Storage.clearAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        super.stop()
        ThreadPool.getExecutorService().shutdown()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>?) {
            launch(Main::class.java,"a")
        }
    }
}
