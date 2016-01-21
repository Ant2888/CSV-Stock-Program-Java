package nodes;


import static handlers.GlobalVars.HEIGHT;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import data.CSVData;
import data.CSVObj;

public class ListNode extends ListView<CSVData>{
	
	protected ObservableList<CSVData> csvObjs;
	private MainContainer parent;
	
	public ListNode(MainContainer parent) {
		this.parent = parent;
		csvObjs = FXCollections.observableArrayList();
		drawList();
		setBounds();
		setHandlers();
	}

	private void setHandlers(){
		setEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			if(e.getButton()==MouseButton.PRIMARY){
				parent.placeDataInView(getSelectionModel().getSelectedItem());
			}
		});
	}
	
	private void setBounds(){
		setMinSize(125,HEIGHT-5);
		setPrefSize(200, HEIGHT-5);
	}
	
	private void drawList(){
		setItems(csvObjs);
	}
	
	public void addCSVObj(CSVObj obj){
		for(int i=1; i<obj.getAmountOfData();i++){
			csvObjs.add(new CSVData(obj,i));
		}
	}
	
	public void removeCSVObj(CSVObj obj){
		for(CSVData d: csvObjs){
			if(d.getParent() == obj){
				csvObjs.remove(d);
			}
		}
	}
	
	public CSVData findData(String toFind){
		for(CSVData data: csvObjs){
			if(data.toString().compareTo(toFind)==0){
				return data;
			}
			if(data.toString().contains(toFind)){
				return data;
			}
		}
		return null;
	}
	
	public boolean isEmpty(){
		return csvObjs.isEmpty();
	}
}
