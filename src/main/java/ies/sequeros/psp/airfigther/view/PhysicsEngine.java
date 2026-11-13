package ies.sequeros.psp.airfigther.view;

import ies.sequeros.psp.airfigther.GameBoard;
import ies.sequeros.psp.airfigther.engine.GameContext;
import ies.sequeros.psp.airfigther.engine.GameStatus;

public class PhysicsEngine extends Thread{
    private static final int TARGET_UPS = 60;
    private static final long OPTIMAL_TIME = 1000000000L / TARGET_UPS;
    private GameContext gameContext;
    private GameBoard gameBoard;

    public PhysicsEngine(GameBoard gameBoard, GameContext gameContext) {
        this.gameContext = gameContext;
        this.gameBoard = gameBoard;
    }

    public void run() {
        long lastUpdateTime = System.nanoTime();
        while (gameContext.getStatus() != GameStatus.GAME_OVER) {
            long now = System.nanoTime();
            long updateLength = now - lastUpdateTime;
            lastUpdateTime = now;

            gameBoard.processInput();   // siempre: la tecla P es la que sale del menú y de la pausa
            if (gameContext.getStatus() == GameStatus.PLAYING) {
                gameBoard.update(updateLength);   // now - lastUpdateTime esta mal porque siempre es 0
            }


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
}
