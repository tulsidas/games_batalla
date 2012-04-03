package batalla.client;

import static pulpcore.image.Colors.WHITE;
import static pulpcore.image.Colors.rgb;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pulpcore.CoreSystem;
import pulpcore.Input;
import pulpcore.Stage;
import pulpcore.animation.Easing;
import pulpcore.animation.Timeline;
import pulpcore.animation.event.TimelineEvent;
import pulpcore.image.CoreFont;
import pulpcore.image.CoreImage;
import pulpcore.scene.Scene;
import pulpcore.sound.Sound;
import pulpcore.sprite.Button;
import pulpcore.sprite.Group;
import pulpcore.sprite.ImageSprite;
import pulpcore.sprite.Label;
import pulpcore.sprite.TextField;
import batalla.common.ifaz.GameHandler;
import batalla.common.messages.client.DisparoMessage;
import batalla.common.messages.client.PosicionesBarcoMessage;
import batalla.common.model.Barco;
import batalla.common.model.BatallaRoom;
import batalla.common.model.Celda;
import batalla.common.model.Posicion;
import batalla.common.model.Resultado;
import client.InGameChatArea;
import client.PingScene;
import client.PopUp;
import client.PulpcoreUtils;
import client.DisconnectedScene.Reason;

import common.game.AbandonRoomMessage;
import common.game.ProximoJuegoMessage;
import common.messages.chat.RoomChatMessage;
import common.model.AbstractRoom;
import common.model.User;

public class BatallaScene extends PingScene implements GameHandler {

    private static final int[] RANDOM_COLORS = new int[] { rgb(0x00e300),
            rgb(0xf6f300), rgb(0x00bff6), rgb(0x2bc5ad), rgb(0xff0099),
            rgb(0xff9900) };

    private GameConnector connection;

    private User currentUser, oponente;

    private BatallaRoom room;

    private ImageSprite cursor;

    private boolean mustDisconnect;

    // me toca jugar
    private boolean miTurno;

    // jugando o poniendo barcos
    private boolean enJuego;

    // el momento en que tengo que abandonar
    long timeToGo;

    // lo que falta para que me rajen
    int tiempoRestante;

    private TableroGroup miTablero, tableroOtro;

    private InGameChatArea chatArea;

    private TextField chatTF;

    private Button sendChat, abandonGame, disableSounds;

    private Button nuevoJuegoSi, nuevoJuegoNo;

    private int colorYo, colorOtro;

    private CoreFont din13, din13white, din24, din30;

    private ImageSprite cartelInicio;

    private Button empezarAJugar;

    private Label turno, timerLabel;

    private Label finalLabel, finalLabel2;

    private Sound haha, beep;

    // la celda donde dispare
    private Celda cellShot;

    // las celdas donde dispare
    private Collection<Celda> cellShots;

    private Group hundidos;

    private static Map<Barco, Point> posHundidoMio;

    private static Map<Barco, Point> posHundidoOtro;

    static {
        posHundidoMio = new HashMap<Barco, Point>();
        posHundidoMio.put(Barco.BOTE, new Point(50, 355));
        posHundidoMio.put(Barco.CRUCERO, new Point(140, 355));
        posHundidoMio.put(Barco.SUBMARINO, new Point(180, 385));
        posHundidoMio.put(Barco.ACORAZADO, new Point(50, 385));
        posHundidoMio.put(Barco.PORTAAVIONES, new Point(80, 420));

        posHundidoOtro = new HashMap<Barco, Point>();
        posHundidoOtro.put(Barco.BOTE, new Point(300, 355));
        posHundidoOtro.put(Barco.CRUCERO, new Point(390, 355));
        posHundidoOtro.put(Barco.SUBMARINO, new Point(430, 385));
        posHundidoOtro.put(Barco.ACORAZADO, new Point(300, 385));
        posHundidoOtro.put(Barco.PORTAAVIONES, new Point(330, 420));
    }

    public BatallaScene(GameConnector connection, User usr, BatallaRoom room) {
        super(connection);
        this.connection = connection;
        this.currentUser = usr;
        this.room = room;

        cellShots = new HashSet<Celda>();

        // inject
        connection.setGameHandler(this);
    }

