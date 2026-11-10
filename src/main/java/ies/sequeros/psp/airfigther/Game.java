package ies.sequeros.psp.airfigther;

import ies.sequeros.psp.airfigther.base.Point2D;
import ies.sequeros.psp.airfigther.base.Size;
import ies.sequeros.psp.airfigther.base.Speed;
import ies.sequeros.psp.airfigther.engine.GameContext;
import ies.sequeros.psp.airfigther.engine.GameStatus;

import ies.sequeros.psp.airfigther.engine.audio.MusicaFondo;
import ies.sequeros.psp.airfigther.input.KeyBoardStatus;
import ies.sequeros.psp.airfigther.model.Player;
import ies.sequeros.psp.airfigther.view.GameRenderer;
import ies.sequeros.psp.airfigther.view.PhysicsEngine;
import ies.sequeros.psp.airfigther.view.ScrollingBackground;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Game extends JPanel {
    private static final int TARGET_UPS = 120;
    private static final long OPTIMAL_TIME = 1000000000L / TARGET_UPS; // Nanosegundos por tick (aprox 16.6ms)
    public static int WIDTH = 480 * 2;
    public static int HEIGHT = 240 * 2;

    private final GameBoard gameBoard;
    private final GameRenderer gameRenderer;
    private final PhysicsEngine pyshicsEngine;
    private final MusicaFondo musicaFondo;
    //private volatile Player player;


    private JFrame windows;
    private GameContext gameContext = new GameContext(GameStatus.MENU, false);
    private KeyBoardStatus keyboardStatus;

    public Game(int enemyCreateProbability,

                int enemyShootProbability,
                Rectangle area) {

        this.configWindows();
        this.keyboardStatus = new KeyBoardStatus();
        Player tempo = new Player(
                "player",
                new Point2D(area.getCenterX(), area.getCenterY()),
                new Size(68, 38),
                new Speed(0, 0),
                area
        );
        //CREAR EL TABLERO, CONTIENE LOS ELEMENTOS DEL JUEGO
        this.gameBoard = new GameBoard(keyboardStatus, tempo,
                new ScrollingBackground("/bg4.png"),
                enemyCreateProbability,
                enemyShootProbability,
                area,
                this.gameContext);
        //CREAR E INICIAR LOS HILOS
        //HILO PARA PINTAR
        //HILO MUSICA DE FONDO
        //HILO EFECTOS SONIDO
        //HILO DE LA FISICA
        this.gameRenderer = new GameRenderer(
                this.gameContext, this.gameBoard, this);

        // Iniciar los hilos
        this.pyshicsEngine = new PhysicsEngine(this.gameBoard , this.gameContext);
        this.musicaFondo = new MusicaFondo(this.gameContext);

        this.pyshicsEngine.start();
        this.gameRenderer.start();
        this.musicaFondo.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.gameRenderer != null) {
            this.gameRenderer.render(g);
        }
    }


    private void configWindows() {
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.windows = new JFrame("Air Fight");
        windows.add(this);
        windows.setSize(WIDTH, HEIGHT + 36);
        windows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windows.setVisible(true);
        this.windows.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyboardStatus.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyboardStatus.keyReleased(e);
            }
        });
        windows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void exit() {
        this.gameContext.setStatus(GameStatus.GAME_OVER);
        try {

            if (gameRenderer != null)
                gameRenderer.join();
            System.out.println("fin de juego");
        System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Game game = new Game(7,  2, new Rectangle(0, 0,
                WIDTH,
                HEIGHT));
    }
}