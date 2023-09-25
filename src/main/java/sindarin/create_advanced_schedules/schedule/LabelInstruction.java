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

public class LabelInstruction extends TextScheduleInstruction {
    @Override
    public boolean supportsConditions() {
        return false;
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(Items.CYAN_BANNER.getDefaultInstance(), Components.literal(getLabelText()));
    }

    @Override
    public ResourceLocation getId() {
        return AdvancedSchedules.asResource("label");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return Items.CYAN_BANNER.getDefaultInstance();
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

    public String getLabel() {
        return getLabelText();
    }
}
