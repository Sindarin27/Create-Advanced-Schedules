package sindarin.create_advanced_schedules.mixin;

import com.ibm.icu.impl.UResource;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sindarin.create_advanced_schedules.schedule.AdvanceToInstruction;
import sindarin.create_advanced_schedules.schedule.LabelInstruction;

@Mixin(value = ScheduleRuntime.class, remap = false)
public class ScheduleRuntimeMixin {
    @Shadow 
    Schedule schedule;
    @Shadow
    public int currentEntry;
    
    @Inject(method = "startCurrentInstruction", at = @At(value = "HEAD"), cancellable = true)
    private void startCurrentInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;
        
        if (instruction instanceof AdvanceToInstruction advance) {

        }
        
        if (instruction instanceof LabelInstruction label) cir.setReturnValue(null); // Label does nothing
    }
}
