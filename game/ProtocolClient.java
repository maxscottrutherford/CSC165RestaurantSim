package game;

import tage.networking.client.GameConnectionClient;
import tage.networking.IGameConnection.ProtocolType;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import org.joml.Vector3f;

public class ProtocolClient extends GameConnectionClient {
    private game gameInstance;
    private UUID id;
    private GhostManager ghostManager;

    public ProtocolClient(InetAddress remAddr, int remPort, ProtocolType pType, game g) throws IOException {
        super(remAddr, remPort, pType);
        this.gameInstance = g;
        this.id = UUID.randomUUID();
        this.ghostManager = g.getGhostManager();
    }

    @Override
    protected void processPacket(Object msg) {
        String strMessage = (String) msg;
        String[] tokens = strMessage.split(",");
        //debugging
        System.out.println("Received packet: " + msg);

        if (tokens[0].equals("join")) {
            if (tokens[1].equals("success")) {
                gameInstance.setIsConnected(true);

                //debugging
                System.out.println("Sending CREATE after join");

                sendCreateMessage(gameInstance.getPlayer().getWorldLocation());
            } else {
                gameInstance.setIsConnected(false);
            }
        }

        else if (tokens[0].equals("bye")) {
            UUID ghostID = UUID.fromString(tokens[1]);
            ghostManager.removeGhostAvatar(ghostID);
        }

        else if (tokens[0].equals("create")) {
            UUID ghostID = UUID.fromString(tokens[1]);
        
            if (ghostID.equals(id)) return;
        
            Vector3f pos = new Vector3f(
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4])
            );
        
            try {
                ghostManager.createGhost(ghostID, pos);
            } catch (IOException e) {
                System.out.println("Failed to create ghost avatar (create)");
            }
        }

        else if (tokens[0].equals("dsfr")) {
            UUID ghostID = UUID.fromString(tokens[1]); 
        
            if (ghostID.equals(id)) return;
        
            Vector3f pos = new Vector3f(
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4])
            );
        
            try {
                ghostManager.createGhost(ghostID, pos);
            } catch (IOException e) {
                System.out.println("Failed to create ghost avatar (dsfr)");
            }
        }
        
        

        else if (tokens[0].equals("move")) {
            UUID ghostID = UUID.fromString(tokens[1]);
            Vector3f pos = new Vector3f(
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3]),
                Float.parseFloat(tokens[4])
            );
            ghostManager.updateGhostAvatar(ghostID, pos);
        }

        else if (tokens[0].equals("wsds")) {
            UUID remoteID = UUID.fromString(tokens[1]);
        
            //to prevent duplicate ghosts
            if (remoteID.equals(id)) return;
        
            Vector3f myPos = gameInstance.getPlayer().getWorldLocation();
            System.out.println("Responding to WSDS: sending DSFR to " + remoteID);
            sendDetailsForMessage(remoteID, myPos);
        }
        
    }

    public void sendJoinMessage() {
        try {
            sendPacket("join," + id.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCreateMessage(Vector3f pos) {
        try {
            String msg = "create," + id + "," + pos.x + "," + pos.y + "," + pos.z;
            System.out.println("Sending CREATE message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMoveMessage(Vector3f pos) {
        try {
            String msg = "move," + id + "," + pos.x + "," + pos.y + "," + pos.z;
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendByeMessage() {
        try {
            sendPacket("bye," + id.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDetailsForMessage(UUID remoteID, Vector3f pos) {
        try {
            String msg = "dsfr," + id + "," + remoteID + "," + pos.x + "," + pos.y + "," + pos.z;
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
