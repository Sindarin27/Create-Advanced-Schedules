package sindarin.create_advanced_schedules.schedule;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.schedule.destination.TextScheduleInstruction;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import sindarin.create_advanced_schedules.AdvancedSchedules;
import sindarin.create_advanced_schedules.Lang;

import java.util.List;

public class AdvanceToInstruction extends TextScheduleInstruction {
    @Override
    public boolean supportsConditions() {
        return false;
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

    // Override due to improper namespace otherwise
    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(Lang.translateDirect("schedule." + type + "." + getId().getPath() + ".summary")
                .withStyle(ChatFormatting.GOLD), Lang.translateDirect("generic.in_quotes", Components.literal(getLabelText())));
    }
    
    public String getTargetLabel() {
        return getLabelText();
    }
}
