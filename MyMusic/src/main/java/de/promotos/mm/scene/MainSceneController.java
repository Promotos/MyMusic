package de.promotos.mm.scene;

import java.io.File;
import java.util.Optional;

import de.promotos.mm.service.Assert;
import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.ServiceException;
import de.promotos.mm.service.TaskFactory;
import de.promotos.mm.service.model.FileModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller class for the main screen.
 * 
 * @author Promotos
 *
 */
public class MainSceneController {

	private CloudApi api;
	private Stage stage;
	private final ObservableList<FileModel> fileTableData = FXCollections.observableArrayList();

	@FXML
	private TableView<FileModel> tvFilesTable;

	@FXML
	private TableColumn<FileModel, String> colName;

	@FXML
	private Button btnUpload;

	@FXML
	private Label lbStatusBarLeft;

	/**
	 * Executed after the controller is created and the components are bound to
	 * instance. Used to initialize the member components.
	 */
	@FXML
	public void initialize() {
		tvFilesTable.setItems(fileTableData);
		colName.setCellValueFactory(new PropertyValueFactory<FileModel, String>("name"));
	}

	/**
	 * Used to set the cloud api access.
	 * 
	 * @param api
	 *           The cloud api instance.
	 */
	public void setApi(final CloudApi api) {
		this.api = api;
	}

	/**
	 * Access to the ui stage.
	 * 
	 * @param stage
	 *           The stage instance.
	 */
	public void setStage(final Stage stage) {
		this.stage = stage;
	}

	/**
	 * Update the available file list in the main table.
	 * 
	 * @throws ServiceException
	 *            If the cloud api call fail.
	 */
	public void updateFiles() throws ServiceException {
		fileTableData.clear();
		api.listAudioFiles().stream().forEach(fileTableData::add);
	}

	/**
	 * Update the status bar label with user quota information.
	 * 
	 * @throws ServiceException
	 *            If the cloud api call fail.
	 */
	public void updateStatusBarLabel() throws ServiceException {
		lbStatusBarLeft.setText(api.getUserQuota());
	}

	/**
	 * Update all main scene components with the cloud information.
	 */
	public void refresh() {
		try {
			updateFiles();
			updateStatusBarLabel();
		} catch (ServiceException e) {
			showErrorDialog(e);
		}
	}

	@FXML
	private void btnUploadOnAction(ActionEvent event) {
		final FileChooser fileChooser = UIComponentFactory.buildAudioFileChooser();
		final File selectedFile = fileChooser.showOpenDialog(stage);

		if (selectedFile != null) {
			final Task<Optional<FileModel>> uploadTask = new TaskFactory().buildUploadTask(Assert.nN(api), selectedFile);
			new ProgressScene().show(Assert.nN(new Stage(StageStyle.UNDECORATED)), uploadTask,
					() -> refresh(),
					error -> showErrorDialog(error));
			new Thread(uploadTask).start();
		}
	}

	private static void showErrorDialog(final Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR, throwable.getMessage());
		alert.showAndWait();
	}
}
