package de.promotos.mm.scene;

import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.ServiceException;
import de.promotos.mm.service.model.FileModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for the main screen.
 * 
 * @author Promotos
 *
 */
public class MainSceneController {

	private CloudApi api;
	private final ObservableList<FileModel> fileTableData = FXCollections.observableArrayList();

	@FXML
	private TableView<FileModel> tvFilesTable;

	@FXML
	private TableColumn<FileModel, String> colName;

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
	 * @param api The cloud api instance.
	 */
	public void setApi(final CloudApi api) {
		this.api = api;
	}

	/**
	 * Update the available file list in the main table.
	 * 
	 * @throws ServiceException If the cloud api call fail.
	 */
	public void updateFiles() throws ServiceException {
		fileTableData.clear();
		api.listAudioFiles().stream().forEach(fileTableData::add);
	}

	/**
	 * Update all main scene components with the cloud information.
	 */
	public void refresh() {
		try {
			updateFiles();
		} catch (ServiceException e) {
			showErrorDialog(e);
		}
	}

	private static void showErrorDialog(final Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR, throwable.getMessage());
		alert.showAndWait();
	}
}
