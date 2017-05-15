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

  @FXML private TableView<MVVars> varTableView;
  @FXML private TableColumn<MVVars, Integer> varIdColumn;
  @FXML private TableColumn<MVVars, String> varNameColumn;

  @FXML private TableView<MVVars> switchTableView;
  @FXML private TableColumn<MVVars, Integer> switchIdColumn;
  @FXML private TableColumn<MVVars, String> switchNameColumn;

  @FXML private ToggleButton toggleButton;

  private final DirectoryChooser dc = new DirectoryChooser();
  private File openedDir = null;
  private ObservableList<MVVars> varMasterData;
  private ObservableList<MVVars> switchMasterData;
  private static final String SEP = File.separator;

  @FXML private void initialize() {//{{{
    dc.setInitialDirectory(new File("."));

    varIdColumn  . setCellValueFactory(new PropertyValueFactory<MVVars, Integer>("id"));
    varNameColumn. setCellValueFactory(new PropertyValueFactory<MVVars, String>("name"));

    switchIdColumn  . setCellValueFactory(new PropertyValueFactory<MVVars, Integer>("id"));
    switchNameColumn. setCellValueFactory(new PropertyValueFactory<MVVars, String>("name"));

    final String SEARCH_VARS_SCRIPT     = "search_vars.vbs";
    final String SEARCH_SWITCHES_SCRIPT = "search_switches.vbs";

    // ダブルクリックで実行
    varTableView.setOnMouseClicked(e -> {
      doubleClick(varTableView, SEARCH_VARS_SCRIPT, e);
    });
    varMasterData = FXCollections.observableArrayList();

    // ダブルクリックで実行
    switchTableView.setOnMouseClicked(e -> {
      doubleClick(switchTableView, SEARCH_SWITCHES_SCRIPT, e);
    });
    switchMasterData = FXCollections.observableArrayList();

    // テーブルのフィルタリング
    FilteredList<MVVars> varFilter = new FilteredList<>(varMasterData, p -> true);
    FilteredList<MVVars> switchFilter = new FilteredList<>(switchMasterData, p -> true);
    searchVarTextField.textProperty().addListener((obs, oldVal, newVal) -> {
      varFilter.setPredicate(db -> existsMatchedText(db, newVal));
      switchFilter.setPredicate(db -> existsMatchedText(db, newVal));
    });
    varTableView.setItems(varFilter);
    switchTableView.setItems(switchFilter);

    // キーマッピング
    varTableView.setOnKeyPressed(e -> {
      onKeyPressed(varTableView, SEARCH_VARS_SCRIPT, e);
    });

    // キーマッピング
    switchTableView.setOnKeyPressed(e -> {
      onKeyPressed(switchTableView, SEARCH_SWITCHES_SCRIPT, e);
    });

  }//}}}

  private void doubleClick(TableView<MVVars> table, String scriptFile, MouseEvent e) {//{{{
    if (e.getClickCount() == 2) {
      MVVars item = table.getSelectionModel().getSelectedItem();
      int id = item.idProperty().get();
      invoke(id, scriptFile);
    }
  }//}}}

  private void onKeyPressed(TableView<MVVars> table, String scriptFile, KeyEvent e) {//{{{
    if (!table.getSelectionModel().isEmpty()) {
      final int PAGE_SIZE = 20;
      if (KeyCode.J == e.getCode() && e.isControlDown()) {
        for (int i=0; i<PAGE_SIZE; i++) {
          selectNext(table);
        }
      } else if (KeyCode.K == e.getCode() && e.isControlDown()) {
        for (int i=0; i<PAGE_SIZE; i++) {
          selectPrevious(table);
        }
      } else if (KeyCode.J == e.getCode()) {
        selectNext(table);
      } else if (KeyCode.K == e.getCode()) {
        selectPrevious(table);
      } else if (KeyCode.ENTER == e.getCode()) {
        MVVars item = table.getSelectionModel().getSelectedItem();
        int id = item.idProperty().get();
        invoke(id, scriptFile);
      }
    }
  }//}}}

  @FXML private void openProject() {//{{{
    File file = dc.showDialog(varTableView.getScene().getWindow());
    if (file != null) {
      openedDir = file;
      dc.setInitialDirectory(file.getParentFile());

      varMasterData.clear();
      readVarsList(new File(file.toString() + SEP + "data" + SEP + "System.json"));
      Stage stage = (Stage) varTableView.getScene().getWindow();
      stage.setTitle(file.getPath() + " - " + Main.TITLE_VERSION);

      varTableView.getSelectionModel().selectFirst();
    }
  }//}}}

  @FXML private void reload() {//{{{
    if (openedDir != null) {
      varMasterData.clear();
      readVarsList(new File(openedDir.toString() + SEP + "data" + SEP + "System.json"));
      varTableView.getSelectionModel().selectFirst();
    }
  }//}}}

  @FXML private void switchAlwaysOnTop() {//{{{
    boolean isSelected = toggleButton.isSelected();
    Stage stage = (Stage) varTableView.getScene().getWindow();
    stage.setAlwaysOnTop(isSelected);
  }//}}}

  private static final boolean existsMatchedText(MVVars db, String newVal) {//{{{
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
        varMasterData.add(new MVVars(i, name));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root       = mapper.readTree(file);
      JsonNode child      = root.get("switches");
      int size = child.size();

      IntStream.range(1, size).forEach(i -> {
        String name = child.get(i).asText();
        switchMasterData.add(new MVVars(i, name));
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

  }//}}}

  public void selectPrevious(TableView<MVVars> table) {//{{{
    int index = table.getSelectionModel().getSelectedIndex();
    index = Math.max(0, --index);
    table.getSelectionModel().clearAndSelect(index);
    table.scrollTo(index);
  }//}}}

  public void selectNext(TableView<MVVars> table) {//{{{
    int index = table.getSelectionModel().getSelectedIndex();
    int max = table.getItems().size();
    index = Math.min(++index, max);
    table.getSelectionModel().clearAndSelect(index);
    table.scrollTo(index);
  }//}}}

  public void invoke(int id, String scriptFileName) {//{{{
    try {
      Runtime.getRuntime().exec("cmd /c " + scriptFileName + " " + id);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }//}}}

}
