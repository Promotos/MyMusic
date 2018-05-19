package de.promotos.mm.scene;

import de.promotos.mm.service.DriveApi;
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

public class MainSceneController {

	private DriveApi api;
	private final ObservableList<FileModel> fileTableData = FXCollections.observableArrayList();
	
	@FXML
	private TableView<FileModel> tvFilesTable;
	
	@FXML
	private TableColumn<FileModel, String> colName;
	
    @FXML
    public void initialize() {
    	tvFilesTable.setItems(fileTableData);
    	colName.setCellValueFactory(new PropertyValueFactory<FileModel, String>("name"));
    }
	
    public void setApi(final DriveApi api) {
    	this.api = api;
    }
    
	public void updateFiles() throws ServiceException {
		fileTableData.clear();
		api.listAudioFiles().stream().forEach(fileTableData::add);
	}

	public void refresh() {
		try {
			updateFiles();
		} catch (ServiceException e) {
			showErrorDialog(e);
		}
	}
    
	private void showErrorDialog(final Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR, throwable.getMessage() );
		alert.showAndWait();
	}
}
