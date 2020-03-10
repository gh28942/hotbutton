package hotbutton;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HighscoreManager {
	
	@FXML
	TableView<Entry> HStableView;
	@FXML
	Label HSlabel;
	
	final String secretKey = "|,£0²hQsµøÈ¹»8Ú¼qñ0TÚÁ®Ø`jî«´è7^Ý}ç";
	
	public void openScoreview(){

		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("HighscoreView.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 650, 355);
			Platform.runLater(new Runnable() {
				@Override public void run() {
					
					String info[] = getPersistentData();
					int gameMode = Integer.parseInt(info[2]);
					
					Stage stage = new Stage();
					stage.setTitle("High Score - "+getGameModeStr(gameMode));
					stage.getIcons().add(new Image(getClass().getResourceAsStream("/hotbutton.png")));
					stage.setScene(scene);
					stage.show();

				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
	
		String info[] = getPersistentData();
		int gameMode = Integer.parseInt(info[2]);
		
		HSlabel.setText("High Score - "+getGameModeStr(gameMode));

		TableColumn<Entry, String> placeCol = new TableColumn<Entry, String>("Place");
		placeCol.setMinWidth(50);
		placeCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("place"));

        TableColumn<Entry, String> nameCol = new TableColumn<Entry, String>("Name");
        nameCol.setMinWidth(140);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("name"));

        TableColumn<Entry, String> scoreCol = new TableColumn<Entry, String>("Score");
        scoreCol.setMinWidth(90);
        scoreCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("score"));

        TableColumn<Entry, String> timeCol = new TableColumn<Entry, String>("Time");
        timeCol.setMinWidth(50);
        timeCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("time"));

        TableColumn<Entry, String> missesCol = new TableColumn<Entry, String>("Misses");
        missesCol.setMinWidth(20);
        missesCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("misses"));

        TableColumn<Entry, String> dateCol = new TableColumn<Entry, String>("Date");
        dateCol.setMinWidth(50);
        dateCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("date"));

        TableColumn<Entry, String> textCol = new TableColumn<Entry, String>("Details");
        textCol.setMinWidth(50);
        textCol.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("text"));

        HStableView.getColumns().addAll(placeCol, nameCol, scoreCol, timeCol, missesCol, dateCol, textCol);

        final ObservableList<Entry> winners = loadCsvData();
        HStableView.setItems(winners);
	}

	public String getGameModeStr(int mode) {
		String gameModeStr = "Error";
		switch (mode) {
		case 1:
			gameModeStr = "Endless Game";
			break;
		case 2:
			gameModeStr = "Hunt (Easy)";
			break;
		case 3:
			gameModeStr = "Hunt (Medium)";
			break;
		case 4:
			gameModeStr = "Hunt (Hard)";
			break;
		case 5:
			gameModeStr = "Time (Easy)";
			break;
		case 6:
			gameModeStr = "Time (Medium)";
			break;
		case 7:
			gameModeStr = "Time (Hard)";
			break;
		case 8:
			gameModeStr = "Click (Easy)";
			break;
		case 9:
			gameModeStr = "Click (Medium)";
			break;
		case 10:
			gameModeStr = "Click (Hard)";
			break;
		case 11:
			gameModeStr = "Horror (Easy)";
			break;
		case 12:
			gameModeStr = "Horror (Medium)";
			break;
		case 13:
			gameModeStr = "Horror (Hard)";
			break;
		default:
			return "Error";
		}
		return gameModeStr;
	}
	
	public static class Entry {
	    private final SimpleStringProperty place;
	    private final SimpleStringProperty name;
	    private final SimpleStringProperty score;
	    private final SimpleStringProperty time;
	    private final SimpleStringProperty misses;
	    private final SimpleStringProperty date;
	    private final SimpleStringProperty text;

	    private Entry(String fPlace, String fName, String fScore, String fTime, String fMisses, String fDate, String fText) {
	        this.place = new SimpleStringProperty(fPlace);
	        this.name = new SimpleStringProperty(fName);
	        this.score = new SimpleStringProperty(fScore);
	        this.time = new SimpleStringProperty(fTime);
	        this.misses = new SimpleStringProperty(fMisses);
	        this.date = new SimpleStringProperty(fDate);
	        this.text = new SimpleStringProperty(fText);
	    }

	    public String getPlace() {
	        return place.get();
	    }
	    public void setPlace(String fPlace) {
	    	place.set(fPlace);
	    }

	    public String getName() {
	        return name.get();
	    }
	    public void setName(String fName) {
	    	name.set(fName);
	    }

	    public String getScore() {
	        return score.get();
	    }
	    public void setScore(String fScore) {
	    	score.set(fScore);
	    }

	    public String getTime() {
	        return time.get();
	    }
	    public void setTime(String fTime) {
	    	time.set(fTime);
	    }

	    public String getMisses() {
	        return misses.get();
	    }
	    public void setMisses(String fMisses) {
	    	misses.set(fMisses);
	    }

	    public String getDate() {
	        return date.get();
	    }
	    public void setDate(String fDate) {
	    	date.set(fDate);
	    }

	    public String getText() {
	        return text.get();
	    }
	    public void setText(String fText) {
	    	text.set(fText);
	    }
	}

	public ObservableList<Entry> loadCsvData(){

		String info[] = getPersistentData();
		int gameMode = Integer.parseInt(info[2]);
		
		ObservableList<Entry> winners = FXCollections.observableArrayList();
		DecimalFormat timeFormat = new DecimalFormat("##.000");
		Scanner in;

		try {
			in = new Scanner(new FileReader("data/"+gameMode));
			StringBuilder sb = new StringBuilder();
			while(in.hasNext()) {
				sb.append(in.next());
			}
			in.close();

			String highscoreString = sb.toString();
			String highscoreStringDecr = AES.decrypt(highscoreString, secretKey) ;
			
			//read & check HS file
			String[] HSlines = highscoreStringDecr.split("\\|");

			for(int i=1; i<11; i++){
				String[] HSentry = HSlines[i].split(";");
				Entry mEntry = new Entry(i+".", HSentry[0], HSentry[1], timeFormat.format(Double.parseDouble(HSentry[2])), HSentry[3], HSentry[4], HSentry[5]);
				winners.add(mEntry);
			}
			return winners;

		} catch (FileNotFoundException e) {
			DialogBoxes.showErrorBox("Error", "\"Hot Button\" has encountered a problem.", e.getMessage());
			return winners;
		}
	}
	
	public String[] getPersistentData() {

		Scanner in;
		try {
			// read and decrypt persistent data file
			in = new Scanner(new FileReader("data/0"));
			StringBuilder sb = new StringBuilder();
			while (in.hasNext()) 
				sb.append(in.next());
			in.close();
			String dataString = sb.toString();
			String outString = AES.decrypt(dataString, secretKey);
			return outString.split("\\|");
		} catch (FileNotFoundException e) {
			DialogBoxes.showErrorBox("Error", "\"Hot Button\" has encountered a problem - could not load data!", e.getMessage());
			return new String[] {"0000","","1"};
		}
	}
}
