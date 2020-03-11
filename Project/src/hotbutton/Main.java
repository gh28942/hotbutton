package hotbutton;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.locks.LockSupport;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javazoom.jl.player.Player;

public class Main extends Application {
	
	// Game types:
	// hunt - button up for a short time
	// time - shoot as many buttons as possible in one minute
	// click - shoot 60 buttons (as fast as possible)
	// horror - like hunt, but with a shocking surprise (silent screamer randomly)

	@FXML
	GridPane GameGridPane;
	@FXML
	Label labelGameText;
	@FXML
	Label labelScoreText;
	@FXML
	Button invButton;
	@FXML
	AnchorPane anchorpane;
	@FXML
	Pane greyPane;

	@FXML
	ImageView imgView1;

	@FXML
	ImageView ammo1;
	@FXML
	ImageView ammo2;
	@FXML
	ImageView ammo3;
	@FXML
	ImageView ammo4;
	@FXML
	ImageView ammo5;
	@FXML
	ImageView ammo6;
	@FXML
	ImageView ammo7;
	@FXML
	ImageView ammo8;
	@FXML
	ImageView ammo9;
	@FXML
	ImageView ammo10;
	
	@FXML
	TextField nameField;
	
	@FXML
	Button horroreasybtn;
	@FXML
	Button horrormedbtn;
	@FXML
	Button horrorhardbtn;
	@FXML
	Button huntmedbtn;
	@FXML
	Button hunthardbtn;
	@FXML
	Button clickeasybtn;
	@FXML
	Button clickmedbtn;
	@FXML
	Button clickhardbtn;
	@FXML
	Button timeeasybtn;
	@FXML
	Button timemedbtn;
	@FXML
	Button timehardbtn;
	@FXML
	Button endgamebtn;

	@FXML
	SplitMenuButton audioSplitMenu;
	float audioValue = 0.20F;

	private int id = 0;
	private int globalscore = 0;
	private boolean gameIsOn = false;

	int globalmisses = 0;
	int globalammo = 10;
	
	int gameMode = 0;
	String unlockedGameModes = "0000";

	Thread musicThread = new Thread();
	Thread playerThread = new Thread();
	
	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
			Parent root = null;
			try {
				root = (Parent) fxmlLoader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.DECORATED);
			stage.setTitle("Hot Button on Fire Deluxe");
			stage.setResizable(false);

