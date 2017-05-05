package app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainController {
  @FXML private TextField searchVarTextField;
  @FXML private TableView<MVVars> tableView;
  @FXML private TableColumn<MVVars, Integer> idColumn;
  @FXML private TableColumn<MVVars, String> nameColumn;
  @FXML private ToggleButton toggleButton;

  private final DirectoryChooser dc = new DirectoryChooser();
  private File openedDir = null;
  private ObservableList<MVVars> masterData;
  private static final String SEP = File.separator;

  @FXML private void initialize() {//{{{
    dc.setInitialDirectory(new File("."));

    idColumn  . setCellValueFactory(new PropertyValueFactory<MVVars, Integer>("id"));
    nameColumn. setCellValueFactory(new PropertyValueFactory<MVVars, String>("name"));

    tableView.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        MVVars item = tableView.getSelectionModel().getSelectedItem();
        int id = item.idProperty().get();
        invoke(id);
      }
    });
    masterData = FXCollections.observableArrayList();
    //masterData.add(new MVVars(0, ""));

    // テーブルのフィルタリング
    FilteredList<MVVars> filteredData = new FilteredList<>(masterData, p -> true);
    searchVarTextField.textProperty().addListener((obs, oldVal, newVal) -> {
      filteredData.setPredicate(db -> existsMatchedText(db, newVal));
    });
    tableView.setItems(filteredData);

    // キーマッピング
    tableView.setOnKeyPressed(e -> {
      if (!tableView.getSelectionModel().isEmpty()) {
        final int PAGE_SIZE = 20;
        if (KeyCode.J == e.getCode() && e.isControlDown()) {
          for (int i=0; i<PAGE_SIZE; i++) {
            selectNext();
          }
        } else if (KeyCode.K == e.getCode() && e.isControlDown()) {
          for (int i=0; i<PAGE_SIZE; i++) {
            selectPrevious();
          }
        } else if (KeyCode.J == e.getCode()) {
          selectNext();
        } else if (KeyCode.K == e.getCode()) {
          selectPrevious();
        } else if (KeyCode.ENTER == e.getCode()) {
          MVVars item = tableView.getSelectionModel().getSelectedItem();
          int id = item.idProperty().get();
          invoke(id);
        }
      }
    });

    // MV側の選択を操作
    //tableView.getFocusModel().focusedCellProperty().addListener((obs, oldVal, newVal) -> { });

  }//}}}

  @FXML private void openProject() {//{{{
    File file = dc.showDialog(tableView.getScene().getWindow());
    if (file != null) {
      openedDir = file;
      dc.setInitialDirectory(file.getParentFile());

      masterData.clear();
      readVarsList(new File(file.toString() + SEP + "data" + SEP + "System.json"));
      Stage stage = (Stage) tableView.getScene().getWindow();
      stage.setTitle(file.getPath() + " - " + Main.TITLE_VERSION);

      tableView.getSelectionModel().selectFirst();
    }
  }//}}}

  @FXML private void reload() {//{{{
    if (openedDir != null) {
      masterData.clear();
      readVarsList(new File(openedDir.toString() + SEP + "data" + SEP + "System.json"));
      tableView.getSelectionModel().selectFirst();
    }
  }//}}}

  @FXML private void switchAlwaysOnTop() {//{{{
    boolean isSelected = toggleButton.isSelected();
    Stage stage = (Stage) tableView.getScene().getWindow();
    stage.setAlwaysOnTop(isSelected);
  }//}}}

  private boolean existsMatchedText(MVVars db, String newVal) {//{{{
    if (newVal == null || newVal.isEmpty()) {
      return true;
    }

    String lowerCaseFilter = newVal.toLowerCase();

    String id   = "" + db.idProperty()   . get();
    String name =      db.nameProperty() . get();

    if (
        id     . toLowerCase() . contains(lowerCaseFilter)
        || name. toLowerCase() . contains(lowerCaseFilter)
       )
    {
      return true;
    }

    try {
      Pattern p = Pattern.compile(newVal);
      Matcher m = p.matcher(id);
      if (m.find()) return true;

      m = p.matcher(name);
      if (m.find()) return true;
      else return false;
    } catch (PatternSyntaxException ignore) {
    }
    return false;
  }//}}}

  private void readVarsList(File file) {//{{{
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root       = mapper.readTree(file);
      JsonNode child      = root.get("variables");
      int size = child.size();

      IntStream.range(1, size).forEach(i -> {
        String name = child.get(i).asText();
        masterData.add(new MVVars(i, name));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }//}}}

  public void selectPrevious() {//{{{
    int index = tableView.getSelectionModel().getSelectedIndex();
    index = Math.max(0, --index);
    tableView.getSelectionModel().clearAndSelect(index);
    tableView.scrollTo(index);
  }//}}}

  public void selectNext() {//{{{
    int index = tableView.getSelectionModel().getSelectedIndex();
    int max = tableView.getItems().size();
    index = Math.min(++index, max);
    tableView.getSelectionModel().clearAndSelect(index);
    tableView.scrollTo(index);
  }//}}}

  public void invoke(int id) {//{{{
    try {
      Runtime.getRuntime().exec("cmd /c search_vars.vbs " + id);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }//}}}

}
