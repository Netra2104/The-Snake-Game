package starter.graphical;

import java.util.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;
import java.util.Random;

public class Main extends Application {
    static int factor = 25;
    static List<Grow> snake = new ArrayList<>();
    static Direction direction = Direction.right;
    public static Direction nextDirection = Direction.right;
    static boolean gameOver = false;
    static Random rand = new Random();
    public static Integer randomIntX[] = new Integer[15];
    public static Integer randomIntY[] = new Integer[15];
    static Integer allX1[] = {2, 6, 1, 7, 4};
    static Integer allY1[] = {14, 16, 3, 15, 6};
    static Integer allX2[] = {2, 6, 1, 7, 4, 8, 3, 10, 5, 9};
    static Integer allY2[] = {14, 16, 3, 15, 6, 2, 6, 1, 7, 4};
    static Integer allX3[] = {2, 6, 1, 7, 4, 8, 3, 10, 5, 9, 14, 11, 15, 13, 12};
    static Integer allY3[] = {14, 16, 3, 15, 6, 2, 6, 1, 7, 4, 9, 11, 10, 5, 8};
    static int wid = 30;
    static int tall = 26;
    public static int currentScreen;
    enum SCENES {SCENE1, SCENE2, SCENE5}
    static int applesEaten = 0;
    public static int score = 0;
    static int level = 1;
    public static Scene scene1, scene2, scene5;
    static int time30 = 30;
    static int duration = 10;
    static Label timeLabel = new Label("0");
    public static Boolean pause = false;
    int count = 0;
    public static double dur = 0;
    public static int timerCount = 0;
    public static int speed = 3;

    public enum Direction {
        up, down, left, right,
    }

    public static class Grow { int x; int y;
        public Grow(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void start(Stage stage) {
        stage.setTitle("Snake Game");
        StackPane pages = splashScreen();
        scene1 = new Scene(pages, 750, 650);
        currentScreen = 0;
        // User input on the splash screen
        scene1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DIGIT1) {
                currentScreen = 1;
                setScene(stage, SCENES.SCENE2);
                setLevel(1);
            }
            else if (event.getCode() == KeyCode.DIGIT2) {
                currentScreen = 2;
                setScene(stage, SCENES.SCENE2);
                setLevel(2);
            }
            else if (event.getCode() == KeyCode.DIGIT3) {
                currentScreen = 3;
                setScene(stage, SCENES.SCENE2);
                setLevel(3);
            }
            else if (event.getCode() == KeyCode.Q) {
                currentScreen = 4;
                gameOver = true;
            }
        });

