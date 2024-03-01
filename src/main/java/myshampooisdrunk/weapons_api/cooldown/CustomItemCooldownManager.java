package myshampooisdrunk.weapons_api.cooldown;

import myshampooisdrunk.weapons_api.weapon.AbstractCustomItem;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.math.MathHelper;

public class CustomItemCooldownManager {
    private final Map<AbstractCustomItem,Entry> entries = Maps.newHashMap();
    private int tick;

    public boolean isCoolingDown(AbstractCustomItem item) {
        return this.getCooldownProgress(item, 0.0f) > 0.0f;
    }

    public float getCooldownProgress(AbstractCustomItem item, float tickDelta) {
        Entry entry = this.entries.get(item);
        if (entry != null) {
            float f = entry.endTick - entry.startTick;
            float g = (float)entry.endTick - ((float)this.tick + tickDelta);
            return MathHelper.clamp(g / f, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            Iterator<Map.Entry<AbstractCustomItem, Entry>> iterator = this.entries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<AbstractCustomItem, Entry> entry = iterator.next();
                if (entry.getValue().endTick > this.tick) continue;
                iterator.remove();
                this.onCooldownUpdate(entry.getKey());
            }
        }
    }

    public void set(AbstractCustomItem item, int duration) {
        this.entries.put(item, new Entry(this.tick, this.tick + duration));
        this.onCooldownUpdate(item, duration);
    }

    public void remove(AbstractCustomItem item) {
        this.entries.remove(item);
        this.onCooldownUpdate(item);
    }

    protected void onCooldownUpdate(AbstractCustomItem item, int duration) {
    }

    protected void onCooldownUpdate(AbstractCustomItem item) {
    }

    static class Entry {
        final int startTick;
        final int endTick;

        Entry(int startTick, int endTick) {
            this.startTick = startTick;
            this.endTick = endTick;
        }
    }
}