			//properly close application (if this doesn't happen automatically)
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/hotbutton.png")));
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@FXML
	private void initialize() {
		invButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (globalammo > 0) {
					updateScore(globalscore - 5);
					globalmisses++;
					shootAmmo(false);
				} else
					playSound(3);
			}
		});
		Image ammoImage = new Image(getClass().getResourceAsStream("/ammo.png"));
		ammo1.setImage(ammoImage);
		ammo2.setImage(ammoImage);
		ammo3.setImage(ammoImage);
		ammo4.setImage(ammoImage);
		ammo5.setImage(ammoImage);
		ammo6.setImage(ammoImage);
		ammo7.setImage(ammoImage);
		ammo8.setImage(ammoImage);
		ammo9.setImage(ammoImage);
		ammo10.setImage(ammoImage);

		String info[] = getPersistentData();
		if (info[1].equals(""))
			nameField.setText(System.getProperty("user.name"));
		else
			nameField.setText(info[1]);
		unlockedGameModes = info[0];
		unlockGameModes();
	}

	@FXML
	private void startEasyHunt() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(40, 2000, 51, "hunt", 2);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startMediumHunt() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(30, 1500, 61, "hunt", 3);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startHardHunt() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(20, 1000, 71, "hunt", 4);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startEasyHorror() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(40, 2000, 51, "horror", 11);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startMediumHorror() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(30, 1500, 61, "horror", 12);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startHardHorror() {
		if (!nameField.getText().equals("")) {
			startHuntOrHorror(20, 1000, 71, "horror", 13);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startEasyTime() {
		if (!nameField.getText().equals("")) {
			startTimeMode(40, "time", 5);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startMediumTime() {
		if (!nameField.getText().equals("")) {
			startTimeMode(30, "time", 6);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startHardTime() {
		if (!nameField.getText().equals("")) {
			startTimeMode(20, "time", 7);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startEasyClick() {
		if (!nameField.getText().equals("")) {
			startClickMode(40, "click", 8);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startMediumClick() {
		if (!nameField.getText().equals("")) {
			startClickMode(30, "click", 9);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void startHardClick() {
		if (!nameField.getText().equals("")) {
			startClickMode(20, "click", 10);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	@FXML
	private void initiateEndlessGame() {
		if (!nameField.getText().equals("")) {
			startEndlessGame(30, 1500, 60, "Endless ", 1);
			nameField.setEditable(false);
		} else
			DialogBoxes.showErrorBox("Name missing", "Please enter a name first!", "");
	}

	private void startHuntOrHorror(int buttonSize, int buttonTime, int buttonNumber, String type, int subtype) {
    	storePersistentData(unlockedGameModes, subtype);
		if (!gameIsOn) {
			startMusic();
			if (type.equals("horror")) {
				labelGameText.setText("Horror");
				int totalTime = buttonNumber * buttonTime;
				int randScreamer1 = (new Random().nextInt((int) (totalTime * 0.2 - 1000 + 1)) + 1000);
				int randScreamer2 = (new Random().nextInt((int) (totalTime * 0.4 - totalTime * 0.2 + 1))
						+ (int) (totalTime * 0.2));
				int randScreamer3 = (new Random().nextInt((int) (totalTime * 0.6 - totalTime * 0.4 + 1))
						+ (int) (totalTime * 0.4));
				int randScreamer4 = (new Random().nextInt((int) (totalTime * 0.8 - totalTime * 0.6 + 1))
						+ (int) (totalTime * 0.6));
				int randScreamer5 = (new Random().nextInt((int) (totalTime - totalTime * 0.8 + 1))
						+ (int) (totalTime * 0.8));
				int[] randScreamers = { randScreamer1, randScreamer2, randScreamer3, randScreamer4, randScreamer5 };
				
				showScreamer(randScreamers, totalTime);
			} else
				labelGameText.setText("Hunt");
			gameIsOn = true;
			updateScore(0);
			globalmisses = 0;
			reloadAmmo(true);
			final int[] buttonsShot = {0};

			Thread thread = new Thread() {
				public void run() {
					for (int z = 0; z < buttonNumber; z++) {
						id++;

						final Button temp = new Button("Button " + id);
						temp.setId("" + id);
						temp.setText(" ");
						temp.setPrefSize(buttonSize, buttonSize);
						temp.setMaxSize(buttonSize, buttonSize);
						temp.setMinSize(buttonSize, buttonSize);
						temp.setStyle("-fx-background-color: #ffffff; ");

						temp.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (globalammo > 0) {
									Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
									updateScore(globalscore + 10);
									shootAmmo(true);
									buttonsShot[0]++;
								} else
									playSound(3);
							}
						});
						int randH = (new Random().nextInt(49 - 0 + 1) + 0);
						int randV = (new Random().nextInt(49 - 0 + 1) + 0);
						try {
							Thread.sleep(buttonTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Platform.runLater(() -> GameGridPane.add(temp, randH, randV));

						// delete button after some time
						Thread deletionThread = new Thread() {
							public void run() {
								try {
									Thread.sleep((long) (buttonTime * 0.7));
									FadeTransition ft = new FadeTransition(Duration.millis(((long) (buttonTime * 0.3))), temp);
									ft.setFromValue(1.0);
									ft.setToValue(0.0);
									ft.play();
									Thread.sleep((long) (buttonTime * 0.3));
									Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
									updateScore(globalscore - 1);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
						};
						deletionThread.start();

					}
					gameIsOn = false;
					if (!type.equals("horror")) { //hunt
						DialogBoxes.showMessageBox("Game Over", "Game mode: Hunt", "Score: " + globalscore + "\nMisses: " + globalmisses);
						//hunt easy cleared
						if(checkUnlockNumber(0)==0 && globalscore>=430)
							changeUnlockNumber(0,'1', subtype);
						//hunt medium cleared
						else if(checkUnlockNumber(0)==1 && globalscore>=519)
							changeUnlockNumber(0,'2', subtype);
						//hunt hard cleared
						else if(checkUnlockNumber(0)==2 && globalscore>=509)
							changeUnlockNumber(0,'3', subtype);
					}
					else { //horror
						DialogBoxes.showMessageBox("Game Over", "Game mode: Horror", "Score: " + globalscore + "\nMisses: " + globalmisses);
						//horror easy cleared
						if(checkUnlockNumber(3)==0 && globalscore>=440)
							changeUnlockNumber(3,'1', subtype);
						//horror medium cleared
						else if(checkUnlockNumber(3)==1 && globalscore>=529)
							changeUnlockNumber(3,'2', subtype);
						//horror hard cleared
						else if(checkUnlockNumber(3)==2 && globalscore>=519)
							changeUnlockNumber(3,'3', subtype);
					}
					nameField.setEditable(true);
					
					//Check for Highscore
					manageHighscore(globalscore, 60, globalmisses, "Buttons shot: " + buttonsShot[0], subtype);
				}
			};

			thread.start();
		}
	}

	private void startTimeMode(int buttonSize, String type, int subtype) {
		storePersistentData(unlockedGameModes, subtype);
		if (!gameIsOn) {
			startMusic();
			labelGameText.setText("Time mode");
				
			gameIsOn = true;
			updateScore(0);
			globalmisses = 0;
			reloadAmmo(true);
			final int[] buttonsShot = {0};
			final boolean[] buttonWasShot = {true};

			Thread thread = new Thread() {
				public void run() {
					
					long start = System.currentTimeMillis();
					
					while(true) {
						id++;

						final Button temp = new Button("Button " + id);
						temp.setId("buttonid" + id);
						temp.setText(" ");
						temp.setPrefSize(buttonSize, buttonSize);
						temp.setMaxSize(buttonSize, buttonSize);
						temp.setMinSize(buttonSize, buttonSize);
						temp.setStyle("-fx-background-color: #ffffff; ");

						temp.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (globalammo > 0) {
									Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
									updateScore(globalscore + 10);
									shootAmmo(true);
									buttonWasShot[0] = true;
									buttonsShot[0]++;
								} else
									playSound(3);
							}
						});
						
						//stop game after one minute:
						long finish = System.currentTimeMillis();
						long timeElapsed = finish - start;
						if(timeElapsed > 60000) {
							//remove button at the end
							Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
							break;
						}
						
						//add new button once the previous one was shot
						if(buttonWasShot[0]) {
							buttonWasShot[0] = false;
							int randH = (new Random().nextInt(49 - 0 + 1) + 0);
							int randV = (new Random().nextInt(49 - 0 + 1) + 0);
							Platform.runLater(() -> GameGridPane.add(temp, randH, randV));
						}
					}
					gameIsOn = false;
					DialogBoxes.showMessageBox("Game Over", "Game mode: Time",
							"Score: " + globalscore + "\nButtons shot: " + buttonsShot[0] + "\nMisses: " + globalmisses);
					nameField.setEditable(true);
					
					//Check for Highscore
					manageHighscore(globalscore, 60, globalmisses, "Buttons shot: " + buttonsShot[0], subtype);
					
					//time easy cleared
					if(checkUnlockNumber(1)==0 && globalscore>=706)
						changeUnlockNumber(1,'1', subtype);
					//time medium cleared
					else if(checkUnlockNumber(1)==1 && globalscore>=643)
						changeUnlockNumber(1,'2', subtype);
					//time hard cleared
					else if(checkUnlockNumber(1)==2 && globalscore>=608)
						changeUnlockNumber(1,'3', subtype);
				
					//remove every remaining buttons
					//fix for bug where 1 button remains at the end (click & time mode)
					ObservableList<Node> things = GameGridPane.getChildren();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							for (Node thing : things)
								if(thing!=null && thing.getId()!=null)
									if(thing.getId().startsWith("buttonid"))
										GameGridPane.getChildren().remove(thing);
						}
					});
				}
			};
			thread.start();
		}
	}
	
	private void startClickMode(int buttonSize, String type, int subtype) {
		storePersistentData(unlockedGameModes, subtype);
		if (!gameIsOn) {
			startMusic();
			labelGameText.setText("Time mode");
				
			gameIsOn = true;
			updateScore(0);
			globalmisses = 0;
			reloadAmmo(true);
			final int[] buttonsShot = {0};
			final boolean[] buttonWasShot = {true};

			Thread thread = new Thread() {
				public void run() {
					
					long start = System.currentTimeMillis();
					
					for (int z = 0; z < 61; z++) {
						id++;

						final Button temp = new Button("Button " + id);
						temp.setId("buttonid" + id);
						temp.setText(" ");
						temp.setPrefSize(buttonSize, buttonSize);
						temp.setMaxSize(buttonSize, buttonSize);
						temp.setMinSize(buttonSize, buttonSize);
						temp.setStyle("-fx-background-color: #ffffff; ");

						temp.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (globalammo > 0) {
									Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
									updateScore(globalscore + 10);
									shootAmmo(true);
									buttonWasShot[0] = true;
									buttonsShot[0]++;
								} else
									playSound(3);
							}
						});
						
						//add new button once the previous one was shot
						while(!buttonWasShot[0])
							try {Thread.sleep(100);} catch (InterruptedException e1) {e1.printStackTrace();}
						if(buttonWasShot[0]) {
							buttonWasShot[0] = false;
							int randH = (new Random().nextInt(49 - 0 + 1) + 0);
							int randV = (new Random().nextInt(49 - 0 + 1) + 0);
							Platform.runLater(() -> GameGridPane.add(temp, randH, randV));
						}
					}
							
					gameIsOn = false;
					long finish = System.currentTimeMillis();
					globalscore -= 3*((finish - start) / 1000); //3x Zeit in Sekunden abziehen
					double timeElapsed = (finish - start) / 1000.0;
					DialogBoxes.showMessageBox("Game Over", "Game mode: Click",
							"Score: " + globalscore + "\nTime: " + timeElapsed + "\nMisses: " + globalmisses);
					buttonWasShot[0] = true;
					nameField.setEditable(true);

					//Check for Highscore
					manageHighscore(globalscore, timeElapsed, globalmisses, "Buttons shot: " + buttonsShot[0], subtype);
					
					//click easy cleared
					if(checkUnlockNumber(2)==0 && globalscore>=440)
						changeUnlockNumber(2,'1', subtype);
					//click medium cleared
					else if(checkUnlockNumber(2)==1 && globalscore>=434)
						changeUnlockNumber(2,'2', subtype);
					//click hard cleared
					else if(checkUnlockNumber(2)==2 && globalscore>=407)
						changeUnlockNumber(2,'3', subtype);
					
					//remove every remaining buttons
					//fix for bug where 1 button remains at the end (click & time mode)
					ObservableList<Node> things = GameGridPane.getChildren();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							for (Node thing : things)
								if(thing!=null && thing.getId()!=null)
									if(thing.getId().startsWith("buttonid"))
										GameGridPane.getChildren().remove(thing);
						}
					});
				}
			};
			thread.start();
		}
	}

	private void startEndlessGame(int buttonSize, int buttonTime, int buttonNumber, String type, int subtype) {
		storePersistentData(unlockedGameModes, subtype);
		if (!gameIsOn) {

			startMusic();

			labelGameText.setText("Endless Game");
			gameIsOn = true;
			updateScore(1);
			globalmisses = 0;
			reloadAmmo(true);
			final int[] buttonsShot = {0};

			Thread thread = new Thread() {
				public void run() {
					int z = 0;
					int insidebuttonSize = buttonSize;
					int insidebuttonTime = buttonTime;
					int numberForATurn = 6;

					long startTime = System.currentTimeMillis();
					long estimatedTime = 0;

					while (true) {
						z++;
						if (z >= numberForATurn) {// 8,900
							if (insidebuttonSize > 9)
								insidebuttonSize--;
							if (insidebuttonTime > 950)
								insidebuttonTime = insidebuttonTime - 50;
							// numberForATurn = numberForATurn + 2;
							z = 0;
						}
						id++;

						final Button temp = new Button("Button " + id);
						temp.setId("" + id);
						temp.setText(" ");
						temp.setPrefSize(insidebuttonSize, insidebuttonSize);
						temp.setMaxSize(insidebuttonSize, insidebuttonSize);
						temp.setMinSize(insidebuttonSize, insidebuttonSize);

						int randColorNum = (new Random().nextInt(5 - 0 + 1) + 0);
						if (randColorNum == 0)
							temp.setStyle("-fx-background-color: #ffffff; "); // white
						if (randColorNum == 1)
							temp.setStyle("-fx-background-color: #ff0000; "); // Red
						if (randColorNum == 2)
							temp.setStyle("-fx-background-color: #ffff00; "); // Yellow
						if (randColorNum == 3)
							temp.setStyle("-fx-background-color: #6c8000; "); // green
						if (randColorNum == 4)
							temp.setStyle("-fx-background-color: #0085ff; "); // blue
						if (randColorNum == 5)
							temp.setStyle("-fx-background-color: #ffa500; "); // Orange

						temp.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if (globalammo > 0) {
									Platform.runLater(() -> GameGridPane.getChildren().remove(temp));
									updateScore(globalscore + 2);
									shootAmmo(true);
									buttonsShot[0]++;
								} else {
									playSound(3);
									globalmisses++;
								}
							}
						});
						int randH = (new Random().nextInt(49 - 0 + 1) + 0);
						int randV = (new Random().nextInt(49 - 0 + 1) + 0);
						try {
							Thread.sleep(insidebuttonTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Platform.runLater(() -> GameGridPane.add(temp, randH, randV));

						// delete button after some time
						Thread deletionThread = new Thread() {
							public void run() {
								try {
									int insideinsidebuttonTime = buttonTime;
									Thread.sleep((long) (insideinsidebuttonTime * 0.7));
									FadeTransition ft = new FadeTransition(
											Duration.millis(((long) (insideinsidebuttonTime * 0.3))), temp);
									ft.setFromValue(1.0);
									ft.setToValue(0.0);
									ft.play();
									Thread.sleep((long) (insideinsidebuttonTime * 0.3));
									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											GameGridPane.getChildren().remove(temp);
											updateScore(globalscore - 1);
											//globalmisses++;
										}
									});

								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
						};
						deletionThread.start();

						// Beende, wenn keine Score oder ammo übrig
						if (globalmisses > 0 || globalscore < 1) {
							estimatedTime = System.currentTimeMillis() - startTime;
							break;
						}
					}
					gameIsOn = false;
					double finalExactScore = (estimatedTime / 1000.0);
					if (globalmisses > 0 && globalscore > 0)
						finalExactScore = finalExactScore + globalscore / 2.5;
					double gameTime = (estimatedTime / 1000.0);
					String scoreText = "Score: " + finalExactScore + "\nTime: " + gameTime + "\nButtons shot: " + buttonsShot[0];
					DialogBoxes.showMessageBox("Game Over", "Game mode: Endless Game", scoreText);
					nameField.setEditable(true);
					
					//Check for Highscore
					manageHighscore(finalExactScore, gameTime, globalmisses, "Buttons shot: " + buttonsShot[0], 1);
				}
			};
			thread.start();
		}
	}

	public void updateScore(int score) {
		if (gameIsOn) {
			globalscore = score;
			Platform.runLater(() -> labelScoreText.setText("" + globalscore));
		}
	}

	public void showScreamer(int[] randScreamers, int totalTime) {
		Thread thread = new Thread() {
			public void run() {
				int scrNum = 0;
				int currentScreamer = randScreamers[scrNum];
				for (int i = 0; i < totalTime; i = i + 500) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (currentScreamer < i) {
						if (currentScreamer != randScreamers[4]) {
							scrNum++;
							currentScreamer = randScreamers[scrNum];
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Image bgImage = new Image(getClass().getResourceAsStream("/AhenobarbusHenocied.jpg"));
								imgView1.setImage(bgImage);
								imgView1.setVisible(true);
								greyPane.setVisible(false);
							}
						});
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								Image bgImage = new Image(
										getClass().getResourceAsStream("/AhenobarbusHenocied2.jpg"));
								imgView1.setImage(bgImage);
							}
						});
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								imgView1.setVisible(false);
								greyPane.setVisible(true);
							}
						});
						if (currentScreamer == randScreamers[4])
							break;
					}
				}
			}
		};
		thread.start();
	}

	@FXML
	public void reloadAmmoManually() {
		reloadAmmo(false);
	}

	public void reloadAmmo(boolean intitializeGame) {
		if (gameIsOn) {
			Image ammoImage = new Image(getClass().getResourceAsStream("/ammo.png"));
			ammo1.setImage(ammoImage);
			ammo2.setImage(ammoImage);
			ammo3.setImage(ammoImage);
			ammo4.setImage(ammoImage);
			ammo5.setImage(ammoImage);
			ammo6.setImage(ammoImage);
			ammo7.setImage(ammoImage);
			ammo8.setImage(ammoImage);
			ammo9.setImage(ammoImage);
			ammo10.setImage(ammoImage);

			globalammo = 10;
			playSound(4);

			if (!intitializeGame)
				globalscore = globalscore - 2;
		}
	}

	public void shootAmmo(boolean isNotOnInv) {
		if (gameIsOn) {
			if (!isNotOnInv && globalammo == 0)
				playSound(3);
			if (!isNotOnInv && globalammo > 0)
				playSound(2);
			else
				playSound(1);

			Image noammoImage = new Image(getClass().getResourceAsStream("/ammoempty.png"));

			if (globalammo >= 10) {
				ammo10.setImage(noammoImage);
				globalammo = 10;
			}
			if (globalammo == 9)
				ammo9.setImage(noammoImage);
			if (globalammo == 8)
				ammo8.setImage(noammoImage);
			if (globalammo == 7)
				ammo7.setImage(noammoImage);
			if (globalammo == 6)
				ammo6.setImage(noammoImage);
			if (globalammo == 5)
				ammo5.setImage(noammoImage);
			if (globalammo == 4)
				ammo4.setImage(noammoImage);
			if (globalammo == 3)
				ammo3.setImage(noammoImage);
			if (globalammo == 2)
				ammo2.setImage(noammoImage);
			if (globalammo == 1)
				ammo1.setImage(noammoImage);

			globalammo--;
		}
	}


	// Method changed to make it playable in a runnable JAR
	public void playSound(int soundType) {
		playerThread = new Thread() {
			public void run() {
				try {

					// FileInputStream fis = null;
					String soundName = "";

					if (soundType == 1)
						// fis = new FileInputStream("/shoothit.mp3");
						soundName = "shoothit";
					if (soundType == 2)
						// fis = new FileInputStream("/shootmiss.mp3");
						soundName = "shootmiss";
					if (soundType == 3)
						// fis = new FileInputStream("/shootnoammo.mp3");
						soundName = "shootnoammo";
					if (soundType == 4)
						// fis = new FileInputStream("/shootreload.mp3");
						soundName = "shootreload";
					if (soundType == 5)
						// fis = new FileInputStream("/fire.mp3");
						soundName = "fire";
					if (soundType == 6)
						// fis = new FileInputStream("/shoothotbuttonhit.mp3");
						soundName = "shoothotbuttonhit";
					// final Player player = new Player(fis);

					Info source = Port.Info.SPEAKER;
					Port outline = (Port) AudioSystem.getLine(source);
					outline.open();
					FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
					volumeControl.setValue(audioValue);

					final URL url = Main.class.getResource("/" + soundName + ".mp3");
					final Player player = new Player(url.openStream());

					// play, stop if closed
					player.play();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		playerThread.setDaemon(true);
		playerThread.start();
	}

	// Method changed to make it playable in a runnable JAR
	public void startMusic() {
		musicThread = new Thread() {
			public void run() {
				try {
					// FileInputStream fis = null;
					String musicStr = "BoxCat_Games_-_10_-_Epic_Song";
					int randMusicNum = (new Random().nextInt(10 - 0 + 1) + 0);

					if (randMusicNum == 0)
						// fis = new FileInputStream("/BoxCat_Games_-_10_-_Epic_Song.mp3");
						musicStr = "BoxCat_Games_-_10_-_Epic_Song";
					else if (randMusicNum == 1)
						// fis = new FileInputStream("/Jason_Shaw_-_Big_Car_Theft.mp3");
						musicStr = "Jason_Shaw_-_Big_Car_Theft";
					else if (randMusicNum == 2)
						// fis = new FileInputStream("/Jason_Shaw_-_Cycles.mp3");
						musicStr = "Jason_Shaw_-_Cycles";
					else if (randMusicNum == 3)
						// fis = new FileInputStream("/Jason_Shaw_-_Ecstasy_X.mp3");
						musicStr = "Jason_Shaw_-_Ecstasy_X";
					else if (randMusicNum == 4)
						// fis = new FileInputStream("/Jason_Shaw_-_Forever_Believe.mp3");
						musicStr = "Jason_Shaw_-_Forever_Believe";
					else if (randMusicNum == 5)
						// fis = new FileInputStream("/Jason_Shaw_-_Get_A_Move_On.mp3");
						musicStr = "Jason_Shaw_-_Get_A_Move_On";
					else if (randMusicNum == 6)
						// fis = new FileInputStream("/Jason_Shaw_-_Groovy_Baby.mp3");
						musicStr = "Jason_Shaw_-_Groovy_Baby";
					else if (randMusicNum == 7)
						// fis = new FileInputStream("/Jason_Shaw_-_Night_Rave.mp3");
						musicStr = "Jason_Shaw_-_Night_Rave";
					else if (randMusicNum == 8)
						// fis = new FileInputStream("/Jason_Shaw_-_Sk8board.mp3");
						musicStr = "Jason_Shaw_-_Sk8board";
					else if (randMusicNum == 9)
						// fis = new FileInputStream("/Jason_Shaw_-_The_Big_House.mp3");
						musicStr = "Jason_Shaw_-_The_Big_House";
					else
						// fis = new FileInputStream("/Jason_Shaw_-_Vanishing_Horizon.mp3");
						musicStr = "Jason_Shaw_-_Vanishing_Horizon";

					// final Player player = new Player(fis);

					final URL url = Main.class.getResource("/" + musicStr + ".mp3");
					final Player player = new Player(url.openStream());

					Info source = Port.Info.SPEAKER;
					Port outline = (Port) AudioSystem.getLine(source);
					outline.open();
					FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
					volumeControl.setValue(audioValue);

					// play, stop if game stopped
					while (player.play(1)) {
						if (!gameIsOn) {
							LockSupport.park();
						}
					}
					// play repeatedly
					if (!player.play(1) || !gameIsOn) {
						player.close();
						startMusic();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		musicThread.setDaemon(true);
		musicThread.start();
	}

	@FXML
	public void setAudioValueTo0() {
		audioSplitMenu.setText("Audio - 0");
		audioValue = 0.00F;
	}

	@FXML
	public void setAudioValueTo10() {
		audioSplitMenu.setText("Audio - 10");
		audioValue = 0.10F;
	}

	@FXML
	public void setAudioValueTo20() {
		audioSplitMenu.setText("Audio - 20");
		audioValue = 0.20F;
	}

	@FXML
	public void setAudioValueTo30() {
		audioSplitMenu.setText("Audio - 30");
		audioValue = 0.30F;
	}

	@FXML
	public void setAudioValueTo40() {
		audioSplitMenu.setText("Audio - 40");
		audioValue = 0.40F;
	}

	@FXML
	public void setAudioValueTo50() {
		audioSplitMenu.setText("Audio - 50");
		audioValue = 0.50F;
	}

	@FXML
	public void setAudioValueTo60() {
		audioSplitMenu.setText("Audio - 60");
		audioValue = 0.60F;
	}

	@FXML
	public void setAudioValueTo70() {
		audioSplitMenu.setText("Audio - 70");
		audioValue = 0.70F;
	}

	@FXML
	public void setAudioValueTo80() {
		audioSplitMenu.setText("Audio - 80");
		audioValue = 0.80F;
	}

	@FXML
	public void setAudioValueTo90() {
		audioSplitMenu.setText("Audio - 90");
		audioValue = 0.90F;
	}

	@FXML
	public void setAudioValueTo100() {
		audioSplitMenu.setText("Audio - 100");
		audioValue = 1.00F;
	}
	
	
	//persistent data (data/):
	/*
	 * 0 - Unlocked modes + last name used
	 * 1 - endless game
	 * 2 - hunt easy
	 * 3 - 		medium
	 * 4 - 		hard
	 * 5 - time easy
	 * 6 - 		medium
	 * 7 - 		hard
	 * 8 - click easy
	 * 9 - 		 medium
	 * 10- 		 hard
	 * 11- horror easy
	 * 12- 		  medium
	 * 13- 		  hard
	 * 
	 * Standard HS String: "a;1;1.0;0;a;a|rabbit;10;200.0;3;Fri Sep 07 12:33:27 CEST 1990;Can|fox;9;150.0;3;Fri Sep 07 12:33:27 CEST 1991;you|goat;8;130.0;3;Fri Sep 07 12:33:27 CEST 1992;beat|deer;7;120.0;2;Fri Sep 07 12:33:27 CEST 1993;these|wolf;6;110.0;2;Fri Sep 07 12:33:27 CEST 1994;high|goose;5;100.0;2;Fri Sep 07 12:33:27 CEST 1995;scores?|bear;4;90.0;1;Fri Sep 07 12:33:27 CEST 1996;Shouldn't|falcon;3;80.0;1;Fri Sep 07 12:33:27 CEST 1997;be|horse;2;70.0;0;Fri Sep 07 12:33:27 CEST 1998;too|sparrow;1;60.0;0;Fri Sep 07 12:33:27 CEST 1999;hard.|"
	 * -"- encrypted     : "BYWbEfX0BrDKLZrotzPkc4zbcbwCDLRTqbfqVooix2CIXA4V6hUB+woJpgFarKbsXL6cMtF4LCMAU5Ida8tScWFJzzZ7WtYo+K+l5Y9k9RGIXA4V6hUB+woJpgFarKbsQlM93fUyclykyPTpDxuHToB2WSiAp0Lwzq8lrE9aDlcJ8sotJ/SH7na0ep3ww0WmIGPrVHemjJrhASruXFgxQ3HfgPuP0kjrMEsVMO+RzXeLhvqMLSRJVskBltmsCgA2KtZ3g+1a+9GFIMZ3Cmdri6ICnEW2IsOTyIO0FueT3zE3zKxdcynkuFvlge/UUCMWYCioOCSColodJXhysdoRTWbfYPkwNyw7Uowh2kDo1ceZulNqONscQj/EuFOtTn+qND3MfYZF1x9Q4S7/bnzjfyv/29nO5FBLn9DqmARNsGzhpOy0Dx08NEMPyAalmuDDSyiDZOL+HkpWDx+7TPZW5+rKZrOMhskFQJxFrjmfU9oCZehsB/t3jluo7IjQ6INXtkOapsDmTEb6j5JyReuNBT5tEFFfM0YbLUhp2nNlnG32G/dOOy0IKq2CIxfXbtz3ENVbtav2a2K9btGP5jnBmz5tEFFfM0YbLUhp2nNlnG1yd2KdLUW3FX8xheD9rMh5/kG8CnD3a2ccN6T6EtV2KPUhdoi3u9LpRlxxvP476oaxzCDm0L5dt+iFsIRMfnYY"
	 * 
	 * Standard data String: "0000||1"  //unlocked modes | last username | game mode
	 * -"- encrypted	   : "HBdbrFxRJy0r3tFTihnl3A=="
	 */
	
	//show hs once a game is over
	
	int newPlace = 11;
	String[] HSlines;
	final String secretKey = "|,£0²hQsµøÈ¹»8Ú¼qñ0TÚÁ®Ø`jî«´è7^Ý}ç";

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
	
	public void storePersistentData(String unlockedModes, int gameMode) {

		//create data String and handle forbidden characters (| and ;) that would corrupt the persistent file
		String dataString = unlockedModes + "|" + nameField.getText().replace("\\|", " ").replace(";", ",") + "|" + gameMode;
		try {
			String highscoreStringEncr = AES.encrypt(dataString, secretKey);
			PrintWriter writer;
			writer = new PrintWriter("data/0", "UTF-8");
			writer.println(highscoreStringEncr);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			DialogBoxes.showErrorBox("Error", "\"Hot Button\" has encountered a problem - could not store data!", e.getMessage());
		}
	}
	
	public void manageHighscore(double score, double time, int misses, String scoreText, int gameMode) {

		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		Date datetext = calendar.getTime();

		Scanner in;
		try {
			//read and decrypt highscore file
			in = new Scanner(new FileReader("data/" + gameMode));
			StringBuilder sb = new StringBuilder();
			while (in.hasNext()) {
				sb.append(in.next());
			}
			in.close();
			String highscoreString = sb.toString();
			String outString = AES.decrypt(highscoreString, secretKey);

			// read & check HS file
			int newPlaceAchieved = 0;
			newPlace = 11;
			HSlines = outString.split("\\|");
			for (int i = 1; i < 11; i++) {
				String[] HSentries = HSlines[i].split(";");
				for (int j = 0; j < 5; j++) {
					if (j == 1) {
						// higher score achieved:
						if (Double.parseDouble(HSentries[j]) < score && newPlaceAchieved == 0) {
							HSlines[i - 1] = "username" + ";" + score + ";" + time + ";" + misses + ";" + datetext + ";"
									+ scoreText;
							newPlaceAchieved = 1;
							newPlace = i;
						} else {
							HSlines[i - 1] = HSlines[i - newPlaceAchieved];
						}
					}
				}
			}

			// write to HS file
			if (newPlaceAchieved == 1) {
				DialogBoxes.showMessageBox("High Score cracked", "Place " + newPlace + " achieved!", "");
				storeScore(scoreText, gameMode);
			}

		} catch (FileNotFoundException e) {
			DialogBoxes.showErrorBox("Error", "\"Hot Button\" has encountered a problem.", e.getMessage());
		}
	}

	//store highscore data persistently
	public void storeScore(String scoreText, int gameMode){ 
		try {
			String scoreName = nameField.getText().replace("\\|", " ").replace(";", ",");

			//insert name into score data
			int currentPlace=newPlace-1;
			//double localScore = Double.parseDouble((HSlines[currentPlace].split(";"))[1]);
			HSlines[currentPlace] = HSlines[currentPlace].replace("username", scoreName);

			String highscoreString = "a;1;1.0;0;a|";
			for(int i=0; i<10; i++)
				highscoreString+=(HSlines[i]+"|");
			String highscoreStringEncr = AES.encrypt(highscoreString, secretKey);
			PrintWriter writer;
			writer = new PrintWriter("data/"+gameMode, "UTF-8");
			writer.println(highscoreStringEncr);
			writer.close();

			//show Highscore
			HighscoreManager mHighscoreManager = new HighscoreManager();
			//mHighscoreManager.setGameMode(gameMode);
			mHighscoreManager.openScoreview();

			//DialogBoxes.showMessageBox("Game Over", "", scoreText + "Score: " + localScore); -> redundant, wird davor ausgegeben.

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			DialogBoxes.showErrorBox("Error", "\"Hot Button\" has encountered a problem.", e.getMessage());
		}
	}
	
	//Change this number when a game mode gets unlocked
	public void changeUnlockNumber(int pos, char digit, int gameMode) {
		char[] charArray = unlockedGameModes.toCharArray();
	    charArray[pos] = digit;
	    unlockedGameModes = new String(charArray);
		DialogBoxes.showMessageBox("Level Completed", "Level Completed", "Finish levels to unlock more game modes!");
		storePersistentData(unlockedGameModes, gameMode);
		unlockGameModes();
	}
	//Before doing this, check the current number
	public int checkUnlockNumber(int pos) {
		char[] charArray = unlockedGameModes.toCharArray();
		return Character.getNumericValue(charArray[pos]);
	}
	
	//Check unlock number and enable buttons
	public void unlockGameModes() {
		char[] digits = unlockedGameModes.toCharArray();

		int huntUnlockVal = Character.getNumericValue(digits[0]);
		int timeUnlockVal = Character.getNumericValue(digits[1]);
		int clickUnlockVal = Character.getNumericValue(digits[2]);
		int horrorUnlockVal = Character.getNumericValue(digits[3]);	
		
		//Enable buttons if modes were unlocked
		if(huntUnlockVal>0) {
			huntmedbtn.setDisable(false);
			clickeasybtn.setDisable(false);
			timeeasybtn.setDisable(false);
			horroreasybtn.setDisable(false);
		}
		if(huntUnlockVal>1)
			hunthardbtn.setDisable(false);

		if(timeUnlockVal>0)
			timemedbtn.setDisable(false);
		if(timeUnlockVal>1)
			timehardbtn.setDisable(false);

		if(clickUnlockVal>0)
			clickmedbtn.setDisable(false);
		if(clickUnlockVal>1)
			clickhardbtn.setDisable(false);

		if(horrorUnlockVal>0)
			horrormedbtn.setDisable(false);
		if(huntUnlockVal>2 && timeUnlockVal>2 && clickUnlockVal>2 && horrorUnlockVal>1)
			horrorhardbtn.setDisable(false);
		
		if(huntUnlockVal>0 && timeUnlockVal>0 && clickUnlockVal>0 && horrorUnlockVal>0)
			endgamebtn.setDisable(false);
	}
}
