package nodes;

import static handlers.GlobalVars.HEIGHT;
import handlers.GlobalVars.Global_Enums;

import java.util.List;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import data.CSVObj;
import data.maths.ObjParser;

public class TreeNode extends TreeView<String> {

	private MainContainer parent;
	private TreeItem<String> root;

	public TreeNode(MainContainer parent) {
		super(new TreeItem<String>("Stocks"));
		this.parent = parent;
		root = getRoot();
		setBounds();
		setHandlers();
	}

	private void setHandlers() {
		setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY){
				if (getSelectionModel().getSelectedItem() != null) {
					parent.notifyObserver(Global_Enums.SELECTED_ITEM_TREE);
				}
			}else if(e.getButton() == MouseButton.SECONDARY){
				if(getSelectionModel().getSelectedItems() != null){
					
				}
			}
		});
	}

	private void setBounds() {
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setMinSize(200, HEIGHT);
		setPrefSize(300, HEIGHT);
	}

	public void removeItems(CSVObj obj) {
		for (TreeItem<String> names : root.getChildren()) {
			if (names.getValue().compareTo(obj.toString()) == 0) {
				names.getChildren().remove(names);
				break;
			}
		}
	}

	public void addItems(CSVObj obj) {
		boolean exists = false;
		for (TreeItem<String> names : root.getChildren()) {
			// since the name of the stock will be to toString if it has that
			// toString
			// then it has already been created and we will ignore the
			// addrequest
			if (names.getValue().compareTo(obj.toString()) == 0) {
				exists = true;
				break;
			}
		}
		if (exists)
			;
		else {
			// the main stocks container (not to be confused with the root)
			TreeItem<String> stockContainer = new TreeItem<>(obj.toString());
			createYears(stockContainer, obj);
			root.getChildren().add(stockContainer); // add the now created
			// container to the root
		}
	}

	// can assume they arent created
	private void createYears(TreeItem<String> stockContainer, CSVObj obj) {
		// get the most recent year to iterate from and create a blank obj
		int mostRecentYear = ObjParser.ParseDateYMD(obj.getData(1)[0])[0];
		// while our year is valid in the stock
		while (obj.getMap().get(String.valueOf(mostRecentYear)) != null) {
			TreeItem<String> year = new TreeItem<>(
					String.valueOf(mostRecentYear));
			for (int i = 1; i <= 12; i++) {
				// iterate through all 12 months
				String recentMonth = ObjParser.getMonth(i);
				TreeItem<String> month = new TreeItem<>(recentMonth);
				if (obj.getMap().get(String.valueOf(mostRecentYear))
						.get(recentMonth) == null);
				// incase for whatever reason a month is missing
				else {
					// grab the list of days in that month: note that those are
					// its indexes not the days themselves
					List<Integer> days = obj.getListAtMonth(
							String.valueOf(mostRecentYear), recentMonth);
					for (int j = 0; j < days.size(); j++) {
						// create date objs
						TreeItem<String> date = new TreeItem<>(mostRecentYear
								+ " " + recentMonth + " "
								+ obj.getData(days.get(j))[0].split("-")[2]);
						month.getChildren().add(date);
					}
					// add the months to the year
					year.getChildren().add(month);
				}
			}
			// add the stock and decrement the year
			stockContainer.getChildren().add(year);
			mostRecentYear--;
		}
	}

	public String getCurrentModel() {
		return getSelectionModel().getSelectedItem().getValue();
	}

	public String getModelStockName(){
		if(getSelectionModel().getSelectedItem() != null){
			TreeItem<String> model = getSelectionModel().getSelectedItem();
			while(model.getParent() != root && model.getParent() != null){
				model = model.getParent();
			}
			return model.getValue();
		}
		return null;
	}
}
