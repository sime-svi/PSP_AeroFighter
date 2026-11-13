package ies.sequeros.psp.airfigther.engine.audio;

import ies.sequeros.psp.airfigther.engine.GameContext;
import ies.sequeros.psp.airfigther.engine.GameStatus;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EfectosSonoros extends Thread {
    private GameContext gameContext;
    private ExecutorService executorService;

    public EfectosSonoros(GameContext gameContext) {
        this.gameContext = gameContext;
        this.executorService = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r, "efecto-sonoro");
            t.setDaemon(true);
            return t;
        });
        this.setName("EfectosSonoros");
        this.setDaemon(true);
    }

    public void run() {
        try {
            while (true) {
                // Espera como máximo 100 ms a que llegue un efecto. Con take se quedaría bloqueado para siempre
                SoundEffect effect = gameContext.getSoundQueue().poll(100, TimeUnit.MILLISECONDS);

                if (effect != null) {
                    executorService.execute(() -> reproducir(effect));
                } else if (gameContext.getStatus() == GameStatus.GAME_OVER) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }

    private void reproducir(SoundEffect effect) {
        // getInputStream crea un stream nuevo desde la RAM en cada llamada,
        // por eso varios hilos pueden reproducir el mismo efecto a la vez
        try (InputStream in = effect.getInputStream()) {
            AdvancedPlayer player = new AdvancedPlayer(in);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}