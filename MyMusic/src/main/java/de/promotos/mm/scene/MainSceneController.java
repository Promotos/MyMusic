package de.promotos.mm.scene;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import de.promotos.mm.player.stream.StreamPlayer;
import de.promotos.mm.player.stream.StreamPlayerException;
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
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final ObservableList<FileModel> fileTableData = FXCollections.observableArrayList();
	private final ObservableList<FileModelFormatted> fileSelectedData = FXCollections.observableArrayList();
	private final StreamPlayer player = new StreamPlayer();

	@FXML
	private TableView<FileModel> tvFilesTable;

	@FXML
	private TableColumn<FileModel, String> colName;

	@FXML
	private Button btnUpload;

	@FXML
	private Button btnDownload;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnPlay;

	@FXML
	private Button btnStop;

	@FXML
	private Label lbStatusBarLeft;

	@FXML
	private ListView<FileModelFormatted> lvSelectedFiles;

	/**
	 * Executed after the controller is created and the components are bound to
	 * instance. Used to initialize the member components.
	 */
	@FXML
	public void initialize() {
		tvFilesTable.setItems(fileTableData);
		tvFilesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tvFilesTable.setOnMouseClicked(event -> tvFilesTableOnMouseClick(Assert.nN(event)));

		colName.setCellValueFactory(new PropertyValueFactory<FileModel, String>("name"));

		lvSelectedFiles.setItems(fileSelectedData);
	}

	/**
	 * Callback before the application shuts down.
	 */
	public void shutdown() {
		player.stop();
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
		final List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

		if (selectedFiles != null) {
			final Task<List<FileModel>> uploadTask = new TaskFactory().buildUploadTask(Assert.nN(api), selectedFiles);
			new ProgressScene().show(Assert.nN(new Stage(StageStyle.UNDECORATED)), uploadTask,
					this::refresh,
					MainSceneController::showErrorDialog);
			executor.submit(uploadTask);
		}
	}

	@FXML
	private void btnDownloadOnAction(ActionEvent event) {
		throw new UnsupportedOperationException("Download unsupported");
	}

	@FXML
	private void btnDeleteOnAction(ActionEvent event) {
		final List<FileModel> files = tvFilesTable.getSelectionModel().getSelectedItems();
		if (files != null) {
			final Task<Boolean> deleteTask = new TaskFactory().buildDeleteTask(Assert.nN(api), files);
			new ProgressScene().show(Assert.nN(new Stage(StageStyle.UNDECORATED)), deleteTask,
					this::refresh,
					MainSceneController::showErrorDialog);
			executor.submit(deleteTask);
		}
	}

	@FXML
	private void tvFilesTableOnMouseClick(MouseEvent event) {
		final FileModel file = tvFilesTable.getSelectionModel().getSelectedItem();
		if (event.getClickCount() == 2 && file != null) {
			fileSelectedData.add(new FileModelFormatted(file));
		}
	}

	@FXML
	private void btnPlayOnAction(ActionEvent event) {
		Thread t1 = new Thread(() -> {
			try {
				for (FileModelFormatted model : fileSelectedData) {
					player.open(api.readFile(model.model));
					player.play();
				}
			} catch (StreamPlayerException e) {
				throw new IllegalStateException(e);
			} catch (ServiceException e) {
				throw new IllegalStateException(e);
			}
		});
		t1.setDaemon(true);
		t1.setName("Stream player thread");
		t1.start();
	}

	@FXML
	private void btnStopOnAction(ActionEvent event) {
		if (player.isPausedOrPlaying()) {
			player.stop();
		}
	}

	private static void showErrorDialog(final Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR, throwable.getMessage());
		alert.showAndWait();
	}

	class FileModelFormatted implements FileModel {

		public final @Nonnull FileModel model;

		public FileModelFormatted(final FileModel model) {
			this.model = model;
		}

		@Override
		public Object getId() {
			return model.getId();
		}

		@Override
		public String getName() {
			return model.getName();
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}
