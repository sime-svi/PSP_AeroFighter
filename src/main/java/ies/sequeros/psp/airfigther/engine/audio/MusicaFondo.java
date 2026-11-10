package ies.sequeros.psp.airfigther.engine.audio;

import ies.sequeros.psp.airfigther.GameBoard;
import ies.sequeros.psp.airfigther.engine.GameContext;
import ies.sequeros.psp.airfigther.engine.GameStatus;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileNotFoundException;
import java.io.InputStream;

//aqui no va el demon porque no se ejecuta el finally,
// añadir en el de efectos sonoros
public class MusicaFondo extends Thread {
    private GameContext gameContext;

    public MusicaFondo(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void run() {
        InputStream audioStream = null;
        String ruta = "/song1.mp3";
        AdvancedPlayer player = null;
        try {
            audioStream = getClass().getResourceAsStream(ruta);
            if (audioStream == null) {
                throw new FileNotFoundException("No se encontró el audio en:" + ruta);
            }
            if (player == null) {
                player = new AdvancedPlayer(audioStream);
                // Reproduce desde el frame guardado
                player.play(); //0, Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            if (gameContext.getStatus() != GameStatus.GAME_OVER) {
                e.printStackTrace();
            }
        } finally { // POR ESTE MÓTIVO NO SE PUEDE HACER DAEMON. NO SE EJECUTARÍA AL CERRAR
            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (Exception ignored) {
                }
            }
            player = null;
        }
    }
}