    @Override
    public void load() {
        add(new ImageSprite("imgs/tablero.png", 0, 0));

        din13 = CoreFont.load("imgs/DIN13.font.png");
        din24 = CoreFont.load("imgs/DIN24.font.png").tint(WHITE);
        din30 = CoreFont.load("imgs/DIN30.font.png").tint(WHITE);
        din13white = din13.tint(WHITE);

        // chat box
        chatArea = new InGameChatArea(din13, 511, 98, 190, 323);
        add(chatArea);

        // campo de texto donde se chatea
        chatTF = new TextField(din13, din13white, "", 512, 429, 157, -1);
        add(chatTF);

        // boton para enviar el chat (asociado al ENTER)
        sendChat = new Button(CoreImage.load("imgs/flecha-chat.png").split(3),
                750, 540);
        sendChat.setKeyBinding(Input.KEY_ENTER);
        add(sendChat);

        miTablero = new TableroGroup(24, 101, 227, 202, true);
        add(miTablero);

        tableroOtro = new TableroGroup(269, 101, 227, 202, false);
        add(tableroOtro);

        // mi color
        // colorYo = RANDOM_COLORS[(int) (Math.random() *
        // RANDOM_COLORS.length)];
        // fontYo = din13.tint(colorYo);

        // el otro (si hay)
        for (User otro : room.getPlayers()) {
            if (!otro.equals(currentUser)) {
                oponente = otro;
                drawNames();
                break;
            }
        }

        cartelInicio = new ImageSprite(
                CoreImage.load("imgs/cartel_inicio.png"), 258, 91);
        add(cartelInicio);

        empezarAJugar = new Button(CoreImage.load("imgs/boton-empezar.png")
                .split(3), 355, 265);
        add(empezarAJugar);

        // layer hundidos
        hundidos = new Group();
        addLayer(hundidos);

        // boton abandonar
        abandonGame = new Button(CoreImage.load("imgs/btn-abandonar.png")
                .split(3), 500, 0);
        add(abandonGame);

        // sonidos
        haha = Sound.load("sfx/haha.wav");
        beep = Sound.load("sfx/beep.wav");

        // mute
        disableSounds = new Button(CoreImage.load("imgs/sonidos.png").split(6),
                5, 55, true);
        disableSounds.setSelected(CoreSystem.isMute());
        disableSounds.setPixelLevelChecks(false);
        add(disableSounds);

        nuevoJuegoSi = new Button(CoreImage.load("imgs/btn-si.png").split(3),
                300, 300);
        nuevoJuegoSi.enabled.set(false);
        nuevoJuegoSi.setPixelLevelChecks(false);

        nuevoJuegoNo = new Button(CoreImage.load("imgs/btn-no.png").split(3),
                400, 300);
        nuevoJuegoNo.enabled.set(false);
        nuevoJuegoNo.setPixelLevelChecks(false);

        turno = new Label(din24, "Esperando oponente", 420, 30);

        finalLabel = new Label(din30, "", 0, 230);
        finalLabel.visible.set(false);
        add(finalLabel);
        finalLabel2 = new Label(din30, "", 0, 260);
        finalLabel2.visible.set(false);
        add(finalLabel2);

        // animo el alpha para que titile
        Timeline alphaCycle = new Timeline();
        int dur = 1000;
        alphaCycle.animate(turno.alpha, 255, 0, dur, Easing.NONE, 0);
        alphaCycle.animate(turno.alpha, 0, 255, dur, Easing.NONE, dur);
        alphaCycle.loopForever();
        addTimeline(alphaCycle);
        add(turno);

        // timer (en un nuevo layer para estar encima de todo)
        timerLabel = new Label(din13, "", 0, 0);
        Group g = new Group();
        g.add(timerLabel);
        addLayer(g);

        CoreImage cursorImg = CoreImage.load("imgs/cursor.png");
        cursorImg.setHotspot(cursorImg.getWidth() / 2,
                cursorImg.getHeight() / 2);
        cursor = new ImageSprite(cursorImg, 0, 0);
        cursor.visible.set(false);
        add(cursor);

        setEnJuego(false);
    }

