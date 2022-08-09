package application

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

class MainController : Initializable {
    @FXML
    private var parent: AnchorPane? = null

    @FXML
    private var generalTabController: GeneralTabController? = null

    @FXML
    private var animationTabController: AnimationTabController? = null

    override fun initialize(location: URL, resources: ResourceBundle?) {
        val parent = parent ?: return
        println(generalTabController)
        println(animationTabController)
        generalTabController!!.initialize(parent)
        animationTabController!!.initialize(parent)
        Storage.generalTabController = generalTabController
        Storage.parent = parent
    }
}
