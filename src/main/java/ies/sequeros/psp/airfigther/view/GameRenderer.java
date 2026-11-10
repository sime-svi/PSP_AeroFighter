package ies.sequeros.psp.airfigther.view;

import ies.sequeros.psp.airfigther.GameBoard;
import ies.sequeros.psp.airfigther.engine.GameContext;
import ies.sequeros.psp.airfigther.engine.GameStatus;
import ies.sequeros.psp.airfigther.model.Bullet;
import ies.sequeros.psp.airfigther.model.Element;
import ies.sequeros.psp.airfigther.model.Player;
import ies.sequeros.psp.airfigther.model.enemy.Enemy;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameRenderer extends Thread {
    private static final int TARGET_UPS = 60;
    private static final long OPTIMAL_TIME = 1000000000L / TARGET_UPS; // Nanosegundos por tick (aprox 16.6ms)
    private final JPanel gamePanel;
    private final Lock lock = new ReentrantLock();
    private final HUD hud = new HUD();


    private GameContext gameContext;
    private GameBoard gameBoard;

    public GameRenderer(
                        GameContext gameContext,
                        GameBoard gameBoard,
                        JPanel gamePanel
    ) {

        this.gameContext = gameContext;
        this.gameBoard = gameBoard;
        this.gamePanel = gamePanel;
        this.setDaemon(true);
    }



    public void run() {
        long lastUpdateTime = System.nanoTime();
        while (gameContext.getStatus() != GameStatus.GAME_OVER) {
            long now = System.nanoTime();
            long updateLength = now - lastUpdateTime;
            lastUpdateTime = now;

            gamePanel.repaint();


            long processingTime = System.nanoTime() - lastUpdateTime;
            long sleepTime = (OPTIMAL_TIME - processingTime) / 1000000L; // Convertir a milisegundos
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    private void renderBackground(Graphics g,int width,int height) {
        ScrollingBackground background = gameBoard.getScrollingBackground();
        if (background != null) {
            int anchoImagen = background.getWidth() << 1;
            int altoPanel = background.getHeight() << 1;
            g.drawImage(background.getBackgroundImage(),
                    background.getXPosition(), 0,
                    anchoImagen, altoPanel,
                    null);

            g.drawImage(background.getBackgroundImage(),
                    background.getXPosition() + anchoImagen,
                    0, anchoImagen,
                    altoPanel, null
            );
        } else {
            //  por si falla la imagen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
        }
    }
    private void renderPlayer(Graphics g) {
        Player p = gameBoard.getPlayer();
        g.drawImage(ImageResource.PLAYER.getImage(),
                (int) p.getPosition().getX(),
                (int) p.getPosition().getY(),
                p.getSize().getWidth(), p.getSize().getHeight(), null
        );
    }

    private void renderEnemy(Graphics g) {
        for (Element element : gameBoard.getElements()) {
            if (element instanceof Enemy e) {
                g.drawImage(EnemySpriterMapper.getImageResource(e.getType()).getImage(),
                        (int) e.getPosition().getX(),
                        (int) e.getPosition().getY(),
                        e.getSize().getWidth(), e.getSize().getHeight(), null
                );
            }
        }
    }
    public void render(Graphics g) {
        int width = this.gameBoard.getArea().width;
        int height = this.gameBoard.getArea().height;
        String subText = "";
        int textWidthSub = 0;
        if (this.gameBoard != null) {

            this.renderBackground(g,width,height);

            //dibujar los elementos
            this.renderPlayer(g);
            this.renderEnemy(g);

            //Manejo de contexto
            switch (gameContext.getStatus()) {
                case MENU:
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 16));
                    subText = "Menu";
                    textWidthSub = g.getFontMetrics().stringWidth(subText);
                    g.drawString(subText, (width - textWidthSub) / 2, (height / 2) + 40);
                    break;
                case PAUSED:
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 16));
                    subText = "Pausa";
                    textWidthSub = g.getFontMetrics().stringWidth(subText);
                    g.drawString(subText, (width - textWidthSub) / 2, (height / 2) + 40);
                    break;
                case PLAYING:
                    this.hud.render(g, gameBoard.getPlayer(), gameContext, gameBoard.getArea().width );
                    if (gameContext.isDebugMode()) {
                        renderDebug(g);
                    }
                    break;

            }
            g.setColor(Color.BLACK);

            //DIBUJAR LOS DIFERENTES TEXTOS EN FUNCIÓN DEL ESTADO
            //MENU-> PULSAR P PARA INICIAR
            //PAUSED -> TEXTO PAUSED
            //PLAYING->MOSTRAR EL HUD

        }
    }

    public void renderDebug(Graphics g) {
        Player p = gameBoard.getPlayer();
        g.setColor(Color.GREEN);
        g.drawRect((int) p.getPosition().getX() , (int) p.getPosition().getY(), p.getSize().getWidth(), p.getSize().getHeight());
        for (Element e : gameBoard.getElements()) {
            g.setColor(Color.RED);
            g.drawRect((int) e.getPosition().getX() , (int) e.getPosition().getY(), e.getSize().getWidth(), e.getSize().getHeight());
        }
    }

}