    public void unload() {
        if (mustDisconnect) {
            connection.disconnect();
        }
    }

    @Override
    public void update(int elapsedTime) {
        super.update(elapsedTime); // PingScene

        int viewX = Input.getMouseX();
        int viewY = Input.getMouseY();

        if (disableSounds.isClicked()) {
            CoreSystem.setMute(disableSounds.isSelected());
        }
        else if (nuevoJuegoSi.enabled.get() && nuevoJuegoSi.isClicked()) {
            nuevoJuegoSi.enabled.set(false);
            remove(nuevoJuegoSi);
            nuevoJuegoNo.enabled.set(false);
            remove(nuevoJuegoNo);

            finalLabel.setText("Esperando respuesta del oponente...");
            finalLabel.visible.set(true);
            finalLabel2.visible.set(false);
            PulpcoreUtils.centerSprite(finalLabel, 235, 319);

            connection.send(new ProximoJuegoMessage(true));

            // corto la ejecucion para que no se realice el disparo
            return;
        }
        else if (nuevoJuegoNo.enabled.get() && nuevoJuegoNo.isClicked()) {
            // aviso que no
            connection.send(new ProximoJuegoMessage(false));

            invokeLater(new Runnable() {
                public void run() {
                    // y me rajo al lobby
                    setScene(new LobbyScene(currentUser, connection));
                }
            });

            // corto la ejecucion para que no se realice el disparo
            return;
        }

        if (enJuego) {
            if (tableroOtro.isMouseOver() && miTurno) {
                tableroOtro.setCursor(Input.CURSOR_OFF);

                cursor.visible.set(true);
                cursor.setLocation(viewX, viewY);
            }
            else {
                cursor.visible.set(false);
                tableroOtro.setCursor(Input.CURSOR_DEFAULT);
            }

            if (miTurno) {
                timeToGo -= elapsedTime;
                // actualizacion del timer
                int t = Math.round(timeToGo / 1000);

                if (t < 0) {
                    abandonGame();
                }
                else if (t != tiempoRestante) {
                    tiempoRestante = t;

                    timerLabel.setText(Integer.toString(t));
                    timerLabel.alpha.set(0xff);

                    if (t >= 10) {
                        timerLabel.x.set(195);
                        timerLabel.y.set(78);
                    }
                    else if (t < 10) {
                        timerLabel.x.set(390);
                        timerLabel.y.set(280);

                        beep.play();

                        timerLabel.alpha.animateTo(0, 500);
                        timerLabel.width.animateTo(100, 500);
                        timerLabel.height.animateTo(100, 500);
                        timerLabel.x.animateTo(timerLabel.x.get() - 50, 500);
                        timerLabel.y.animateTo(timerLabel.y.get() - 50, 500);
                    }
                }

                if (tableroOtro.isMouseReleased()) {
                    cellShot = tableroOtro.getCelda(viewX, viewY);

                    // no dejo disparar dos veces en el mismo lugar
                    if (cellShot != null && !cellShots.contains(cellShot)) {
                        cellShots.add(cellShot);
                        connection.send(new DisparoMessage(cellShot));
                        setMiTurno(false);
                    }
                    else {
                        // XXX avisar que no
                    }
                }
            }
        }
        else {
            if (empezarAJugar.isClicked()) {
                if (miTablero.isValid()) {
                    connection.send(new PosicionesBarcoMessage(miTablero
                            .getBarcos()));
                    setEnJuego(true);

                    remove(cartelInicio);
                    remove(empezarAJugar);
                }
                else {
                    // TODO agregar uno solo
                    add(new PopUp(din13white, " INVALIDO "));
                }
            }
        }

        if (sendChat.isClicked() && chatTF.getText().trim().length() > 0) {
            connection.send(new RoomChatMessage(chatTF.getText()));

            chatArea.addLine(currentUser.getName() + ": " + chatTF.getText());
            chatTF.setText("");
        }
        else if (abandonGame.enabled.get() && abandonGame.isClicked()) {
            abandonGame();
        }
    }

    public void disconnected() {
        invokeLater(new Runnable() {
            public void run() {
                Stage.setScene(new client.DisconnectedScene(Reason.FAILED));
            }
        });
    }