        try {
            VBox sceneBox = new VBox();
            Canvas can = new Canvas(750, 650);
            GraphicsContext gc = can.getGraphicsContext2D();
            sceneBox.getChildren().add(can);
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            dur = (1.0/10);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(dur),
                            actionEvent -> {
                                timerCount++;
                                count++;
                                // update the timer on the screen
                                if(timerCount == 10) {
                                    time30--;
                                    if (level != 3) {
                                        timeLabel.setText(
                                                Integer.toString(time30));
                                    }
                                    if (level == 3) {
                                        timeLabel.setText("Unlimited!");
                                    }
                                    timerCount = 0;
                                }
                                // Switch to the next level after 30 seconds
                                if(count == speed) {
                                    count = 0;
                                    if (time30 <= 0) {
                                        if (level == 1) {
                                            setLevel(2);
                                        } else if (level == 2) {
                                            setLevel(3);
                                        }
                                        time30 = 30;
                                    }
                                    if(currentScreen != 0) {
                                        snakeActions(gc, stage);
                                    }
                                }
                            }));
            // play 30s of animation
            timeline.playFromStart();
            
            scene2 = new Scene(sceneBox, 750, 650);
            controls(scene2); // controls to movement the snake
            // all possible user input from the main page of the game
            scene2.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.DIGIT2) {
                    currentScreen = 2;
                    setLevel(2);
                } else if (event.getCode() == KeyCode.DIGIT3) {
                    currentScreen = 3;
                    setLevel(3);
                } else if (event.getCode() == KeyCode.DIGIT1) {
                    currentScreen = 1;
                    setLevel(1);
                } else if (event.getCode() == KeyCode.Q) {
                    currentScreen = 5;
                    gameOver = true;
                } else if (event.getCode() == KeyCode.R) {
                    doReset(stage, scene2);
                } else if (event.getCode() == KeyCode.P) {
                    if (pause == false) {
                        timeline.pause();
                        pause = true;
                    } else if (pause == true) {
                        timeline.play();
                        pause = false;
                    }
                }
            });
            //Set the first scene as splash page
            setScene(stage, SCENES.SCENE1);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // set the speed and the number of apples for a level
    public static void setLevel(int setLevel) {
        level = setLevel;
        if(level == 1) {
            for(int i = 0; i < allX1.length; i++) {
                randomIntX[i] = allX1[i];
            }
            for(int i = 0; i < allY1.length; i++) {
                randomIntY[i] = allY1[i];
            }
            time30 = 30;
        }
        else if (level == 2) {
            for(int i = 0; i < allX2.length; i++) {
                randomIntX[i] = allX2[i];
            }
            for(int i = 0; i < allY2.length; i++) {
                randomIntY[i] = allY2[i];
            }
            time30 = 30;
        }
        else if (level == 3) {
            for(int i = 0; i < allX3.length; i++) {
                randomIntX[i] = allX3[i];
            }
            for(int i = 0; i < allY3.length; i++) {
                randomIntY[i] = allY3[i];
            }
        }
    }
    
    // processing snake's movement and actions during the animation
    public static void snakeActions(GraphicsContext gc, Stage stage) {
        if(level == 1) {
            speed = 4;
        }
        if(level == 2) {
            speed = 3;
        }
        if(level == 3) {
            speed = 2;
        }
        if (gameOver) {
            if (currentScreen != 0) {
                StackPane pages1 = gameOverScreen(score);
                scene5 = new Scene(pages1, 750, 650);
                setScene(stage, SCENES.SCENE5);
                scene5.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.R) {
                        doReset(stage, scene2);
                    }
                });
            }
        }
        direction = nextDirection;
        // update the snake x and y coordinate during the animation
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }
        checkBoundary(direction);

        // snake eats the food
        for (int i = 0; i < level * 5; i++) {
            if (randomIntX[i] == snake.get(0).x && randomIntY[i] == snake.get(0).y) {
                String sound = Main.class.getClassLoader().getResource("click.mp3").toString();
                AudioClip clip = new AudioClip(sound);
                clip.play();
                snake.add(new Grow(-1, -1));
                applesEaten++;
                score = applesEaten * level * 2;
                randomIntY[i] = rand.nextInt(24); //25
                randomIntX[i] = rand.nextInt(23); // 29
                if(randomIntY[i] <= 5) {
                    randomIntY[i] += 5;
                }
                if(randomIntX[i] <= 5) {
                    randomIntX[i] += 5;
                }
                System.out.println(randomIntX[i] + ", " +randomIntY[i]);
            }
        }
        // snake eats itself
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // setting the stage
        Image img = new Image(String.valueOf(Main.class.getClassLoader().getResource("images.jpg")));
        gc.setFill(new ImagePattern(img));
        gc.fillRect(0, 0, 750, 650);
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 20));
        gc.fillText("Score: " + score, 10, 30);
        gc.fillText("Apples Eaten: " + applesEaten, 200, 30);
        gc.fillText("Time: " + timeLabel.getText(), 550, 30);
        gc.fillText("Level: " + level, 400, 30);

        for (int i = 0; i < level * 5; i++) {
            Image fruit1 = new Image(String.valueOf(Main.class.getClassLoader().getResource("apple.png")));
            gc.setFill(new ImagePattern(fruit1));
            gc.fillOval(randomIntX[i] * factor, randomIntY[i] * factor, factor, factor);
        }

        //draw the snake
        for (Grow len : snake) {
            Image snake = new Image(String.valueOf(Main.class.getClassLoader().getResource("snake.png")));
            gc.setFill(new ImagePattern(snake));
            gc.fillRect(len.x * factor, len.y * factor, factor - 1, factor - 1);
        }
    }

    //switch scenes
    public static void setScene(Stage stage, SCENES scene) {
        switch (scene) {
            case SCENE1:
                stage.setTitle("Splash Screen");
                stage.setScene(scene1);
                break;
            case SCENE2:
                stage.setTitle("Level");
                stage.setScene(scene2);
                break;
            case SCENE5:
                stage.setTitle("Game Over");
                stage.setScene(scene5);
                break;
        }
    }

    // changing snake's direction
    public static void controls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.LEFT) {
                if (direction == Direction.up) {
                    nextDirection = Direction.left;
                } else if (direction == Direction.right) {
                    nextDirection = Direction.up;
                } else if (direction == Direction.down) {
                    nextDirection = Direction.right;
                } else if (direction == Direction.left) {
                    nextDirection = Direction.down;
                }
            }
            if (key.getCode() == KeyCode.RIGHT) {
                if (direction == Direction.up) {
                    nextDirection = Direction.right;
                } else if (direction == Direction.right) {
                    nextDirection = Direction.down;
                } else if (direction == Direction.down) {
                    nextDirection = Direction.left;
                } else if (direction == Direction.left) {
                    nextDirection = Direction.up;
                }
            }

        });
    }

    // creating the graphics for the splash screen
    public static StackPane splashScreen() {
        snake.add(new Grow(wid / 2, tall / 2));
        snake.add(new Grow(wid / 2, tall / 2));
        snake.add(new Grow(wid / 2, tall / 2));
        String instructions = "Welcome to the Snake Game!\nHere is how to play:\nThe direction of the snake can be controlled by the arrows keys.\nThe objective of the snake is to eat the fruit.\nEvery time the snake eats a piece of fruit, it gets one block longer.\nA timer ticks down on each level. When the timer runs out, the next level is loaded.\nThe snake can die by eating itself or hitting the edge of the screen.\n P: Pause\n Q: Quit\n 1/2/3: Level 1 / Level 2 / Level 3\n R: Reset\n L: Snake goes left\n R: Snake goes right";
        Group mainpage = new Group();
        Rectangle back = new Rectangle(0, 0, 750, 650);
        Image img = new Image(String.valueOf(Main.class.getClassLoader().getResource("images.jpg")));
        back.setFill(new ImagePattern(img));
        mainpage.getChildren().add(back);
        Text title = new Text("Netra Mali\n20772959 \n\n" + instructions);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(new Font(16));
        title.setFill(Color.WHITE);
        StackPane pages = new StackPane();
        pages.getChildren().add(mainpage);
        pages.getChildren().add(title);
        return pages;
    }

    // setting variables when game resets
    public static void doReset(Stage stage, Scene scene) {
        currentScreen = 0;
        setScene(stage, SCENES.SCENE1);
        score = 0;
        applesEaten = 0;
        snake = new ArrayList<>();
        snake.add(new Grow(wid / 2, tall / 2));
        snake.add(new Grow(wid / 2, tall / 2));
        snake.add(new Grow(wid / 2, tall / 2));
        pause = false;
        level = 0;
        time30 = 30;
        gameOver = false;
        duration = 10;
    }

    // creating graphics for the game over screen
    public static StackPane gameOverScreen(Integer score) {
        String sound = Main.class.getClassLoader().getResource("click.mp3").toString();
        AudioClip clip = new AudioClip(sound);
        clip.play();
        Group mainPage = new Group();
        Rectangle back = new Rectangle(0, 0, 750, 650);
        Image img = new Image(String.valueOf(Main.class.getClassLoader().getResource("images.jpg")));
        back.setFill(new ImagePattern(img));
        mainPage.getChildren().add(back);
        Text title = new Text("Game Over! \n\n" + "HighScore: " + score +"\n\n Please press R to restart the game!");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(new Font(25));
        title.setFill(Color.WHITE);
        StackPane pages = new StackPane();
        pages.getChildren().add(mainPage);
        pages.getChildren().add(title);
        return pages;
    }

    // checking if snake has hit one of the boundaries
    public static void checkBoundary(Direction direction) {
        if(direction == Direction.left) {
            snake.get(0).x--;
            if (snake.get(0).x - 1 < 0) {
                gameOver = true;
            }
        }
        else if(direction == Direction.down) {
            snake.get(0).y++;
            if (snake.get(0).y + 2 > tall) {
                gameOver = true;
            }
        }
        else if(direction == Direction.right) {
            snake.get(0).x++;
            if (snake.get(0).x + 2 > wid) {
                gameOver = true;
            }
        }
        else if(direction == Direction.up) {
            snake.get(0).y--;
            if (snake.get(0).y - 1 < 0) {
                gameOver = true;
            }
        }
    }
}
