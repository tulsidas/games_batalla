package batalla.client;

import batalla.common.model.BatallaRoom;
import pulpcore.image.Colors;
import pulpcore.scene.Scene;
import pulpcore.sprite.FilledSprite;
import pulpcore.sprite.Sprite;
import client.AbstractGameConnector;
import client.AbstractLobbyScene;

import common.model.AbstractRoom;
import common.model.User;

public class LobbyScene extends AbstractLobbyScene {

   public LobbyScene(User user, AbstractGameConnector connection) {
      super(user, connection);
   }

   @Override
   protected Scene getGameScene(AbstractGameConnector connection, User usr,
         AbstractRoom room) {
      return new BatallaScene((GameConnector) connection, usr,
            (BatallaRoom) room);
   }

   @Override
   protected Sprite getGameImage() {
      // FIXME
      return new FilledSprite(10, 10, 10, 10, Colors.BLACK);
   }
}