    public void incomingChat(final User from, final String msg) {
        invokeLater(new Runnable() {
            public void run() {
                chatArea.addLine(from.getName() + ": " + msg);
            }
        });
    }

    public void oponenteAbandono(boolean enJuego) {
        if (enJuego) {
            finalLabel.setText("¡Tu oponente abandono!");
        }
        else {
            finalLabel.setText("No quiso jugar otro");
        }

        PulpcoreUtils.centerSprite(finalLabel, 235, 319);
        finalLabel.visible.set(true);
        finalLabel2.visible.set(false);

        nuevoJuegoSi.visible.set(false);
        nuevoJuegoNo.visible.set(false);

        addEvent(new TimelineEvent(2000) {
            @Override
            public void run() {
                setScene(new LobbyScene(currentUser, connection));
            }
        });
    }

    public void roomJoined(AbstractRoom room, User user) {
        if (!user.equals(currentUser)) {
            oponente = user;
            drawNames();
        }
    }

    public void updatePoints(int puntos) {
        // actualizo puntos
        currentUser.setPuntos(puntos);

        invokeLater(new Runnable() {
            public void run() {
                // obligo a contestar o que vuelva al lobby
                setMiTurno(true);

                if (currentUser.getPuntos() >= room.getPuntosApostados()) {
                    add(nuevoJuegoSi);
                    nuevoJuegoSi.enabled.set(true);
                    add(nuevoJuegoNo);
                    nuevoJuegoNo.enabled.set(true);

                    finalLabel2.setText("�Otro partido?");
                    finalLabel2.visible.set(true);
                    PulpcoreUtils.centerSprite(finalLabel2, 235, 319);
                }
                else {
                    // no me alcanza para jugar otro

                    // aviso que no
                    connection.send(new ProximoJuegoMessage(false));

                    // y me rajo al lobby
                    setScene(new LobbyScene(currentUser, connection));
                }
            }
        });
    }

    private final void setScene(final Scene s) {
        mustDisconnect = false;
        Stage.setScene(s);
    }

    private void drawNames() {
        invokeLater(new Runnable() {
            public void run() {
                int MAX_SIZE = 10;

                String yo = currentUser.getName().toUpperCase();
                if (yo.length() > MAX_SIZE) {
                    yo = yo.substring(0, MAX_SIZE);
                }
                String otro = oponente.getName().toUpperCase();
                if (otro.length() > MAX_SIZE) {
                    otro = otro.substring(0, MAX_SIZE);
                }

                do {
                    // uno distinto!
                    colorOtro = RANDOM_COLORS[(int) (Math.random() * RANDOM_COLORS.length)];
                }
                while (colorOtro == colorYo);

                // fontOtro =
                // CoreFont.load("imgs/DIN13.font.png").tint(colorOtro);

                CoreFont din30 = CoreFont.load("imgs/DIN30.font.png");

                // yo vs otro
                int x = 240;
                int y = 10;

                Label labelYo = new Label(din30.tint(colorYo), yo, x, y);
                x += labelYo.width.getAsInt();

                Label labelVs = new Label(din30.tint(WHITE), " vs ", x, y);
                x += labelVs.width.getAsInt();

                Label labelOtro = new Label(din30.tint(colorOtro), otro, x, y);

                add(labelYo);
                add(labelVs);
                add(labelOtro);
            }
        });
    }

    private void abandonGame() {
        // envio abandono
        connection.send(new AbandonRoomMessage());

        invokeLater(new Runnable() {
            public void run() {
                // me rajo al lobby
                setScene(new LobbyScene(currentUser, connection));
            }
        });
    }

    private void setEnJuego(boolean enJuego) {
        this.enJuego = enJuego;
        miTablero.setEnJuego(enJuego);
        tableroOtro.setEnJuego(enJuego);
    }

    private void setMiTurno(boolean miTurno) {
        if (miTurno) {
            turno.setText("Te toca");

            if (!this.miTurno) {
                // no era mi turno y ahora es
                // FIXME avisar sonoramente
                // teToca[(int) (Math.random() * teToca.length)].play();
            }

            // en 30s abandonamos
            timeToGo = 30 * 1000;
        }
        else {
            turno.setText("Esperando jugada");
        }

        this.miTurno = miTurno;
    }

