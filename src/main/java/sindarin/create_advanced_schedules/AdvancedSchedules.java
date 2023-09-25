package sindarin.create_advanced_schedules;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import sindarin.create_advanced_schedules.schedule.AdvanceToInstruction;
import sindarin.create_advanced_schedules.schedule.LabelInstruction;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AdvancedSchedules.MODID)
public class AdvancedSchedules
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_advanced_schedules";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public AdvancedSchedules()
    {
        Schedule.INSTRUCTION_TYPES.add(Pair.of(asResource("advance"), AdvanceToInstruction::new));
        Schedule.INSTRUCTION_TYPES.add(Pair.of(asResource("label"), LabelInstruction::new));
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
