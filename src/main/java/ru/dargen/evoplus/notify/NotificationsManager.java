package ru.dargen.evoplus.notify;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.render.HudRenderEvent;
import ru.dargen.evoplus.event.server.DisconnectServerEvent;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.LinkedList;
import java.util.List;

@Getter
public class NotificationsManager {

    private final int SPLIT_Y = 7, SPLIT_X = 7, H_SIZE = 50, W_SIZE = 120;

    protected final List<Notification> notifications = new LinkedList<>();

    public NotificationsManager(EvoPlus mod) {
        mod.getEventBus().register(HudRenderEvent.class, event -> {
            val matrixStack = event.getMatrixStack();
            render(matrixStack, Util.getWidth(), Util.getHeight());
        });
        mod.getEventBus().register(DisconnectServerEvent.class, event -> notifications.clear());
    }

    public Notification notify(Notification.Type type, String name, int duration, String message) {
        val notify = new Notification(type, name, duration, message);
        notify.x = Util.getWidth();
        notify.y = SPLIT_Y;
        notifications.add(notify);
        if (Util.getPlayer() != null && Feature.MISC_FEATURE.getNotifySound().getValue())
            Util.getPlayer().playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1F, 1F);
        return notify;
    }

    public Notification notify(Notification.Type type, String name, int duration) {
        return notify(type, name, duration, null);
    }

    public void remove(Notification notify) {
        notifications.remove(notify);
    }

    //shit
    public void render(MatrixStack matrixStack, int width, int height) {
        int y = SPLIT_Y;
        int w = width;

        for (int i = 0; i < notifications.size(); i++) {
            val notification = notifications.get(i);


            int max = Math.min(width - W_SIZE - SPLIT_X, width - 15 - Render.getStringWidth(notification.name) - SPLIT_X);
            int maxSize = Math.max(W_SIZE, 15 + Render.getStringWidth(notification.name));
            if (notification.message != null) {
                max = Math.min(max, width - 10 - notification.message.length() - SPLIT_X);
                maxSize = Math.max(10 + notification.message.length(), maxSize);
            }

            if (!notification.enabled) {
                if ((notification.x -= 4) <= max) {
                    notification.x = max;
                    notification.enabled = true;
                }
            }

            val delay = notification.getDuration() * 1000;
            val end = notification.getStartTime() + delay;
            val current = System.currentTimeMillis();
            val left = (end - current);

            boolean is = current >= end;

            double percent = notification.enabled && !is ? 1 - left / ((double) delay) : 1;

            if (is && (notification.x += 4) >= w) {
                remove(notification);
                continue;
            }

            if (notification.y < y) {
                notification.y += Math.min(3, y - notification.y);
                if (notification.y > y)
                    notification.y = y;
            }
            if (notification.y > y) {
                notification.y -= Math.min(3, notification.y - y);
                if (notification.y < y)
                    notification.y = y;
            }

            int hsize = 10 + Render.getStringHeight() * (notification.message != null ? 2 : 0);

            if (y <= height) {
                Render.fill(matrixStack, notification.x, notification.y, maxSize, hsize, Util.rgb(56, 149, 198, 150));
                Render.fill(matrixStack, notification.x, notification.y, 4, hsize, notification.type.color);

                Render.fill(matrixStack, notification.x + 4, notification.y, (int) (maxSize * percent), 4, Util.rgb(26, 93, 128, 220));

                Render.drawStringWithShadow(matrixStack, notification.name, notification.x + 10, notification.y + 5, -1);
                if (notification.message != null)
                    Render.drawStringWithShadow(matrixStack, notification.message, notification.x + 15, notification.y + 8 + Render.getStringHeight(), -1);
            }

            y += SPLIT_Y + hsize;
        }
    }


}