    // /////////////////
    // GameHandler
    // /////////////////
    public void newGame() {
        setEnJuego(false);
        setMiTurno(false);

        // reseteo tableros
        miTablero.reset();
        tableroOtro.reset();
        hundidos.removeAll();

        // habilito tablero enemigo
        tableroOtro.enabled.set(true);

        // reseteo variables
        cellShots.clear();

        invokeLater(new Runnable() {
            public void run() {
                add(cartelInicio);
                add(empezarAJugar);

                turno.setText("Esperando oponente");

                empezarAJugar.update(0);
                nuevoJuegoNo.update(0);
                nuevoJuegoSi.update(0);

                finalLabel.visible.set(false);
                finalLabel2.visible.set(false);

                // visibilizo y habilito el boton de abandonar
                abandonGame.visible.set(true);
                abandonGame.enabled.set(true);
            }
        });
    }

    public void startGame(final boolean start) {
        invokeLater(new Runnable() {
            public void run() {
                setMiTurno(start);
            }
        });
    }

    public void disparoEnemigo(final Celda cell, final Resultado res) {
        invokeLater(new Runnable() {
            public void run() {
                if (res == Resultado.AGUA) {
                    setMiTurno(true);
                    miTablero.agua(cell);
                }
                else {
                    Barco barco = miTablero.tocado(cell);

                    if (res == Resultado.HUNDIDO) {
                        // agregar a la lista de hundidos
                        Point p = posHundidoMio.get(barco);
                        hundidos.add(new ImageSprite(BarcoSprite
                                .getImages(barco)[0], p.x, p.y));
                    }
                }
            }
        });
    }

    public void agua() {
        invokeLater(new Runnable() {
            public void run() {
                tableroOtro.agua(cellShot);
            }
        });
    }

    public void tocado() {
        invokeLater(new Runnable() {
            public void run() {
                tableroOtro.tocado(cellShot);
                setMiTurno(true);
            }
        });
    }

    public void hundido(final Barco barco, final Posicion pos) {
        invokeLater(new Runnable() {
            public void run() {
                tableroOtro.tocado(cellShot);

                BarcoSprite bs = new BarcoSprite(barco, pos);
                tableroOtro.add(bs);
                tableroOtro.moveToBottom(bs);

                // agregar a la lista de hundidos
                Point p = posHundidoOtro.get(barco);
                hundidos.add(new ImageSprite(BarcoSprite.getImages(barco)[0],
                        p.x, p.y));

                setMiTurno(true);
            }
        });
    }

    public void finJuego(final boolean victoria) {
        invokeLater(new Runnable() {
            public void run() {
                if (victoria) {
                    // gane
                    finalLabel.setText("¡Ganaste! Sos groso");
                }
                else {
                    // perdi
                    finalLabel.setText("¡Perdiste, sos un nabo!");
                    haha.play();
                }

                finalLabel.visible.set(true);
                PulpcoreUtils.centerSprite(finalLabel, 235, 319);

                // invisibilizo y deshabilito el boton de abandonar
                abandonGame.visible.set(false);
                abandonGame.enabled.set(false);

                // deshabilito tablero enemigo
                tableroOtro.enabled.set(false);

                // espero a que lleguen los puntos
                setMiTurno(false);

                // cambio texto del cartel
                turno.setText("Actualizando puntos");
            }
        });
    }

    public void oponenteAbandono(final boolean enJuego, User user) {
        invokeLater(new Runnable() {
            public void run() {
                if (enJuego) {
                    finalLabel.setText("¡Tu oponente abandono!");
                }
                else {
                    finalLabel.setText("No quiso jugar otro");
                }
                PulpcoreUtils.centerSprite(finalLabel, 235, 319);
                finalLabel.visible.set(true);
                finalLabel2.visible.set(false);

                // TODO boton ACK

                // back to lobby
                setScene(new LobbyScene(currentUser, connection));
            }
        });
    }
}

// TODO cuando no es tu turno que saque el cursor mira
// TODO sonidos
