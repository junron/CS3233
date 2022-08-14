package application

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import java.net.URL
import java.util.*

class MainController : Initializable {
    @FXML
    private lateinit var parent: AnchorPane

    @FXML
    private lateinit var generalTabController: GeneralTabController

    override fun initialize(location: URL, resources: ResourceBundle?) {
        generalTabController.initialize(parent)
        Storage.generalTabController = generalTabController
        Storage.parent = parent
        parent.background = Background(
            BackgroundImage(
                Image(Main::class.java.getResourceAsStream("/susco.png")),
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
            )
        )
    }
}
