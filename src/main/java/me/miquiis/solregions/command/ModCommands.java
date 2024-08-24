package me.thepond.solregions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.thepond.solregions.ModPackets;
import me.thepond.solregions.SOLRegions;
import me.thepond.solregions.Scheduler;
import me.thepond.solregions.TextureManager;
import me.thepond.solregions.data.Region;
import me.thepond.solregions.data.RegionData;
import me.thepond.solregions.data.RegionDataManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModCommands implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("sol").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("create").then(CommandManager.argument("name", StringArgumentType.string()).then(CommandManager.argument("description", StringArgumentType.string()).executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    String description = StringArgumentType.getString(context, "description");
                    SOLRegions.LOGGER.info("Creating new sign of life with name: " + name + " and description: " + description);
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    regionData.addOrUpdateRegion(new Region(name, description));
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Created new sign of life with name: " + name + " and description: " + description);
                    }, true);
                    return 1;
                }))))
                .then(CommandManager.literal("start").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);

                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    SOLRegions.LOGGER.info("Starting tracing vertices for region with name: " + name);

                    ServerPlayerEntity player = context.getSource().getPlayer();

                    ItemStack regionSelectorItem = new ItemStack(Items.GOLDEN_AXE);
                    regionSelectorItem.setCustomName(Text.literal("\u00A7e\u00A7lRegion Selector"));
                    NbtCompound nbt = regionSelectorItem.getOrCreateNbt();
                    nbt.putString("region", name);

                    if (!player.getInventory().contains(regionSelectorItem)) {
                        player.giveItemStack(regionSelectorItem);
                    }

                    if (!region.getRegionVertices().isEmpty()) {
                        region.getEditingRegionVertices().clear();
                        region.getEditingRegionVertices().addAll(region.getRegionVertices());
                    }

                    PacketByteBuf byteBuf = PacketByteBufs.create();
                    byteBuf.writeBoolean(true);
                    byteBuf.writeNbt(region.toNbt());
                    ServerPlayNetworking.send(player, ModPackets.EDIT_REGION, byteBuf);

                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Right click blocks to add vertices to region: " + name);
                    }, true);

                    return 1;
                })))
                .then(CommandManager.literal("finish").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }

                    SOLRegions.LOGGER.info("Finishing tracing vertices for region with name: " + name);

                    region.setRegionVertices(new ArrayList<>(region.getEditingRegionVertices()));
                    region.getEditingRegionVertices().clear();
                    regionData.setDirty(true);

                    ServerWorld world = context.getSource().getWorld();
                    region.getRegionVertices().forEach(pos -> {
                        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    });

                    removeRegionSelector(context.getSource().getPlayer());

                    PacketByteBuf byteBuf = PacketByteBufs.create();
                    byteBuf.writeBoolean(false);
                    ServerPlayNetworking.send(context.getSource().getPlayer(), ModPackets.EDIT_REGION, byteBuf);

                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Finished tracing vertices for region: " + name);
                    }, true);
                    return 1;
                })))
                .then(CommandManager.literal("cancel").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);

                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }

                    removeRegionSelector(context.getSource().getPlayer());

                    SOLRegions.LOGGER.info("Cancelling tracing vertices for region with name: " + name);
                    region.getEditingRegionVertices().clear();
                    regionData.setDirty(true);

                    ServerWorld world = context.getSource().getWorld();
                    region.getRegionVertices().forEach(pos -> {
                        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    });

                    PacketByteBuf byteBuf = PacketByteBufs.create();
                    byteBuf.writeBoolean(false);
                    ServerPlayNetworking.send(context.getSource().getPlayer(), ModPackets.EDIT_REGION, byteBuf);
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Cancelled tracing vertices for region: " + name);
                    }, true);
                    return 1;
                })))
                .then(CommandManager.literal("who").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    List<ServerPlayerEntity> players = new ArrayList<>(region.getPlayersInRegion(context.getSource().getWorld()));
                    StringBuilder playersString = new StringBuilder();
                    for (ServerPlayerEntity player : players) {
                        playersString.append(player.getName().getString()).append("\n");
                    }
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Players in region: " + name + "\n" + playersString);
                    }, true);
                    return 1;
                })))
                .then(CommandManager.literal("height").then(getRegionNameSuggestionsArgument().then(CommandManager.argument("minY", IntegerArgumentType.integer()).suggests(getPlayerYLevelSuggestions()).then(CommandManager.argument("maxY", IntegerArgumentType.integer()).suggests(getPlayerYLevelSuggestions()).executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    int minY = IntegerArgumentType.getInteger(context, "minY");
                    int maxY = IntegerArgumentType.getInteger(context, "maxY");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    region.setRegionMinY(minY);
                    region.setRegionMaxY(maxY);
                    regionData.setDirty(true);
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Region with name: " + name + " now has minY: " + minY + " and maxY: " + maxY);
                    }, true);
                    return 1;
                })))))
                .then(CommandManager.literal("info").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    StringBuilder vertices = new StringBuilder();
                    for (int i = 0; i < region.getRegionVertices().size(); i++) {
                        vertices.append(region.getRegionVertices().get(i).toShortString()).append("\n");
                    }

                    PacketByteBuf byteBuf = PacketByteBufs.create();
                    byteBuf.writeCollection(region.getRegionVertices(), PacketByteBuf::writeBlockPos);
                    ServerPlayNetworking.send(context.getSource().getPlayer(), ModPackets.HIGHLIGHT_BLOCKS, byteBuf);

                    Scheduler.schedule(20 * 5, () -> {
                        ServerWorld world = context.getSource().getWorld();
                        region.getRegionVertices().forEach(pos -> {
                            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        });
                    });

                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Region with name: " + name + " has description: " + region.getRegionDescription() + " minY: " + region.getRegionMinY() + " maxY: " + region.getRegionMaxY() + "\nVertices:\n" + vertices);
                    }, true);
                    return 1;
                })))
                .then(CommandManager.literal("delete").then(getRegionNameSuggestionsArgument().executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    regionData.removeRegion(name);
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Deleted region with name: " + name);
                    }, true);
                    return 1;
                })))
                .then(CommandManager.literal("list").executes(context -> {
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    StringBuilder regions = new StringBuilder();
                    for (Region region : regionData.getRegions()) {
                        regions.append(region.getRegionName()).append("\n");
                    }
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Regions:\n" + regions);
                    }, true);
                    return 1;
                }))
                .then(CommandManager.literal("reload").executes(context -> {
                    TextureManager.loadTextures();

                    PacketByteBuf byteBuf = TextureManager.createCheckBannerPacket();
                    context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player -> {
                        ServerPlayNetworking.send(player, ModPackets.CHECK_BANNER_TEXTURES, byteBuf);
                    });

                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Reloaded textures!");
                    }, true);
                    return 1;
                }))
                .then(CommandManager.literal("showBannerName").then(getRegionNameSuggestionsArgument().then(CommandManager.argument("show", BoolArgumentType.bool()).executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    boolean show = BoolArgumentType.getBool(context, "show");
                    RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
                    Region region = regionData.getRegion(name);
                    if (region == null) {
                        SOLRegions.LOGGER.info("Region with name: " + name + " not found");
                        context.getSource().sendFeedback(() -> {
                            return Text.literal("Region with name: " + name + " not found");
                        }, true);
                        return 0;
                    }
                    region.setShowBannerName(show);
                    regionData.setDirty(true);
                    context.getSource().sendFeedback(() -> {
                        return Text.literal("Region with name: " + name + " now has showBannerName: " + show);
                    }, true);
                    return 1;
                }))))
        );
    }

    private SuggestionProvider<ServerCommandSource> getPlayerYLevelSuggestions() {
        return (context, builder) -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            builder.suggest(player.getBlockPos().getY());
            return builder.buildFuture();
        };
    }

    private RequiredArgumentBuilder<ServerCommandSource, String> getRegionNameSuggestionsArgument() {
        return CommandManager.argument("name", StringArgumentType.string()).suggests((context, builder) -> {
            RegionData regionData =  RegionDataManager.getRegionData(context.getSource().getServer());
            for (Region region : regionData.getRegions()) {
                builder.suggest("\"" + region.getRegionName() +"\"");
            }
            return builder.buildFuture();
        });
    }

    private void removeRegionSelector(ServerPlayerEntity player) {
        try {
            player.getInventory().remove(itemStack -> itemStack.hasNbt() && itemStack.getNbt().contains("region"), 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
