package schedule;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.schedule.destination.TextScheduleInstruction;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import sindarin.create_advanced_schedules.AdvancedSchedules;

import java.util.List;

public class AdvanceToInstruction extends TextScheduleInstruction {
    @Override
    public boolean supportsConditions() {
        return true;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(Items.ENDER_PEARL.getDefaultInstance(), Components.literal(getLabelText()));
    }

    @Override
    public ResourceLocation getId() {
        return AdvancedSchedules.asResource("advance");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return Items.ENDER_PEARL.getDefaultInstance();
    }

    @Nullable
    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(Lang.translateDirect("schedule.instruction.marker_edit_box"));
    }
}
