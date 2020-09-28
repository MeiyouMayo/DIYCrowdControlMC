package tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.rest;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.halt;
import static tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.util.Communication.messagePlayer;
import static tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.util.Entity.getEntityByName;
import static tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.util.Entity.spawnEntity;
import static tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.util.Game.adjustHearts;

/**
 * Listens to a KnockOffCrowdControl Rest Resource. Must have logic for all commands in its enum
 */
public class RestListener {

    private static final String QUERY_MESSAGE = "message";
    private static final String QUERY_ARGS = "args";

    /**
     * The MC server this object modifies
     */
    private MinecraftServer server;

    protected RestListener(@Nullable MinecraftServer server){
        this.restInit();
    }

    public RestListener(){
        this(null);
    }

    /**
     * Sets up all REST resources
     */
    private void restInit(){

        get("/command/:command", (req, res) -> {
            String commandString = req.params("command");

            String message = req.queryParamOrDefault(QUERY_MESSAGE, "no message");

            String args = req.queryParamOrDefault(QUERY_ARGS, "no args");
            //Try to parse command
            try{
                Command command = Command.valueOf(commandString);
                command.action(getPlayer(this.server), message + commandString+ "with args" + args, args);
                res.status(200);
                return message + commandString;
            }
            catch(Error e){
                halt(404, "not a command bruvh");
                return null;
            }
        });
    }

    private static PlayerEntity getPlayer(MinecraftServer server){
        return server.getPlayerList().getPlayers().get(0);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    /**
     * All possible commands to be used on player
     */
    private enum Command{
        JUMP {
            public void action(PlayerEntity player, String message, String ... args){
                player.jump();
                messagePlayer(player, message);
            }
        },
        KILL {
            public void action(PlayerEntity player, String message, String ... args){
                player.setHealth(0);
                messagePlayer(player, message);
            }
        },
        GIVE_HEART {
            public void action(PlayerEntity player, String message, String ... args){
                player.setHealth(adjustHearts(player.getHealth(), 1));
                messagePlayer(player, message);
            }
        },
        TAKE_HEART {
            public void action(PlayerEntity player, String message, String ... args){
                player.setHealth(adjustHearts(player.getHealth(), -1));
                messagePlayer(player, message);
            }
        },
        SPAWN {
            public void action(PlayerEntity player, String message, String ... args){
                World world = player.world;
                messagePlayer(player, player.world.getDimension().getType().toString());

                Optional<Map.Entry<ResourceLocation, EntityType<?>>> toSpawnOpt = getEntityByName(args[0]);

                if(!toSpawnOpt.isPresent()){
                    messagePlayer(player, "couldn't find "+ args[0]);
                }
                else {
                    spawnEntity(toSpawnOpt.get().getValue(), world, new BlockPos(player.posX + 1, player.posY + 1, player.posZ + 1));
                    messagePlayer(player, message);
                }
            }
        }
        ;

        /**
         * The action performed by this enum on the player
         * @param player the player to have an action done on
         * @param message the message to send the player when doing this action
         * @param args optional arguments
         */
        abstract void action(PlayerEntity player, String message, String ... args);



    }


}
