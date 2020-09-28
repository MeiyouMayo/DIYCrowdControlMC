package tv.twitch.meiyoumayo.knockoffcrowdcontrolmc.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class dealing with entities
 */
public final class Entity {
    /**
     * Retrieves an entity by its name
     * @param entityName the string name of entity (title case)
     * @return an optional of the registry entry produced by searching for the first entity that exactly matches this name
     */
    public static Optional<Map.Entry<ResourceLocation, EntityType<?>>> getEntityByName(String entityName){
        return ForgeRegistries.ENTITIES
                .getEntries()
                .stream()
                .filter(entry ->
                        entry.getValue()
                                .getName()
                                .getString()
                                .matches(entityName)
                )
                .findFirst();
    }

    /**
     * Spawns the entity type in the world at the given block
     * @param entityType the type of entity to spawn
     * @param world the world to spawn the entity in
     * @param blockPos where to spawn the entity
     * @return the entity spawned
     */
    public static net.minecraft.entity.Entity spawnEntity(EntityType<?> entityType, World world, BlockPos blockPos){
        return entityType.spawn(
                world,
                null,
                null,
                null,
                blockPos,
                SpawnReason.COMMAND,
                true,
                false
        );
    }
}
