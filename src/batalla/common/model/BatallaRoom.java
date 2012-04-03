package batalla.common.model;

import java.util.List;

import common.model.TwoPlayerRoom;
import common.model.User;

public class BatallaRoom extends TwoPlayerRoom {
    public BatallaRoom() {
    }

    public BatallaRoom(int id, int puntosApostados, List<User> players) {
        super(id, puntosApostados, players);
    }
}