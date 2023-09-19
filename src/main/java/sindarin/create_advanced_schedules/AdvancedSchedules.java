package sindarin.create_advanced_schedules;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

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
    }
}
