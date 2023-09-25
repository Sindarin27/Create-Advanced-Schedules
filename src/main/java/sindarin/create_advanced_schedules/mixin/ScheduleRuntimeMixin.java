package sindarin.create_advanced_schedules.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sindarin.create_advanced_schedules.schedule.AdvanceToInstruction;
import sindarin.create_advanced_schedules.schedule.LabelInstruction;

import java.util.HashMap;
import java.util.List;

@Mixin(value = ScheduleRuntime.class, remap = false)
public abstract class ScheduleRuntimeMixin {
    @Shadow
    Schedule schedule;
    @Shadow
    public int currentEntry;
    @Shadow
    public ScheduleRuntime.State state;
    @Shadow
    Train train;
    @Final
    @Shadow
    static int INTERVAL;
    @Shadow
    int cooldown;
    @Shadow
    List<Integer> conditionProgress;
    @Shadow
    List<CompoundTag> conditionContext;


    @Shadow public abstract void tickConditions(Level level);

    @Unique
    private HashMap<String, Integer> create_advanced_schedules$labelEntries = new HashMap<>();

    @Inject(method = "startCurrentInstruction", at = @At(value = "HEAD"), cancellable = true)
    private void startCurrentInstruction(CallbackInfoReturnable<GlobalStation> cir) {
        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof AdvanceToInstruction advance) {
            String targetLabel = advance.getTargetLabel();
            int targetEntry = create_advanced_schedules$labelEntries.getOrDefault(targetLabel, -1);

            if (targetEntry != -1) {
                currentEntry = targetEntry;
                state = ScheduleRuntime.State.PRE_TRANSIT;
            } else {
                train.status.displayInformation("no_label", false);
                cooldown = INTERVAL;
            }
            cir.setReturnValue(null);
        }

        if (instruction instanceof LabelInstruction) {
            state = ScheduleRuntime.State.PRE_TRANSIT;
            currentEntry++;
            cir.setReturnValue(null);
        } // Label does nothing
    }

    @Inject(method = "setSchedule", at = @At(value = "TAIL"))
    private void storeScheduleLabels(Schedule schedule, boolean auto, CallbackInfo ci) {
        for (int i = 0; i < schedule.entries.size(); i++) {
            if (schedule.entries.get(i).instruction instanceof LabelInstruction label) {
                create_advanced_schedules$labelEntries.put(label.getLabel(), i);
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;tickConditions(Lnet/minecraft/world/level/Level;)V", remap = false))
    private void OnTickConditions(ScheduleRuntime instance, Level level) {
        if (schedule.entries.get(currentEntry).instruction instanceof AdvanceToInstruction advance) {
            boolean success = tickConditionsInstantFeedback(level);
            
        }
        else instance.tickConditions(level);
        
    }
    
    public boolean tickConditionsInstantFeedback(Level level) {
        List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
        for (int i = 0; i < conditions.size(); i++) {
            CompoundTag tag = conditionContext.get(i);
            List<ScheduleWaitCondition> list = conditions.get(i);
            
            boolean passedAll = true;
            
            for (ScheduleWaitCondition condition : list) {
                boolean completed = condition.tickCompletion(level, train, tag);
                if (completed) {
                    conditionContext.set(i, new CompoundTag());
                }
                else {
                    passedAll = false;
                    break;
                }
            }
            if (passedAll) return true;
        }
        return false;
    }
}
