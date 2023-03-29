package ru.dargen.evoplus.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import ru.dargen.evoplus.mixins.BossBarHudAccessor;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class Util {

    //    public final MethodHandles.Lookup LOOKUP = getLookup();
    public final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[\\dA-FK-ORX]");

    public MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public Window getWindow() {
        return getClient() == null ? null : getClient().getWindow();
    }

    public Screen getCurrentScreen() {
        return getClient() == null ? null : getClient().currentScreen;
    }

    public ClientPlayerEntity getPlayer() {
        return getClient() == null ? null : getClient().player;
    }

    public World getWorld() {
        return getClient() == null ? null : getClient().world;
    }

    public Scoreboard getScoreboard() {
        return getWorld() == null ? null : getWorld().getScoreboard();
    }

    public ScoreboardObjective getSidebarObjective() {
        return getScoreboard() == null ? null : getScoreboard().getObjectiveForSlot(1);
    }

    public List<String> getSidebarScores() {
        val sidebar = getSidebarObjective();
        val scoreboard = getScoreboard();
        if (sidebar == null) return Collections.emptyList();
        val scores = new LinkedList<>(scoreboard.getAllPlayerScores(sidebar));
        scores.sort(Comparator.comparingInt(ScoreboardPlayerScore::getScore));

        return scores.stream()
                .filter(score -> score.getObjective().getName().equals(sidebar.getName()))
                .map(score -> score.getPlayerName() + getSuffixFromContainingScoreboardTeam(scoreboard, score.getPlayerName()))
                .collect(Collectors.toList());
    }

    private String getSuffixFromContainingScoreboardTeam(Scoreboard scoreboard, String member) {
        String suffix = null;
        for (Team team : scoreboard.getTeams()) {
            if (team.getPlayerList().contains(member)) {
                suffix = team.getPrefix().getString() + team.getSuffix().getString();
                break;
            }
        }
        return (suffix == null ? "" : suffix);
    }

    public Map<UUID, ClientBossBar> getBossBarInfos() {
        return getClient() == null || getClient().inGameHud == null ?
                Collections.emptyMap() : ((BossBarHudAccessor) getClient().inGameHud.getBossBarHud()).getInfos();
    }

    public boolean isOpenedScreen() {
        return getCurrentScreen() != null;
    }

    public boolean isKeyPressed(int keyCode) {
        return getWindow() != null && InputUtil.isKeyPressed(getWindow().getHandle(), keyCode);
    }

    public String getName() {
        return getClient().getSession().getUsername();
    }

    public int getWidth() {
        return getClient().getWindow().getScaledWidth();
    }

    public int getHeight() {
        return getClient().getWindow().getScaledHeight();
    }

    public static int rgb(int r, int g, int b) {
        return rgb(r, g, b, 255);
    }

    public static int rgb(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    public void printMessage(String text) {
        if (getPlayer() != null) getPlayer().sendMessage(Text.of(text), false);
    }

    public void sendMessage(String text) {
        if (getPlayer() != null) getPlayer().networkHandler.sendChatMessage(text);
    }

    public String stripColor(String input) {
        if (input == null)
            return null;

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public void sendPacket(Packet<ServerPlayPacketListener> packet) {
        if (Util.getPlayer() != null)
            Util.getPlayer().networkHandler.sendPacket(packet);
    }

    public boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

//    @SneakyThrows
//    private MethodHandles.Lookup getLookup() {
//        val lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//        lookupField.setAccessible(true);
//        return (MethodHandles.Lookup) lookupField.get(null);
//    }
}
